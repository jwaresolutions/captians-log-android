package com.captainslog.ui.boats

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Dialog for adding a new boat.
 * Validates boat name and calls onConfirm when user submits.
 */
@Composable
fun AddBoatDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var boatName by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Boat") },
        text = {
            Column {
                OutlinedTextField(
                    value = boatName,
                    onValueChange = {
                        boatName = it
                        showError = false
                    },
                    label = { Text("Boat Name") },
                    placeholder = { Text("Enter boat name") },
                    isError = showError,
                    supportingText = if (showError) {
                        { Text("Boat name cannot be empty") }
                    } else null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (boatName.isBlank()) {
                        showError = true
                    } else {
                        onConfirm(boatName.trim())
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
