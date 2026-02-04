package com.captainslog.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.captainslog.nautical.NauticalProviders
import com.captainslog.nautical.NauticalSettingsManager
import com.captainslog.nautical.model.NauticalProviderMeta
import com.captainslog.nautical.model.ProviderTier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NauticalSettingsScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val settingsManager = remember { NauticalSettingsManager.getInstance(context) }
    val settings by settingsManager.settings.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Free Providers
        SettingsSection(title = "Free Providers") {
            NauticalProviders.free.forEach { provider ->
                ProviderCard(
                    provider = provider,
                    enabled = settings[provider.id]?.enabled ?: false,
                    apiKey = settings[provider.id]?.apiKey ?: "",
                    onToggle = { settingsManager.toggleProvider(provider.id) },
                    onApiKeyChange = { settingsManager.setApiKey(provider.id, it) }
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
                    onToggle = { settingsManager.toggleProvider(provider.id) },
                    onApiKeyChange = { settingsManager.setApiKey(provider.id, it) }
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
    onToggle: () -> Unit,
    onApiKeyChange: (String) -> Unit
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
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
                    checked = enabled,
                    onCheckedChange = { onToggle() }
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

                    // Pros
                    Text(
                        text = "Advantages",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    provider.pros.forEach { pro ->
                        Text(
                            text = "+ $pro",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Cons
                    Text(
                        text = "Limitations",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                    provider.cons.forEach { con ->
                        Text(
                            text = "- $con",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                        )
                    }

                    // API Key input
                    if (provider.requiresApiKey) {
                        OutlinedTextField(
                            value = apiKey,
                            onValueChange = onApiKeyChange,
                            label = { Text("API Key") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
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
                }
            }
        }
    }
}
