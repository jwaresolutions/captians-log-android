package com.captainslog.ui.maintenance

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.captainslog.viewmodel.MaintenanceTemplateViewModel
import kotlinx.coroutines.flow.flowOf

@Composable
fun MaintenanceEventCompletionScreen(
    eventId: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MaintenanceTemplateViewModel
) {
    val event by viewModel.getEventById(eventId).collectAsStateWithLifecycle(initialValue = null)
    val template by remember(event?.templateId) {
        if (event?.templateId != null) {
            viewModel.getTemplateById(event!!.templateId)
        } else {
            flowOf(null)
        }
    }.collectAsStateWithLifecycle(initialValue = null)

    var actualCost by remember { mutableStateOf("") }
    var actualTime by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Event info
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = template?.title ?: "Maintenance Event",
                    style = MaterialTheme.typography.titleLarge
                )

                template?.description?.let { description ->
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                template?.component?.let { component ->
                    Text(
                        text = "Component: $component",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Text(
            text = "Completion Details",
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = actualCost,
                onValueChange = { actualCost = it },
                label = { Text("Actual Cost") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f),
                placeholder = {
                    template?.estimatedCost?.let {
                        Text("Est: $${String.format("%.2f", it)}")
                    }
                }
            )

            OutlinedTextField(
                value = actualTime,
                onValueChange = { actualTime = it },
                label = { Text("Actual Time (min)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                placeholder = {
                    template?.estimatedTime?.let {
                        Text("Est: ${it}min")
                    }
                }
            )
        }

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Completion Notes") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            placeholder = { Text("Add any notes about the maintenance work performed...") }
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                val cost = actualCost.toDoubleOrNull()
                val time = actualTime.toIntOrNull()

                viewModel.completeEvent(
                    eventId = eventId,
                    actualCost = cost,
                    actualTime = time,
                    notes = notes.takeIf { it.isNotBlank() }
                )
                onNavigateBack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Complete Event")
        }
    }
}
