package com.captainslog.ui.maintenance

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.captainslog.viewmodel.MaintenanceTemplateViewModel
import kotlinx.coroutines.flow.flowOf
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MaintenanceEventDetailScreen(
    eventId: String,
    onNavigateBack: () -> Unit,
    onNavigateToTemplate: (String) -> Unit,
    onNavigateToComplete: (String) -> Unit,
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

    event?.let { evt ->
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
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = template?.title ?: "Maintenance Event",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )

                            template?.description?.let { description ->
                                Text(
                                    text = description,
                                    style = MaterialTheme.typography.bodyLarge
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

                        if (evt.completedAt != null) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Completed",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Divider()

                    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

                    if (evt.completedAt != null) {
                        Text(
                            text = "Completed: ${dateFormat.format(evt.completedAt)}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        evt.actualCost?.let { cost ->
                            Text(
                                text = "Actual Cost: $${String.format("%.2f", cost)}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        evt.actualTime?.let { time ->
                            Text(
                                text = "Actual Time: ${time} minutes",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        evt.notes?.let { notes ->
                            Text(
                                text = "Notes: $notes",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else {
                        Text(
                            text = "Due: ${dateFormat.format(evt.dueDate)}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )

                        val daysUntilDue = viewModel.getDaysUntilDue(evt)
                        val daysText = when {
                            daysUntilDue < 0 -> "${-daysUntilDue} days overdue"
                            daysUntilDue == 0L -> "Due today"
                            daysUntilDue == 1L -> "Due tomorrow"
                            else -> "Due in $daysUntilDue days"
                        }

                        Text(
                            text = daysText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = when {
                                daysUntilDue < 0 -> MaterialTheme.colorScheme.error
                                daysUntilDue <= 7 -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )

                        template?.estimatedCost?.let { cost ->
                            Text(
                                text = "Estimated Cost: $${String.format("%.2f", cost)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        template?.estimatedTime?.let { time ->
                            Text(
                                text = "Estimated Time: ${time} minutes",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Navigation to template
            Card(
                onClick = { template?.let { onNavigateToTemplate(it.id) } },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "View Template",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "See schedule and template details",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = "Go to template"
                    )
                }
            }

            // Complete button for incomplete events
            if (evt.completedAt == null) {
                Button(
                    onClick = { onNavigateToComplete(eventId) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Complete This Event")
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
