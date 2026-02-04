package com.captainslog.license

import com.captainslog.data.LicenseProgress
import com.captainslog.database.dao.TripDao
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

/**
 * Calculator for captain's license progress based on local trip data.
 * Implements the same calculation logic as the server-side processor.
 */
class LicenseCalculator @Inject constructor(
    private val tripDao: TripDao
) {

    /**
     * Calculate license progress from completed trips in the local database.
     *
     * Algorithm:
     * 1. Query all completed trips (where endTime is not null)
     * 2. Group trips by date (using startTime)
     * 3. For each day, sum the total hours of all trips
     * 4. Count a day as "sea day" if total hours >= 4.0
     * 5. Calculate progress metrics
     *
     * @return LicenseProgress object with current progress metrics
     */
    suspend fun calculateProgress(): LicenseProgress {
        // Query all completed trips
        val completedTrips = tripDao.getAllTrips()
            .first()
            .filter { it.endTime != null }

        // Group trips by date
        val tripsByDate = mutableMapOf<String, MutableList<com.captainslog.database.entities.TripEntity>>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        for (trip in completedTrips) {
            val dateKey = dateFormat.format(trip.startTime)
            tripsByDate.getOrPut(dateKey) { mutableListOf() }.add(trip)
        }

        // Calculate metrics
        val cutoffDate = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1095) // 3 years = 1095 days
        }.time

        var totalDays = 0
        var totalHours = 0.0
        var daysInLast3Years = 0
        var hoursInLast3Years = 0.0

        val seaDates = mutableListOf<Date>()

        for ((dateStr, tripsOnDay) in tripsByDate) {
            // Calculate total hours for the day
            val dayTotalHours = tripsOnDay.sumOf { trip ->
                val start = trip.startTime.time
                val end = trip.endTime?.time ?: start
                val durationMs = end - start
                durationMs.milliseconds.inWholeHours.toDouble() +
                    (durationMs.milliseconds.inWholeMinutes % 60) / 60.0
            }

            // Check if this qualifies as a sea day (>= 4 hours)
            if (dayTotalHours >= 4.0) {
                totalDays++
                totalHours += dayTotalHours

                val date = dateFormat.parse(dateStr) ?: continue
                seaDates.add(date)

                // Check if in last 3 years
                if (date.after(cutoffDate) || date == cutoffDate) {
                    daysInLast3Years++
                    hoursInLast3Years += dayTotalHours
                }
            }
        }

        // Calculate remaining requirements
        val daysRemaining360 = maxOf(0, 360 - totalDays)
        val daysRemaining90In3Years = maxOf(0, 90 - daysInLast3Years)

        // Calculate average days per month (if we have data)
        val averageDaysPerMonth = if (seaDates.isEmpty()) {
            0.0
        } else {
            seaDates.sort()
            val firstDate = seaDates.first()
            val lastDate = seaDates.last()

            val monthsBetween = calculateMonthsBetween(firstDate, lastDate)
            if (monthsBetween > 0) {
                totalDays.toDouble() / monthsBetween
            } else {
                totalDays.toDouble() // All in one month
            }
        }

        // Calculate estimated completion dates
        val estimatedCompletion360 = if (averageDaysPerMonth > 0 && daysRemaining360 > 0) {
            val monthsNeeded = daysRemaining360 / averageDaysPerMonth
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.MONTH, monthsNeeded.toInt())
            SimpleDateFormat("MMM dd, yyyy", Locale.US).format(calendar.time)
        } else {
            null
        }

        val estimatedCompletion90In3Years = if (averageDaysPerMonth > 0 && daysRemaining90In3Years > 0) {
            val monthsNeeded = daysRemaining90In3Years / averageDaysPerMonth
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.MONTH, monthsNeeded.toInt())
            SimpleDateFormat("MMM dd, yyyy", Locale.US).format(calendar.time)
        } else {
            null
        }

        return LicenseProgress(
            totalDays = totalDays,
            totalHours = totalHours,
            daysInLast3Years = daysInLast3Years,
            hoursInLast3Years = hoursInLast3Years,
            daysRemaining360 = daysRemaining360,
            daysRemaining90In3Years = daysRemaining90In3Years,
            estimatedCompletion360 = estimatedCompletion360,
            estimatedCompletion90In3Years = estimatedCompletion90In3Years,
            averageDaysPerMonth = averageDaysPerMonth
        )
    }

    /**
     * Calculate the number of months between two dates
     */
    private fun calculateMonthsBetween(start: Date, end: Date): Int {
        val startCal = Calendar.getInstance().apply { time = start }
        val endCal = Calendar.getInstance().apply { time = end }

        val years = endCal.get(Calendar.YEAR) - startCal.get(Calendar.YEAR)
        val months = endCal.get(Calendar.MONTH) - startCal.get(Calendar.MONTH)

        return years * 12 + months + 1 // +1 to include both start and end months
    }
}
