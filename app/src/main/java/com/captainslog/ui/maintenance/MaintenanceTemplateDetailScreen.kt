package com.captainslog.ui.maintenance

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.captainslog.viewmodel.MaintenanceTemplateViewModel

@Composable
fun MaintenanceTemplateDetailScreen(
    templateId: String,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MaintenanceTemplateViewModel
) {
    val template by viewModel.getTemplateById(templateId).collectAsStateWithLifecycle(initialValue = null)
    val events by viewModel.getEventsByTemplate(templateId).collectAsStateWithLifecycle(initialValue = emptyList())

    template?.let { tmpl ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = tmpl.title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = tmpl.description,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Text(
                        text = "Component: ${tmpl.component}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "Schedule: ${viewModel.formatRecurrence(tmpl)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    tmpl.estimatedCost?.let { cost ->
                        Text(
                            text = "Estimated Cost: $${String.format("%.2f", cost)}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    tmpl.estimatedTime?.let { time ->
                        Text(
                            text = "Estimated Time: ${time} minutes",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Edit button
            Button(
                onClick = { onNavigateToEdit(templateId) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Edit Template")
            }

            Text(
                text = "Upcoming Events (${events.filter { it.completedAt == null }.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (events.isEmpty()) {
                Text(
                    text = "No events generated yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                events.filter { it.completedAt == null }.take(5).forEach { event ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Text(
                                text = "Due: ${java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(event.dueDate)}",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            val daysUntilDue = viewModel.getDaysUntilDue(event)
                            val daysText = when {
                                daysUntilDue < 0 -> "${-daysUntilDue} days overdue"
                                daysUntilDue == 0L -> "Due today"
                                daysUntilDue == 1L -> "Due tomorrow"
                                else -> "Due in $daysUntilDue days"
                            }

                            Text(
                                text = daysText,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    } ?: run {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}
