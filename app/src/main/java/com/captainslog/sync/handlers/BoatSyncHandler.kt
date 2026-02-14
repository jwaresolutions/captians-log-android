package com.captainslog.sync.handlers

import android.util.Log
import com.captainslog.connection.ConnectionManager
import com.captainslog.database.AppDatabase
import com.captainslog.network.models.CreateBoatRequest
import com.captainslog.repository.BoatRepository
import com.captainslog.sync.DataType
import com.captainslog.sync.HandlerSyncResult
import com.captainslog.sync.SyncHandler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BoatSyncHandler @Inject constructor(
    private val boatRepository: BoatRepository,
    private val connectionManager: ConnectionManager,
    private val database: AppDatabase
) : SyncHandler {

    companion object {
        private const val TAG = "BoatSyncHandler"
    }

    override val dataType = DataType.BOATS

    override suspend fun syncFromServer(): HandlerSyncResult {
        return try {
            Log.d(TAG, "Syncing boats from server...")
            val result = boatRepository.syncBoatsFromApi()
            if (result.isFailure) {
                Log.e(TAG, "Failed to sync boats from server: ${result.exceptionOrNull()?.message}")
                HandlerSyncResult(success = false, errors = listOf(result.exceptionOrNull()?.message ?: "Unknown error"))
            } else {
                val count = database.boatDao().getAllBoatsSync().size
                Log.d(TAG, "Successfully synced boats from server, $count boats local")
                HandlerSyncResult(success = true, syncedCount = count)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing boats from server", e)
            HandlerSyncResult(success = false, errors = listOf(e.message ?: "Unknown error"))
        }
    }

    override suspend fun syncToServer(): HandlerSyncResult {
        return try {
            Log.d(TAG, "Syncing boats to server...")
            val result = boatRepository.syncBoatsToApi()
            if (result.isFailure) {
                Log.e(TAG, "Failed to sync boats to server: ${result.exceptionOrNull()?.message}")
                HandlerSyncResult(success = false, errors = listOf(result.exceptionOrNull()?.message ?: "Unknown error"))
            } else {
                Log.d(TAG, "Successfully synced boats to server")
                HandlerSyncResult(success = true)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing boats to server", e)
            HandlerSyncResult(success = false, errors = listOf(e.message ?: "Unknown error"))
        }
    }

    override suspend fun syncEntity(entityId: String): HandlerSyncResult {
        return try {
            val boat = database.boatDao().getBoatById(entityId) ?: return HandlerSyncResult(
                success = false, errors = listOf("Boat not found: $entityId")
            )
            val apiService = connectionManager.getApiService()

            // Check if boat already exists on server by name
            val allBoatsResponse = apiService.getBoats()
            var existingBoat: com.captainslog.network.models.BoatResponse? = null

            if (allBoatsResponse.isSuccessful && allBoatsResponse.body() != null) {
                existingBoat = allBoatsResponse.body()!!.data.find {
                    it.name.equals(boat.name, ignoreCase = true)
                }
            }

            if (existingBoat != null) {
                val updatedBoat = boat.copy(
                    id = existingBoat.id,
                    synced = true,
                    lastModified = java.util.Date()
                )
                database.boatDao().insertBoat(updatedBoat)
                apiService.updateBoat(existingBoat.id, CreateBoatRequest(name = boat.name))
                if (boat.isActive) {
                    apiService.setActiveBoat(existingBoat.id)
                }
                apiService.updateBoatStatus(existingBoat.id, mapOf("enabled" to boat.enabled))
                Log.d(TAG, "Boat merged with existing server boat: ${boat.name}")
            } else {
                val response = apiService.getBoat(entityId)
                if (response.isSuccessful) {
                    apiService.updateBoat(entityId, CreateBoatRequest(name = boat.name))
                    if (boat.isActive) {
                        apiService.setActiveBoat(entityId)
                    }
                    apiService.updateBoatStatus(entityId, mapOf("enabled" to boat.enabled))
                    database.boatDao().markAsSynced(entityId)
                } else {
                    val createResponse = apiService.createBoat(CreateBoatRequest(name = boat.name))
                    if (createResponse.isSuccessful && createResponse.body() != null) {
                        val apiBoat = createResponse.body()!!.data
                        val syncedBoat = boat.copy(
                            id = apiBoat.id,
                            synced = true,
                            lastModified = java.util.Date()
                        )
                        database.boatDao().insertBoat(syncedBoat)
                        if (boat.isActive) {
                            apiService.setActiveBoat(apiBoat.id)
                        }
                        Log.d(TAG, "New boat created on server: ${boat.name}")
                    }
                }
            }

            HandlerSyncResult(success = true, syncedCount = 1)
        } catch (e: Exception) {
            Log.e(TAG, "Error syncing boat entity: $entityId", e)
            HandlerSyncResult(success = false, errors = listOf(e.message ?: "Unknown error"))
        }
    }
}
