package com.captainslog.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Error message card component
 */
@Composable
fun ErrorCard(
    title: String = "Error",
    message: String,
    onRetry: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0x20FF6B6B)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "⚠ $title",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF6B6B)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = message,
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
            
            if (onRetry != null || onDismiss != null) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    onRetry?.let { retry ->
                        Button(
                            onClick = retry,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFF6B6B)
                            )
                        ) {
                            Text("Try Again")
                        }
                    }
                    
                    onDismiss?.let { dismiss ->
                        OutlinedButton(
                            onClick = dismiss,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.Gray
                            )
                        ) {
                            Text("Dismiss")
                        }
                    }
                }
            }
        }
    }
}

/**
 * Inline error message component
 */
@Composable
fun InlineError(
    message: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "⚠",
            color = Color(0xFFFF6B6B),
            fontSize = 16.sp
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = message,
            color = Color(0xFFFF6B6B),
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Network error component with offline indicator
 */
@Composable
fun NetworkError(
    isOffline: Boolean = false,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    ErrorCard(
        title = if (isOffline) "Offline" else "Network Error",
        message = if (isOffline) {
            "You are currently offline. Some features may not be available."
        } else {
            "Unable to connect to the server. Please check your internet connection."
        },
        onRetry = onRetry,
        modifier = modifier
    )
}

/**
 * Loading error component for failed data loading
 */
@Composable
fun LoadingError(
    resource: String = "data",
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    ErrorCard(
        title = "Loading Failed",
        message = "Failed to load $resource. Please try again.",
        onRetry = onRetry,
        modifier = modifier
    )
}

/**
 * Permission error component
 */
@Composable
fun PermissionError(
    permission: String,
    onRequestPermission: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    ErrorCard(
        title = "Permission Required",
        message = "$permission permission is required for this feature to work properly.",
        onRetry = onRequestPermission,
        modifier = modifier
    )
}

/**
 * Empty state component (not exactly an error, but related)
 */
@Composable
fun EmptyState(
    title: String = "No Data",
    message: String,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "📭",
            fontSize = 48.sp
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = message,
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
        
        if (actionText != null && onAction != null) {
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = onAction,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(actionText)
            }
        }
    }
}

/**
 * Composable for handling different UI states
 */
@Composable
fun <T> StateHandler(
    state: UiState<T>,
    onRetry: (() -> Unit)? = null,
    loadingContent: @Composable () -> Unit = { LoadingIndicator() },
    emptyContent: @Composable () -> Unit = { 
        EmptyState(message = "No data available") 
    },
    errorContent: @Composable (String) -> Unit = { error ->
        ErrorCard(
            message = error,
            onRetry = onRetry
        )
    },
    successContent: @Composable (T) -> Unit
) {
    when (state) {
        is UiState.Loading -> loadingContent()
        is UiState.Success -> {
            if (state.data is List<*> && state.data.isEmpty()) {
                emptyContent()
            } else {
                successContent(state.data)
            }
        }
        is UiState.Error -> errorContent(state.message)
    }
}

/**
 * UI State sealed class for handling different states
 */
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : UiState<Nothing>()
}

/**
 * Loading indicator component
 */
@Composable
fun LoadingIndicator(
    message: String = "Loading...",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = message,
            color = Color.Gray,
            fontSize = 14.sp
        )
    }
}