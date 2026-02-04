package com.captainslog.ui.setup

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.captainslog.R

@Composable
fun SetupScreen(
    onSetupComplete: () -> Unit,
    viewModel: SetupViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Captain's Log Logo
        Image(
            painter = painterResource(id = R.drawable.captains_log_logo),
            contentDescription = "Captain's Log",
            modifier = Modifier
                .height(100.dp)
                .padding(bottom = 16.dp)
        )
        
        Text(
            text = "Setup",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        when (uiState.currentStep) {
            SetupStep.SERVER_CONFIG -> ServerConfigStep(
                serverUrl = uiState.serverUrl,
                certPin = uiState.certPin,
                onServerUrlChange = { viewModel.updateServerUrl(it) },
                onCertPinChange = { viewModel.updateCertPin(it) },
                onNext = { viewModel.nextStep() }
            )
            SetupStep.TEST_CONNECTION -> TestConnectionStep(
                isTesting = uiState.isTesting,
                testResult = uiState.testResult,
                onTest = { viewModel.testConnection() },
                onComplete = {
                    viewModel.completeSetup()
                    onSetupComplete()
                },
                onBack = { viewModel.previousStep() }
            )
        }
    }
}

@Composable
fun ServerConfigStep(
    serverUrl: String,
    certPin: String,
    onServerUrlChange: (String) -> Unit,
    onCertPinChange: (String) -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Server Configuration",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = if (com.captainslog.BuildConfig.REQUIRE_CERT_PINNING) {
                "Enter your server URL and certificate fingerprint. You'll log in with your username and password after setup."
            } else {
                "Enter your server URL. You'll log in with your username and password after setup."
            },
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = serverUrl,
            onValueChange = onServerUrlChange,
            label = { Text("Server URL") },
            placeholder = { Text(if (com.captainslog.BuildConfig.ALLOW_HTTP) "http://10.0.2.2:8585" else "https://captainslog.jware.dev") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true
        )

        if (com.captainslog.BuildConfig.REQUIRE_CERT_PINNING) {
            OutlinedTextField(
                value = certPin,
                onValueChange = onCertPinChange,
                label = { Text("Certificate Fingerprint (SHA-256)") },
                placeholder = { Text("sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                singleLine = false,
                maxLines = 3
            )
        }

        val isValid = if (com.captainslog.BuildConfig.REQUIRE_CERT_PINNING) {
            serverUrl.isNotBlank() && certPin.isNotBlank()
        } else {
            serverUrl.isNotBlank()
        }

        Button(
            onClick = onNext,
            enabled = isValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Next")
        }
    }
}

@Composable
fun TestConnectionStep(
    isTesting: Boolean,
    testResult: ConnectionTestResult?,
    onTest: () -> Unit,
    onComplete: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Test Connection",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Test your server connection to ensure everything is configured correctly",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Connection status
        ConnectionStatusCard(
            title = "Server Connection",
            isLoading = isTesting,
            result = testResult,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Button(
            onClick = onTest,
            enabled = !isTesting,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            if (isTesting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(if (isTesting) "Testing..." else "Test Connection")
        }

        val canComplete = testResult?.success == true
        Button(
            onClick = onComplete,
            enabled = canComplete && !isTesting,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            Text("Complete Setup")
        }

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}

@Composable
fun ConnectionStatusCard(
    title: String,
    isLoading: Boolean,
    result: ConnectionTestResult?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isLoading -> MaterialTheme.colorScheme.surfaceVariant
                result?.success == true -> MaterialTheme.colorScheme.primaryContainer
                result?.success == false -> MaterialTheme.colorScheme.errorContainer
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            when {
                isLoading -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Testing connection...")
                    }
                }
                result != null -> {
                    Text(
                        text = if (result.success) "✓ Connected successfully" else "✗ Connection failed",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (result.success) 
                            MaterialTheme.colorScheme.onPrimaryContainer 
                        else 
                            MaterialTheme.colorScheme.onErrorContainer
                    )
                    if (result.message.isNotBlank()) {
                        Text(
                            text = result.message,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
                else -> {
                    Text(
                        text = "Not tested yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
