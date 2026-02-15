package com.captainslog.sharing

import android.graphics.Bitmap
import com.captainslog.database.entities.CrewMemberEntity
import com.captainslog.database.entities.TripEntity
import com.captainslog.security.SecurePreferences
import com.captainslog.sharing.models.CrewMemberData
import com.captainslog.sharing.models.TripCrewData
import com.captainslog.sharing.models.CrewResponseData
import com.captainslog.sharing.models.TripCrewShareData
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder

/**
 * Generates QR codes for sharing trip crew data between devices.
 */
class TripCrewShareGenerator(
    private val securePreferences: SecurePreferences
) {
    private val gson = Gson()

    /**
     * Generates a QR code bitmap from trip and crew data.
     *
     * @param trip The trip to encode
     * @param boatName The name of the boat for the trip
     * @param crewMembers The list of crew members to include
     * @param size The size of the QR code in pixels (default 512)
     * @return A Bitmap containing the QR code
     */
    fun generateQrBitmap(
        trip: TripEntity,
        boatName: String,
        crewMembers: List<CrewMemberEntity>,
        size: Int = 512
    ): Bitmap {
        val captainName = securePreferences.displayName
            ?: securePreferences.username
            ?: "Captain"

        val shareData = TripCrewShareData(
            origin = "device:${securePreferences.deviceId}",
            timestamp = System.currentTimeMillis(),
            data = TripCrewData(
                tripId = trip.id,
                boatId = trip.boatId,
                boatName = boatName,
                captainName = captainName,
                startTime = trip.startTime.time,
                endTime = trip.endTime?.time,
                waterType = trip.waterType,
                crew = crewMembers.map { member ->
                    CrewMemberData(
                        deviceId = member.deviceId,
                        name = member.displayName,
                        joinedAt = member.joinedAt.time
                    )
                }
            )
        )
        val json = gson.toJson(shareData)
        return BarcodeEncoder().encodeBitmap(json, BarcodeFormat.QR_CODE, size, size)
    }

    /**
     * Parses QR code JSON data back into a TripCrewShareData object.
     * Validates that the type is "crew_join".
     *
     * @param json The JSON string from the QR code
     * @return TripCrewShareData if parsing succeeds and type is valid, null otherwise
     */
    fun parseQrData(json: String): TripCrewShareData? {
        return try {
            val shareData = gson.fromJson(json, TripCrewShareData::class.java)
            if (shareData.type == "crew_join") shareData else null
        } catch (e: Exception) {
            null
        }
    }

    fun generateCrewResponseJson(deviceId: String, displayName: String, tripId: String): String {
        val data = CrewResponseData(
            version = 1,
            origin = "device:$deviceId",
            timestamp = System.currentTimeMillis(),
            tripId = tripId,
            deviceId = deviceId,
            displayName = displayName,
            joinedAt = System.currentTimeMillis()
        )
        return gson.toJson(data)
    }

    fun parseCrewResponse(json: String): CrewResponseData? {
        return try {
            val data = gson.fromJson(json, CrewResponseData::class.java)
            if (data.type == "crew_response") data else null
        } catch (e: Exception) {
            null
        }
    }

    fun generateCrewResponseQrBitmap(deviceId: String, displayName: String, tripId: String, size: Int = 512): Bitmap {
        val json = generateCrewResponseJson(deviceId, displayName, tripId)
        return BarcodeEncoder().encodeBitmap(json, BarcodeFormat.QR_CODE, size, size)
    }
}
