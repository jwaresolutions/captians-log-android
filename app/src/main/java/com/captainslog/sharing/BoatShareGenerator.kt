package com.captainslog.sharing

import android.graphics.Bitmap
import com.captainslog.database.entities.BoatEntity
import com.captainslog.security.SecurePreferences
import com.captainslog.sharing.models.BoatShareData
import com.captainslog.sharing.models.BoatData
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder

/**
 * Generates QR codes for sharing boat data between devices.
 */
class BoatShareGenerator(
    private val securePreferences: SecurePreferences
) {
    private val gson = Gson()

    /**
     * Generates a QR code bitmap from a BoatEntity.
     *
     * @param boat The boat to encode
     * @param size The size of the QR code in pixels (default 512)
     * @return A Bitmap containing the QR code
     */
    fun generateQrBitmap(boat: BoatEntity, size: Int = 512): Bitmap {
        val shareData = BoatShareData(
            origin = "device:${securePreferences.deviceId}",
            timestamp = System.currentTimeMillis(),
            data = BoatData(
                id = boat.id,
                name = boat.name,
                enabled = boat.enabled
            )
        )
        val json = gson.toJson(shareData)
        return BarcodeEncoder().encodeBitmap(json, BarcodeFormat.QR_CODE, size, size)
    }

    /**
     * Parses QR code JSON data back into a BoatShareData object.
     *
     * @param json The JSON string from the QR code
     * @return BoatShareData if parsing succeeds, null otherwise
     */
    fun parseQrData(json: String): BoatShareData? {
        return try {
            gson.fromJson(json, BoatShareData::class.java)
        } catch (e: Exception) {
            null
        }
    }
}
