package com.captainslog.util

import android.content.Context
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

/**
 * Centralized error handling for the application
 */
object ErrorHandler {
    private const val TAG = "ErrorHandler"

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

    /**
     * Handle API errors and show appropriate user messages
     */
    fun handleApiError(
        context: Context,
        error: Throwable,
        userMessage: String? = null,
        showToast: Boolean = true
    ): String {
        val message = userMessage ?: getErrorMessage(error)

        Log.e(TAG, "API Error: ${error.message}", error)

        if (showToast) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }

        return message
    }

    /**
     * Handle database errors
     */
    fun handleDatabaseError(
        context: Context,
        error: Throwable,
        operation: String = "database operation"
    ): String {
        val message = "Failed to perform $operation. Please try again"
        
        Log.e(TAG, "Database Error during $operation: ${error.message}", error)
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        
        return message
    }

    /**
     * Handle GPS/location errors
     */
    fun handleLocationError(
        context: Context,
        error: Throwable
    ): String {
        val message = when {
            error.message?.contains("permission", ignoreCase = true) == true -> 
                "Location permission is required for GPS tracking"
            error.message?.contains("disabled", ignoreCase = true) == true -> 
                "Please enable location services in device settings"
            else -> "GPS tracking error. Please check location settings"
        }

        Log.e(TAG, "Location Error: ${error.message}", error)
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        
        return message
    }

    /**
     * Create a coroutine exception handler for ViewModels
     */
    fun createCoroutineExceptionHandler(
        context: Context,
        onError: ((String) -> Unit)? = null
    ): CoroutineExceptionHandler {
        return CoroutineExceptionHandler { _, exception ->
            Log.e(TAG, "Coroutine Exception", exception)
            
            val message = when (exception) {
                is UnknownHostException -> "Unable to reach server"
                is ConnectException -> "Server unavailable"
                is SocketTimeoutException -> "Connection timeout"
                is IOException -> "Network error"
                else -> "An unexpected error occurred"
            }
            
            onError?.invoke(message) ?: run {
                // Show toast on main thread
                CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Log non-fatal errors for debugging
     */
    fun logNonFatalError(
        tag: String,
        message: String,
        error: Throwable? = null
    ) {
        Log.w(tag, message, error)
        
        // In production, you might want to send this to a crash reporting service
        // like Firebase Crashlytics
        // FirebaseCrashlytics.getInstance().recordException(error ?: Exception(message))
    }

    /**
     * Retry mechanism for operations
     */
    suspend fun <T> retryOperation(
        maxRetries: Int = 3,
        delayMs: Long = 1000,
        operation: suspend () -> T
    ): T {
        var lastException: Exception? = null
        
        repeat(maxRetries) { attempt ->
            try {
                return operation()
            } catch (e: Exception) {
                lastException = e
                Log.w(TAG, "Operation failed, attempt ${attempt + 1}/$maxRetries", e)
                
                if (attempt < maxRetries - 1) {
                    kotlinx.coroutines.delay(delayMs * (attempt + 1)) // Exponential backoff
                }
            }
        }
        
        throw lastException ?: Exception("Operation failed after $maxRetries attempts")
    }
}

/**
 * Extension function for safe API calls
 */
suspend fun <T> safeApiCall(
    context: Context,
    onError: ((String) -> Unit)? = null,
    apiCall: suspend () -> T
): T? {
    return try {
        apiCall()
    } catch (e: Exception) {
        val message = ErrorHandler.handleApiError(context, e, showToast = false)
        onError?.invoke(message)
        null
    }
}

/**
 * Extension function for safe database operations
 */
suspend fun <T> safeDatabaseCall(
    context: Context,
    operation: String = "database operation",
    onError: ((String) -> Unit)? = null,
    databaseCall: suspend () -> T
): T? {
    return try {
        databaseCall()
    } catch (e: Exception) {
        val message = ErrorHandler.handleDatabaseError(context, e, operation)
        onError?.invoke(message)
        null
    }
}