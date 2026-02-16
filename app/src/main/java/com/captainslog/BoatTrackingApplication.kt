package com.captainslog

import android.app.Application
import com.captainslog.nautical.NauticalSettingsManager
import com.captainslog.nautical.tile.NauticalTilePreloader
import dagger.hilt.android.HiltAndroidApp
import org.osmdroid.config.Configuration
import javax.inject.Inject

@HiltAndroidApp
class BoatTrackingApplication : Application() {

    @Inject lateinit var nauticalSettingsManager: NauticalSettingsManager

    override fun onCreate() {
        super.onCreate()

        // Initialize osmdroid configuration early
        Configuration.getInstance().load(this, getSharedPreferences("osmdroid", 0))
        Configuration.getInstance().userAgentValue = "CaptainsLog"

        // Preload NOAA chart tiles in the background
        NauticalTilePreloader.preload(this, nauticalSettingsManager)
    }
}
