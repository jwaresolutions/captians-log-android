package com.captainslog.ui.sensors

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

/**
 * Dialog for adding a new sensor type
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSensorTypeDialog(
    isCreating: Boolean,
    error: String?,
    onDismiss: () -> Unit,
    onCreateSensorType: (name: String, unit: String, frequency: String, description: String?) -> Unit,
    onClearError: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }
    var selectedFrequency by remember { mutableStateOf("continuous") }
    var description by remember { mutableStateOf("") }
    
    val frequencyOptions = listOf(
        "continuous" to "Continuous",
        "snapshot" to "Snapshot"
    )
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Add Sensor Type",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Show error if present
                error?.let { errorMessage ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF44336).copy(alpha = 0.1f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = errorMessage,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFF44336),
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(onClick = onClearError) {
                                Text("Dismiss")
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
                
                // Name field
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Sensor Name") },
                    placeholder = { Text("e.g., Fuel Level, Battery Voltage") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isCreating,
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Unit field
                OutlinedTextField(
                    value = unit,
                    onValueChange = { unit = it },
                    label = { Text("Unit") },
                    placeholder = { Text("e.g., L, V, C, %") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isCreating,
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Logging frequency selection
                Text(
                    text = "Logging Frequency",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Column(
                    modifier = Modifier.selectableGroup()
                ) {
                    frequencyOptions.forEach { (value, label) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = (selectedFrequency == value),
                                    onClick = { selectedFrequency = value },
                                    role = Role.RadioButton
                                )
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (selectedFrequency == value),
                                onClick = null,
                                enabled = !isCreating
                            )
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Description field (optional)
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    placeholder = { Text("Additional details about this sensor") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isCreating,
                    maxLines = 3
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onDismiss,
                        enabled = !isCreating
                    ) {
                        Text("Cancel")
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = {
                            if (name.isNotBlank() && unit.isNotBlank()) {
                                onCreateSensorType(
                                    name.trim(),
                                    unit.trim(),
                                    selectedFrequency,
                                    description.trim().takeIf { it.isNotBlank() }
                                )
                            }
                        },
                        enabled = !isCreating && name.isNotBlank() && unit.isNotBlank()
                    ) {
                        if (isCreating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text("Create")
                    }
                }
            }
        }
    }
}