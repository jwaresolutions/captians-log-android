package com.captainslog.pdf

import com.captainslog.database.entities.BoatEntity
import com.captainslog.database.entities.TripEntity
import com.captainslog.security.SecurePreferences
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Aggregates trip and boat data into CG719SFormData for PDF generation.
 * One form per boat.
 */
object CG719SDataAggregator {

    fun aggregate(
        boat: BoatEntity,
        trips: List<TripEntity>,
        prefs: SecurePreferences
    ): CG719SFormData {
        val boatTrips = trips.filter { it.boatId == boat.id }

        // Monthly service: group by month, then by year, count distinct days
        val monthlyService = buildMonthlyService(boatTrips)

        // Boundary classification counts
        var greatLakesDays = 0
        var shorewardDays = 0
        var seawardDays = 0
        for (trip in boatTrips) {
            when (trip.boundaryClassification) {
                "great_lakes" -> greatLakesDays++
                "shoreward" -> shorewardDays++
                "seaward" -> seawardDays++
            }
        }

        // Average hours underway per day
        val avgHours = calculateAverageHoursUnderway(boatTrips)

        // Average distance offshore
        val avgDistance = calculateAverageDistanceOffshore(boatTrips)

        // Bodies of water (distinct, joined)
        val bodiesOfWater = boatTrips
            .mapNotNull { it.bodyOfWater }
            .filter { it.isNotBlank() }
            .distinct()
            .joinToString(", ")

        // Most common role on this boat
        val servedAs = boatTrips
            .groupBy { it.role }
            .maxByOrNull { it.value.size }
            ?.key
            ?.let { formatRole(it) }

        // Propulsion display
        val propulsion = boat.propulsionType?.let { formatPropulsion(it) }

        val totalDays = monthlyService.values.sumOf { yearDaysList ->
            yearDaysList.sumOf { it.days }
        }

        return CG719SFormData(
            lastName = prefs.profileLastName,
            firstName = prefs.profileFirstName,
            middleName = prefs.profileMiddleName,
            referenceNumber = prefs.referenceNumber,
            vesselName = boat.name,
            officialNumber = boat.officialNumber,
            grossTons = boat.grossTons?.let { formatNumber(it) },
            lengthFeet = boat.lengthFeet?.toString(),
            lengthInches = boat.lengthInches?.toString(),
            widthFeet = boat.widthFeet?.toString(),
            widthInches = boat.widthInches?.toString(),
            depthFeet = boat.depthFeet?.toString(),
            depthInches = boat.depthInches?.toString(),
            propulsion = propulsion,
            servedAs = servedAs,
            bodiesOfWater = bodiesOfWater.ifBlank { null },
            monthlyService = monthlyService,
            totalDaysServed = totalDays,
            daysOnGreatLakes = greatLakesDays,
            daysShoreward = shorewardDays,
            daysSeaward = seawardDays,
            averageHoursUnderway = avgHours,
            averageDistanceOffshore = avgDistance,
            ownerLastName = boat.ownerLastName,
            ownerFirstName = boat.ownerFirstName,
            ownerMiddleName = boat.ownerMiddleName,
            ownerStreetAddress = boat.ownerStreetAddress,
            ownerCity = boat.ownerCity,
            ownerState = boat.ownerState,
            ownerZipCode = boat.ownerZipCode,
            ownerEmail = boat.ownerEmail,
            ownerPhone = boat.ownerPhone
        )
    }

    /**
     * Build monthly service map: month (1-12) -> list of (year, days) pairs.
     * Counts distinct calendar days per month/year from trip start times.
     */
    private fun buildMonthlyService(trips: List<TripEntity>): Map<Int, List<YearDays>> {
        // Collect all (year, month, day) triples from trips
        val daySet = mutableSetOf<Triple<Int, Int, Int>>()
        val cal = Calendar.getInstance()

        for (trip in trips) {
            cal.time = trip.startTime
            val startYear = cal.get(Calendar.YEAR)
            val startMonth = cal.get(Calendar.MONTH) + 1 // 1-based
            val startDay = cal.get(Calendar.DAY_OF_MONTH)
            daySet.add(Triple(startYear, startMonth, startDay))

            // If trip spans multiple days, count each day
            if (trip.endTime != null) {
                val tempCal = Calendar.getInstance()
                tempCal.time = trip.startTime
                val endCal = Calendar.getInstance()
                endCal.time = trip.endTime
                while (tempCal.before(endCal)) {
                    tempCal.add(Calendar.DAY_OF_MONTH, 1)
                    if (!tempCal.after(endCal)) {
                        val y = tempCal.get(Calendar.YEAR)
                        val m = tempCal.get(Calendar.MONTH) + 1
                        val d = tempCal.get(Calendar.DAY_OF_MONTH)
                        daySet.add(Triple(y, m, d))
                    }
                }
            }
        }

        // Group by month, then by year, count days
        return daySet
            .groupBy { it.second } // group by month
            .mapValues { (_, triples) ->
                triples
                    .groupBy { it.first } // group by year
                    .map { (year, yearTriples) ->
                        YearDays(year, yearTriples.map { it.third }.distinct().size)
                    }
                    .sortedBy { it.year }
            }
    }

    private fun calculateAverageHoursUnderway(trips: List<TripEntity>): String? {
        val completedTrips = trips.filter { it.endTime != null }
        if (completedTrips.isEmpty()) return null

        val totalHours = completedTrips.sumOf { trip ->
            val durationMs = trip.endTime!!.time - trip.startTime.time
            TimeUnit.MILLISECONDS.toMinutes(durationMs).toDouble() / 60.0
        }
        val totalDays = completedTrips.size
        val avg = totalHours / totalDays
        return "%.1f".format(avg)
    }

    private fun calculateAverageDistanceOffshore(trips: List<TripEntity>): String? {
        val tripsWithDistance = trips.mapNotNull { it.distanceOffshore }
        if (tripsWithDistance.isEmpty()) return null
        val avg = tripsWithDistance.average()
        return "%.1f".format(avg)
    }

    private fun formatRole(role: String): String = when (role) {
        "master" -> "Master"
        "mate" -> "Mate"
        "operator" -> "Operator"
        "deckhand" -> "Deckhand"
        "engineer" -> "Engineer"
        "other" -> "Other"
        else -> role.replaceFirstChar { it.uppercase() }
    }

    private fun formatPropulsion(type: String): String = when (type) {
        "motor" -> "Motor"
        "steam" -> "Steam"
        "gas_turbine" -> "Gas Turbine"
        "sail" -> "Sail"
        "aux_sail" -> "Aux Sail"
        else -> type.replaceFirstChar { it.uppercase() }
    }

    private fun formatNumber(value: Double): String {
        return if (value == value.toLong().toDouble()) {
            value.toLong().toString()
        } else {
            "%.1f".format(value)
        }
    }
}
