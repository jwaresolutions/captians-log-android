package com.captainslog.security

import com.captainslog.BuildConfig
import okhttp3.CertificatePinner
import java.net.URL

object CertificatePinnerBuilder {
    /**
     * Creates a CertificatePinner with the provided certificate pins
     * In debug builds, returns an empty pinner (no pinning)
     * @param localUrl Optional local connection URL
     * @param localPin Optional SHA-256 certificate pin for local connection (with or without "sha256/" prefix)
     * @param remoteUrl Remote connection URL
     * @param remotePin SHA-256 certificate pin for remote connection (with or without "sha256/" prefix)
     */
    fun build(
        localUrl: String?,
        localPin: String?,
        remoteUrl: String,
        remotePin: String
    ): CertificatePinner {
        // In debug builds, skip certificate pinning
        if (!BuildConfig.REQUIRE_CERT_PINNING) {
            return CertificatePinner.Builder().build()
        }

        val builder = CertificatePinner.Builder()

        // Add remote certificate pin (required in release)
        val remoteHost = URL(remoteUrl).host
        val normalizedRemotePin = normalizePin(remotePin)
        builder.add(remoteHost, normalizedRemotePin)

        // Add local certificate pin if configured
        if (!localUrl.isNullOrEmpty() && !localPin.isNullOrEmpty()) {
            val localHost = URL(localUrl).host
            val normalizedLocalPin = normalizePin(localPin)
            builder.add(localHost, normalizedLocalPin)
        }

        return builder.build()
    }

    /**
     * Normalizes a certificate pin to ensure it has the "sha256/" prefix
     */
    private fun normalizePin(pin: String): String {
        val trimmed = pin.trim()
        return if (trimmed.startsWith("sha256/")) {
            trimmed
        } else {
            "sha256/$trimmed"
        }
    }
}
