package com.captainslog.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.captainslog.nautical.NauticalProviders
import com.captainslog.nautical.NauticalSettingsManager
import com.captainslog.nautical.model.MapRole
import com.captainslog.nautical.model.ProviderType
import com.captainslog.nautical.tile.NauticalTileSources
import org.osmdroid.config.Configuration
import com.captainslog.nautical.model.NauticalProviderMeta
import com.captainslog.nautical.model.ProviderTier
import com.captainslog.viewmodel.MainNavigationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NauticalSettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: MainNavigationViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val settingsManager = viewModel.nauticalSettingsManager
    val settings by settingsManager.settings.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Free Providers
        SettingsSection(title = "Free Providers") {
            // Separate grouped and ungrouped providers
            val grouped = NauticalProviders.free.filter { it.group != null }.groupBy { it.group!! }
            val ungrouped = NauticalProviders.free.filter { it.group == null }

            // Render grouped providers under section headers
            grouped.forEach { (groupName, providers) ->
                Text(
                    text = groupName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 4.dp)
                )
                providers.forEach { provider ->
                    ProviderCard(
                        provider = provider,
                        enabled = settings[provider.id]?.enabled ?: false,
                        apiKey = settings[provider.id]?.apiKey ?: "",
                        apiKeyVerified = settings[provider.id]?.options?.get("apiKeyVerified") == "true",
                        onToggle = { settingsManager.toggleProvider(provider.id) },
                        onApiKeyChange = { settingsManager.setApiKey(provider.id, it) },
                        onApiKeyVerified = { verified -> settingsManager.setProviderOption(provider.id, "apiKeyVerified", verified.toString()) }
                    )
                }
            }

            // Render ungrouped providers standalone
            ungrouped.forEach { provider ->
                ProviderCard(
                    provider = provider,
                    enabled = settings[provider.id]?.enabled ?: false,
                    apiKey = settings[provider.id]?.apiKey ?: "",
                    apiKeyVerified = settings[provider.id]?.options?.get("apiKeyVerified") == "true",
                    onToggle = { settingsManager.toggleProvider(provider.id) },
                    onApiKeyChange = { settingsManager.setApiKey(provider.id, it) },
                    onApiKeyVerified = { verified -> settingsManager.setProviderOption(provider.id, "apiKeyVerified", verified.toString()) }
                )
            }
        }

        HorizontalDivider()

        // Paid Providers
        SettingsSection(title = "Paid Providers") {
            NauticalProviders.paid.forEach { provider ->
                ProviderCard(
                    provider = provider,
                    enabled = settings[provider.id]?.enabled ?: false,
                    apiKey = settings[provider.id]?.apiKey ?: "",
                    apiKeyVerified = settings[provider.id]?.options?.get("apiKeyVerified") == "true",
                    onToggle = { settingsManager.toggleProvider(provider.id) },
                    onApiKeyChange = { settingsManager.setApiKey(provider.id, it) },
                    onApiKeyVerified = { verified -> settingsManager.setProviderOption(provider.id, "apiKeyVerified", verified.toString()) }
                )
            }
        }

        HorizontalDivider()

        // Clear all tile cache
        SettingsSection(title = "Cache") {
            var showConfirmDialog by remember { mutableStateOf(false) }
            var cacheSize by remember { mutableStateOf(getAllCacheSize()) }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "All Map Tiles",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = cacheSize,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    OutlinedButton(
                        onClick = { showConfirmDialog = true },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Clear All")
                    }
                }
            }

            if (showConfirmDialog) {
                AlertDialog(
                    onDismissRequest = { showConfirmDialog = false },
                    title = { Text("Clear All Tile Cache") },
                    text = { Text("This will delete all cached map tiles for every provider. They will be re-downloaded as needed.") },
                    confirmButton = {
                        TextButton(onClick = {
                            clearAllCache()
                            cacheSize = getAllCacheSize()
                            showConfirmDialog = false
                        }) {
                            Text("Clear All", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showConfirmDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun ProviderCard(
    provider: NauticalProviderMeta,
    enabled: Boolean,
    apiKey: String,
    apiKeyVerified: Boolean = false,
    onToggle: () -> Unit,
    onApiKeyChange: (String) -> Unit,
    onApiKeyVerified: (Boolean) -> Unit = {},
    indent: Boolean = false,
    parentEnabled: Boolean = true
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = if (indent) 32.dp else 16.dp, end = 16.dp, top = 4.dp, bottom = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = provider.name,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                        AssistChip(
                            onClick = {},
                            label = {
                                Text(
                                    text = if (provider.tier == ProviderTier.FREE) "FREE" else "PAID",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (provider.tier == ProviderTier.FREE)
                                    MaterialTheme.colorScheme.primaryContainer
                                else
                                    MaterialTheme.colorScheme.tertiaryContainer
                            ),
                            modifier = Modifier.height(24.dp)
                        )
                        AssistChip(
                            onClick = {},
                            label = {
                                Text(
                                    text = when (provider.mapRole) {
                                        MapRole.BASE_MAP -> "Base Map"
                                        MapRole.OVERLAY -> "Overlay"
                                        MapRole.DATA -> "Data"
                                    },
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = when (provider.mapRole) {
                                    MapRole.BASE_MAP -> MaterialTheme.colorScheme.secondaryContainer
                                    MapRole.OVERLAY -> MaterialTheme.colorScheme.tertiaryContainer
                                    MapRole.DATA -> MaterialTheme.colorScheme.surfaceVariant
                                }
                            ),
                            modifier = Modifier.height(24.dp)
                        )
                    }
                    if (!expanded) {
                        Text(
                            text = provider.description.split(".").first(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Switch(
                    checked = enabled && parentEnabled,
                    onCheckedChange = { onToggle() },
                    enabled = parentEnabled && (!provider.requiresApiKey || apiKeyVerified)
                )

                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // Expanded content
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = provider.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Features
                    Text(
                        text = "Features",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    provider.features.forEach { feature ->
                        Text(
                            text = "+ $feature",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Warnings
                    Text(
                        text = "Warnings",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                    provider.warnings.forEach { warning ->
                        Text(
                            text = "⚠ $warning",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                        )
                    }

                    // API Key input
                    if (provider.requiresApiKey) {
                        var localApiKey by remember(apiKey) { mutableStateOf(apiKey) }
                        var testState by remember { mutableStateOf<ApiKeyTestState>(ApiKeyTestState.Idle) }
                        val coroutineScope = rememberCoroutineScope()

                        OutlinedTextField(
                            value = localApiKey,
                            onValueChange = {
                                localApiKey = it
                                testState = ApiKeyTestState.Idle
                                onApiKeyVerified(false)
                                if (enabled) onToggle() // Disable provider when key changes
                            },
                            label = { Text("API Key") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    onApiKeyChange(localApiKey)
                                    if (provider.id == "aisstream" && localApiKey.isNotBlank()) {
                                        testState = ApiKeyTestState.Testing
                                        coroutineScope.launch {
                                            val result = com.captainslog.nautical.service.AISStreamService().testApiKey(localApiKey)
                                            if (result.isSuccess) {
                                                testState = ApiKeyTestState.Success
                                                onApiKeyVerified(true)
                                            } else {
                                                testState = ApiKeyTestState.Failed(result.exceptionOrNull()?.message ?: "Connection failed")
                                                onApiKeyVerified(false)
                                            }
                                        }
                                    } else {
                                        testState = ApiKeyTestState.Saved
                                    }
                                },
                                enabled = localApiKey.isNotBlank() && testState != ApiKeyTestState.Testing
                            ) {
                                if (testState == ApiKeyTestState.Testing) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Testing...")
                                } else {
                                    Text(if (provider.id == "aisstream") "Save & Test" else "Save")
                                }
                            }

                            when (testState) {
                                is ApiKeyTestState.Success -> Text(
                                    "Connected successfully",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                is ApiKeyTestState.Saved -> Text(
                                    "Saved",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                is ApiKeyTestState.Failed -> Text(
                                    (testState as ApiKeyTestState.Failed).message,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                                else -> {}
                            }
                        }

                        provider.apiKeySignupUrl?.let { url ->
                            TextButton(
                                onClick = {
                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                                }
                            ) {
                                Icon(
                                    Icons.Default.OpenInNew,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Get an API key")
                            }
                        }
                    }

                    // Pricing note
                    provider.pricingNote?.let { note ->
                        Text(
                            text = note,
                            style = MaterialTheme.typography.bodySmall,
                            fontStyle = FontStyle.Italic,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Website link
                    TextButton(
                        onClick = {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(provider.website)))
                        }
                    ) {
                        Icon(
                            Icons.Default.OpenInNew,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Visit website")
                    }

                    // Per-provider cache management for tile providers
                    if (provider.type == ProviderType.TILE) {
                        val tileName = NauticalTileSources.getSourceById(provider.id)?.name()
                        if (tileName != null) {
                            var cacheSize by remember { mutableStateOf(getProviderCacheSize(tileName)) }
                            var showClearDialog by remember { mutableStateOf(false) }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Cached tiles",
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                    Text(
                                        text = cacheSize,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                OutlinedButton(
                                    onClick = { showClearDialog = true },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Clear Cache", style = MaterialTheme.typography.labelSmall)
                                }
                            }

                            if (showClearDialog) {
                                AlertDialog(
                                    onDismissRequest = { showClearDialog = false },
                                    title = { Text("Clear ${provider.name} Cache") },
                                    text = { Text("Delete cached tiles for ${provider.name}? They will be re-downloaded as needed.") },
                                    confirmButton = {
                                        TextButton(onClick = {
                                            clearProviderCache(tileName)
                                            cacheSize = getProviderCacheSize(tileName)
                                            showClearDialog = false
                                        }) {
                                            Text("Clear", color = MaterialTheme.colorScheme.error)
                                        }
                                    },
                                    dismissButton = {
                                        TextButton(onClick = { showClearDialog = false }) {
                                            Text("Cancel")
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun getProviderCacheSize(tileName: String): String {
    val cacheDir = java.io.File(Configuration.getInstance().osmdroidTileCache, tileName)
    if (!cacheDir.exists()) return "Empty"
    val bytes = cacheDir.walkTopDown().filter { it.isFile }.sumOf { it.length() }
    return formatBytes(bytes)
}

private fun clearProviderCache(tileName: String) {
    val cacheDir = java.io.File(Configuration.getInstance().osmdroidTileCache, tileName)
    if (cacheDir.exists()) {
        cacheDir.deleteRecursively()
    }
}

private fun getAllCacheSize(): String {
    val cacheDir = Configuration.getInstance().osmdroidTileCache
    if (!cacheDir.exists()) return "Empty"
    val bytes = cacheDir.walkTopDown().filter { it.isFile }.sumOf { it.length() }
    return formatBytes(bytes)
}

private fun clearAllCache() {
    val cacheDir = Configuration.getInstance().osmdroidTileCache
    if (cacheDir.exists()) {
        cacheDir.deleteRecursively()
    }
}

private fun formatBytes(bytes: Long): String = when {
    bytes < 1024 -> "$bytes B"
    bytes < 1024 * 1024 -> "${"%.1f".format(bytes / 1024.0)} KB"
    else -> "${"%.1f".format(bytes / (1024.0 * 1024.0))} MB"
}

private sealed class ApiKeyTestState {
    data object Idle : ApiKeyTestState()
    data object Testing : ApiKeyTestState()
    data object Success : ApiKeyTestState()
    data object Saved : ApiKeyTestState()
    data class Failed(val message: String) : ApiKeyTestState()
}
