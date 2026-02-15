package com.captainslog.ui.settings

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.captainslog.database.AppDatabase
import com.captainslog.database.entities.BoatEntity
import com.captainslog.pdf.CG719SFormGenerator
import com.captainslog.security.SecurePreferences
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CG719SFormScreen(
    database: AppDatabase,
    securePreferences: SecurePreferences,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var boats by remember { mutableStateOf<List<BoatEntity>>(emptyList()) }
    var selectedBoatIds by remember { mutableStateOf<Set<String>>(emptySet()) }
    var isGenerating by remember { mutableStateOf(false) }
    var results by remember { mutableStateOf<List<CG719SFormGenerator.GenerationResult>?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Load boats
    LaunchedEffect(Unit) {
        boats = database.boatDao().getAllBoatsSync()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Generate CG-719S Forms",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Select boats to generate sea service forms. One form will be created per boat using your recorded trip data.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (results != null) {
            // Show results
            Text(
                text = "Forms Generated",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(results!!) { result ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = result.boatName,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = "${result.totalDays} days of service",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(onClick = {
                                // Share the PDF
                                try {
                                    val uri = FileProvider.getUriForFile(
                                        context,
                                        "${context.packageName}.fileprovider",
                                        result.file
                                    )
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "application/pdf"
                                        putExtra(Intent.EXTRA_STREAM, uri)
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "Share CG-719S"))
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Share failed: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share PDF"
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    results = null
                    selectedBoatIds = emptySet()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Generate More")
            }
        } else {
            // Show boat selection
            if (boats.isEmpty()) {
                Text(
                    text = "No boats found. Add a boat first in Manage Boats.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            } else {
                // Select All / Deselect All
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = {
                        selectedBoatIds = if (selectedBoatIds.size == boats.size) {
                            emptySet()
                        } else {
                            boats.map { it.id }.toSet()
                        }
                    }) {
                        Text(if (selectedBoatIds.size == boats.size) "Deselect All" else "Select All")
                    }
                }

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(boats) { boat ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = boat.id in selectedBoatIds,
                                onCheckedChange = { checked ->
                                    selectedBoatIds = if (checked) {
                                        selectedBoatIds + boat.id
                                    } else {
                                        selectedBoatIds - boat.id
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = boat.name,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Button(
                    onClick = {
                        if (selectedBoatIds.isEmpty()) {
                            errorMessage = "Please select at least one boat"
                            return@Button
                        }
                        errorMessage = null
                        isGenerating = true
                        scope.launch {
                            try {
                                val generator = CG719SFormGenerator(context, database, securePreferences)
                                val genResults = generator.generateForms(selectedBoatIds.toList())
                                results = genResults
                                if (genResults.isEmpty()) {
                                    errorMessage = "No forms were generated. Make sure selected boats have trip data."
                                    results = null
                                }
                            } catch (e: Exception) {
                                errorMessage = "Generation failed: ${e.message}"
                            } finally {
                                isGenerating = false
                            }
                        }
                    },
                    enabled = !isGenerating && selectedBoatIds.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Generating...")
                    } else {
                        Text("Generate Forms (${selectedBoatIds.size} boat${if (selectedBoatIds.size != 1) "s" else ""})")
                    }
                }
            }
        }
    }
}
