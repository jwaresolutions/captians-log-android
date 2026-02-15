package com.captainslog.pdf

/**
 * Data class representing all fields needed to fill a CG-719S form for one vessel.
 */
data class CG719SFormData(
    // Section I: Applicant Information
    val lastName: String?,
    val firstName: String?,
    val middleName: String?,
    val referenceNumber: String?,
    // SSN intentionally omitted — user fills manually

    // Vessel Info
    val vesselName: String,
    val officialNumber: String?,
    val grossTons: String?,
    val lengthFeet: String?,
    val lengthInches: String?,
    val widthFeet: String?,
    val widthInches: String?,
    val depthFeet: String?,
    val depthInches: String?,
    val propulsion: String?,
    val servedAs: String?,
    val bodiesOfWater: String?,

    // Section II: Monthly service records
    // Map of (month 1-12) -> list of (year, days) pairs
    val monthlyService: Map<Int, List<YearDays>>,

    // Summary fields
    val totalDaysServed: Int,
    val daysOnGreatLakes: Int,
    val daysShoreward: Int,
    val daysSeaward: Int,
    val averageHoursUnderway: String?,
    val averageDistanceOffshore: String?,

    // Section III: Owner/Operator (Page 2)
    val ownerLastName: String?,
    val ownerFirstName: String?,
    val ownerMiddleName: String?,
    val ownerStreetAddress: String?,
    val ownerCity: String?,
    val ownerState: String?,
    val ownerZipCode: String?,
    val ownerEmail: String?,
    val ownerPhone: String?
)

data class YearDays(
    val year: Int,
    val days: Int
)
