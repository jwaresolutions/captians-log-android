package com.captainslog.util

import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

/**
 * Centralized error handling for the application
 */
object ErrorHandler {

    /**
     * Get user-friendly error message from exception (without context/toast)
     */
    fun getErrorMessage(error: Throwable): String {
        return when (error) {
            is UnknownHostException -> "Unable to reach server. Check your connection"
            is ConnectException -> "Unable to reach server. The server may be unavailable"
            is SocketTimeoutException -> "Connection timed out. Please try again"
            is SSLException -> "Secure connection failed. Please check your network"
            is IOException -> "Network error occurred. Please try again"
            else -> error.message ?: "An unexpected error occurred"
        }
    }
}
