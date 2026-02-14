package com.captainslog.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.captainslog.R
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import com.captainslog.ui.components.Starfield

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    // Refresh state from preferences when LoginScreen appears (e.g., after logout)
    LaunchedEffect(Unit) {
        viewModel.refreshState()
    }

    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current
    var passwordVisible by remember { mutableStateOf(false) }

    // Pulsing glow animation
    val infiniteTransition = rememberInfiniteTransition(label = "logoGlow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )
    val glowRadius by infiniteTransition.animateFloat(
        initialValue = 20f,
        targetValue = 50f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowRadius"
    )

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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.ime)
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Add flexible space at the top to push content up when keyboard appears
            Spacer(modifier = Modifier.weight(1f))

            // Pulsing logo with orange glow
            val glowColor = Color(0xFFFF9933)
            Box(
                modifier = Modifier
                    .drawBehind {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    glowColor.copy(alpha = glowAlpha * 0.6f),
                                    glowColor.copy(alpha = glowAlpha * 0.2f),
                                    Color.Transparent
                                ),
                                center = Offset(size.width / 2f, size.height / 2f),
                                radius = glowRadius * 4f
                            )
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.captains_log_logo),
                    contentDescription = "Captain's Log",
                    modifier = Modifier
                        .height(120.dp)
                        .padding(bottom = 16.dp)
                        .graphicsLayer {
                            val scale = 1f + (glowAlpha - 0.4f) * 0.06f
                            scaleX = scale
                            scaleY = scale
                        }
                )
            }

            Text(
                text = "Sign in to continue",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF99CCFF).copy(alpha = 0.8f),
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Server URL dropdown
            val serverOptions = listOf("http://10.0.0.145:8585", "https://boat.jware.dev")
            var serverDropdownExpanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = serverDropdownExpanded,
                onExpandedChange = { if (!uiState.isLoading) serverDropdownExpanded = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                OutlinedTextField(
                    value = uiState.serverUrl,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Server URL") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = serverDropdownExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    singleLine = true,
                    enabled = !uiState.isLoading,
                    isError = uiState.error != null,
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedTextColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        focusedBorderColor = Color(0xFFFF9933),
                        unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                        focusedLabelColor = Color(0xFFFF9933),
                        cursorColor = Color(0xFFFF9933)
                    )
                )
                ExposedDropdownMenu(
                    expanded = serverDropdownExpanded,
                    onDismissRequest = { serverDropdownExpanded = false }
                ) {
                    serverOptions.forEach { url ->
                        DropdownMenuItem(
                            text = { Text(url) },
                            onClick = {
                                viewModel.updateServerUrl(url)
                                serverDropdownExpanded = false
                            }
                        )
                    }
                }
            }

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
                isError = uiState.error != null,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedTextColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                    focusedBorderColor = Color(0xFFFF9933),
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                    focusedLabelColor = Color(0xFFFF9933),
                    cursorColor = Color(0xFFFF9933)
                )
            )

            // Save username checkbox
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = uiState.saveUsername,
                    onCheckedChange = { viewModel.updateSaveUsername(it) },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFFFF9933),
                        uncheckedColor = Color.White.copy(alpha = 0.7f),
                        checkmarkColor = Color.Black
                    )
                )
                Text(
                    text = "Remember username",
                    color = Color.White.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Password field
            OutlinedTextField(
                value = uiState.password,
                onValueChange = { viewModel.updatePassword(it) },
                label = { Text("Password") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                singleLine = true,
                enabled = !uiState.isLoading,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        if (uiState.canLogin) {
                            viewModel.login(onLoginSuccess)
                        }
                    }
                ),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                isError = uiState.error != null,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedTextColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                    focusedBorderColor = Color(0xFFFF9933),
                    unfocusedLabelColor = Color.White.copy(alpha = 0.7f),
                    focusedLabelColor = Color(0xFFFF9933),
                    cursorColor = Color(0xFFFF9933)
                )
            )

            // Error message
            if (uiState.error != null) {
                Text(
                    text = uiState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
            }

            // Login button
            Button(
                onClick = { viewModel.login(onLoginSuccess) },
                enabled = !uiState.isLoading && uiState.canLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF9933),
                    contentColor = Color.Black
                )
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(if (uiState.isLoading) "Signing in..." else "Sign In")
            }

            // Offline login button (only show if we have a stored token)
            if (uiState.hasStoredToken && !uiState.isLoading) {
                OutlinedButton(
                    onClick = { viewModel.loginOffline(onLoginSuccess) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFFF9933)
                    )
                ) {
                    Text("Continue Offline")
                }
            }

            // Info text
            Text(
                text = "Contact your administrator if you need an account",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF99CCFF).copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 24.dp)
            )

            // Add flexible space at the bottom to allow keyboard to push content up
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
