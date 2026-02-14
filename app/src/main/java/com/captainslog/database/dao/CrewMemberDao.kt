package com.captainslog.database.dao

import androidx.room.*
import com.captainslog.database.entities.CrewMemberEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CrewMemberDao {
    @Query("SELECT * FROM crew_members WHERE tripId = :tripId")
    fun getCrewForTrip(tripId: String): Flow<List<CrewMemberEntity>>

    @Query("SELECT * FROM crew_members WHERE tripId = :tripId")
    suspend fun getCrewForTripSync(tripId: String): List<CrewMemberEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrewMember(crewMember: CrewMemberEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrewMembers(crewMembers: List<CrewMemberEntity>)

    @Delete
    suspend fun deleteCrewMember(crewMember: CrewMemberEntity)

    @Query("DELETE FROM crew_members WHERE tripId = :tripId")
    suspend fun deleteCrewForTrip(tripId: String)
}
