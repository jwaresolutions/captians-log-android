package com.captainslog.data

/**
 * Data class representing captain's license progress information
 */
data class LicenseProgress(
    val totalDays: Int,
    val totalHours: Double,
    val daysInLast3Years: Int,
    val hoursInLast3Years: Double,
    val daysRemaining360: Int,
    val daysRemaining90In3Years: Int,
    val estimatedCompletion360: String?,
    val estimatedCompletion90In3Years: String?,
    val averageDaysPerMonth: Double
)

/**
 * Data class representing a sea time day with trip details
 */
data class SeaTimeDay(
    val date: String, // YYYY-MM-DD format
    val totalHours: Double,
    val trips: List<SeaTimeDayTrip>
)

/**
 * Data class representing a trip within a sea time day
 */
data class SeaTimeDayTrip(
    val id: String,
    val boatId: String,
    val startTime: String, // ISO format
    val endTime: String,   // ISO format
    val durationHours: Double
)

/**
 * Data class representing monthly sea time breakdown
 */
data class SeaTimeBreakdown(
    val month: String, // YYYY-MM format
    val days: Int,
    val hours: Double
)