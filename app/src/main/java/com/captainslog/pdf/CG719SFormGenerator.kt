package com.captainslog.pdf

import android.content.Context
import com.captainslog.database.AppDatabase
import com.captainslog.security.SecurePreferences
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * High-level form generator that coordinates data aggregation and PDF filling.
 * Generates one CG-719S PDF per selected boat.
 */
class CG719SFormGenerator(
    private val context: Context,
    private val database: AppDatabase,
    private val securePreferences: SecurePreferences
) {

    data class GenerationResult(
        val boatName: String,
        val file: File,
        val totalDays: Int
    )

    /**
     * Generate CG-719S forms for the specified boats.
     * @param boatIds List of boat IDs to generate forms for
     * @return List of GenerationResult, one per boat
     */
    suspend fun generateForms(boatIds: List<String>): List<GenerationResult> {
        val results = mutableListOf<GenerationResult>()
        val allTrips = database.tripDao().getAllTripsSync()
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())

        // Ensure output directory exists
        val outputDir = File(context.cacheDir, "cg719s")
        outputDir.mkdirs()

        for (boatId in boatIds) {
            val boat = database.boatDao().getBoatById(boatId) ?: continue
            val boatTrips = allTrips.filter { it.boatId == boatId }

            val formData = CG719SDataAggregator.aggregate(boat, boatTrips, securePreferences)

            val safeBoatName = boat.name.replace(Regex("[^a-zA-Z0-9_-]"), "_")
            val outputFile = File(outputDir, "CG719S_${safeBoatName}_$timestamp.pdf")

            CG719SPdfFiller.fillForm(context, formData, outputFile)

            results.add(
                GenerationResult(
                    boatName = boat.name,
                    file = outputFile,
                    totalDays = formData.totalDaysServed
                )
            )
        }

        return results
    }
}
