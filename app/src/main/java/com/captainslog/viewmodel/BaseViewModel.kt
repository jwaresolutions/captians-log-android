package com.captainslog.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.captainslog.util.ErrorHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Base ViewModel with standardized error handling, loading state, and success messaging
 */
abstract class BaseViewModel : ViewModel() {

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    fun clearError() { _error.value = null }
    fun clearSuccess() { _successMessage.value = null }

    protected fun setError(message: String) { _error.value = message }
    protected fun setSuccess(message: String) { _successMessage.value = message }
    protected fun setLoading(loading: Boolean) { _isLoading.value = loading }

    /**
     * Launch a coroutine with automatic error handling and loading state management
     *
     * @param tag Tag for logging (defaults to class name)
     * @param onSuccess Optional callback invoked after successful completion
     * @param block The suspend block to execute
     */
    protected fun launchWithErrorHandling(
        tag: String = this::class.simpleName ?: "ViewModel",
        onSuccess: (() -> Unit)? = null,
        block: suspend () -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                block()
                onSuccess?.invoke()
            } catch (e: Exception) {
                Log.e(tag, "Error in $tag", e)
                _error.value = ErrorHandler.getErrorMessage(e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
