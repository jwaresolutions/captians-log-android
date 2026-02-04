package com.captainslog.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.captainslog.ui.components.Starfield
import com.captainslog.viewmodel.ServerConnectionViewModel
import com.captainslog.viewmodel.ServerConnectionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerConnectionScreen(
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: ServerConnectionViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    var passwordVisible by remember { mutableStateOf(false) }

    // Handle success state
    LaunchedEffect(uiState.connectionState) {
        if (uiState.connectionState is ServerConnectionState.Success) {
            onSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Starfield background
        Starfield(
            modifier = Modifier.fillMaxSize(),
            numStars = 150,
            speed = 2f,
            opacity = 0.5f
        )

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Connect to Server", color = Color.White) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Black.copy(alpha = 0.9f)
                    )
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Info card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1A1A2E).copy(alpha = 0.9f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = null,
                            tint = Color(0xFF99CCFF),
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Connecting to a server enables multi-device sync, crew features, and maintenance tracking.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF99CCFF).copy(alpha = 0.9f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Server URL field
                OutlinedTextField(
                    value = uiState.serverUrl,
                    onValueChange = { viewModel.updateServerUrl(it) },
                    label = { Text("Server URL") },
                    placeholder = { Text("https://example.com") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !uiState.isLoading,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Uri,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    isError = uiState.connectionState is ServerConnectionState.Error,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        focusedBorderColor = Color(0xFFFF9933),
                        unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                        focusedLabelColor = Color(0xFFFF9933),
                        unfocusedPlaceholderColor = Color.White.copy(alpha = 0.4f),
                        focusedPlaceholderColor = Color.White.copy(alpha = 0.4f),
                        cursorColor = Color(0xFFFF9933),
                        errorBorderColor = MaterialTheme.colorScheme.error,
                        errorLabelColor = MaterialTheme.colorScheme.error
                    )
                )

                // Username field
                OutlinedTextField(
                    value = uiState.username,
                    onValueChange = { viewModel.updateUsername(it) },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !uiState.isLoading,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { focusManager.moveFocus(FocusDirection.Down) }
                    ),
                    isError = uiState.connectionState is ServerConnectionState.Error,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        focusedBorderColor = Color(0xFFFF9933),
                        unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                        focusedLabelColor = Color(0xFFFF9933),
                        cursorColor = Color(0xFFFF9933),
                        errorBorderColor = MaterialTheme.colorScheme.error,
                        errorLabelColor = MaterialTheme.colorScheme.error
                    )
                )

                // Password field
                OutlinedTextField(
                    value = uiState.password,
                    onValueChange = { viewModel.updatePassword(it) },
                    label = { Text("Password") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !uiState.isLoading,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            if (uiState.canConnect) {
                                viewModel.connect(onSuccess)
                            }
                        }
                    ),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                tint = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    },
                    isError = uiState.connectionState is ServerConnectionState.Error,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        focusedBorderColor = Color(0xFFFF9933),
                        unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                        focusedLabelColor = Color(0xFFFF9933),
                        cursorColor = Color(0xFFFF9933),
                        errorBorderColor = MaterialTheme.colorScheme.error,
                        errorLabelColor = MaterialTheme.colorScheme.error
                    )
                )

                // Error message
                if (uiState.connectionState is ServerConnectionState.Error) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                        )
                    ) {
                        Text(
                            text = (uiState.connectionState as ServerConnectionState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Connect button
                Button(
                    onClick = { viewModel.connect(onSuccess) },
                    enabled = uiState.canConnect,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF9933),
                        contentColor = Color.Black,
                        disabledContainerColor = Color(0xFFFF9933).copy(alpha = 0.3f),
                        disabledContentColor = Color.Black.copy(alpha = 0.5f)
                    )
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.Black,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Connecting...")
                    } else {
                        Text("Connect")
                    }
                }

                // Help text
                Text(
                    text = "Your credentials will be stored securely on this device",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF99CCFF).copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 16.dp)
                )

                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}
