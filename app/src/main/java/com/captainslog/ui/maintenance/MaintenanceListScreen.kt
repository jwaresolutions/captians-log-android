package com.captainslog.ui.maintenance

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.captainslog.database.entities.MaintenanceTemplateEntity
import com.captainslog.database.entities.MaintenanceEventEntity
import com.captainslog.viewmodel.MaintenanceTemplateViewModel
import com.captainslog.viewmodel.TaskColor
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaintenanceListScreen(
    onNavigateToTemplateDetail: (String) -> Unit,
    onNavigateToEventDetail: (String) -> Unit,
    onNavigateToCreateTemplate: () -> Unit,
    onNavigateToEditTemplate: (String) -> Unit = { },
    modifier: Modifier = Modifier,
    viewModel: MaintenanceTemplateViewModel
) {
    val error by viewModel.error.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val successMessage by viewModel.successMessage.collectAsStateWithLifecycle()
    val allTemplates by viewModel.allTemplates.collectAsStateWithLifecycle(initialValue = emptyList())
    val upcomingEvents by viewModel.upcomingEvents.collectAsStateWithLifecycle(initialValue = emptyList())
    val completedEvents by viewModel.completedEvents.collectAsStateWithLifecycle(initialValue = emptyList())

    val overdueEvents = remember(upcomingEvents) {
        upcomingEvents.filter { viewModel.getDaysUntilDue(it) < 0 }
            .sortedBy { it.dueDate }
    }

    val dueSoonEvents = remember(upcomingEvents) {
        upcomingEvents.filter {
            val days = viewModel.getDaysUntilDue(it)
            days in 0..7
        }.sortedBy { it.dueDate }
    }

    val laterEvents = remember(upcomingEvents) {
        upcomingEvents.filter { viewModel.getDaysUntilDue(it) > 7 }
            .sortedBy { it.dueDate }
    }

    val recentCompleted = remember(completedEvents) {
        completedEvents
            .sortedByDescending { it.completedAt }
            .take(10)
    }

    var scheduleExpanded by remember { mutableStateOf(true) }
    var completedExpanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreateTemplate,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Maintenance Template")
            }
        },
        snackbarHost = {
            if (error != null || successMessage != null) {
                SnackbarHost(hostState = remember { SnackbarHostState() }) {
                    Snackbar(
                        containerColor = if (error != null)
                            MaterialTheme.colorScheme.errorContainer
                        else
                            MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = error ?: successMessage ?: "",
                            color = if (error != null)
                                MaterialTheme.colorScheme.onErrorContainer
                            else
                                MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            upcomingEvents.isEmpty() && allTemplates.isEmpty() && completedEvents.isEmpty() -> {
                EmptyState(
                    onCreateTemplate = onNavigateToCreateTemplate,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        SummaryCard(
                            overdueCount = overdueEvents.size,
                            dueSoonCount = dueSoonEvents.size,
                            scheduledCount = laterEvents.size
                        )
                    }

                    if (overdueEvents.isNotEmpty() || dueSoonEvents.isNotEmpty()) {
                        item {
                            SectionHeader(
                                title = "Attention Needed",
                                icon = Icons.Default.Warning
                            )
                        }

                        items(overdueEvents) { event ->
                            AttentionEventCard(
                                event = event,
                                isOverdue = true,
                                onClick = { onNavigateToEventDetail(event.id) },
                                onComplete = { viewModel.completeEvent(event.id, null, null, null) },
                                viewModel = viewModel
                            )
                        }

                        items(dueSoonEvents) { event ->
                            AttentionEventCard(
                                event = event,
                                isOverdue = false,
                                onClick = { onNavigateToEventDetail(event.id) },
                                onComplete = { viewModel.completeEvent(event.id, null, null, null) },
                                viewModel = viewModel
                            )
                        }
                    }

                    if (laterEvents.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            SectionHeader(title = "Upcoming")
                        }

                        items(laterEvents) { event ->
                            CompactEventCard(
                                event = event,
                                onClick = { onNavigateToEventDetail(event.id) },
                                viewModel = viewModel
                            )
                        }
                    }

                    if (allTemplates.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            CollapsibleSectionHeader(
                                title = "Maintenance Schedule",
                                count = allTemplates.size,
                                expanded = scheduleExpanded,
                                onToggle = { scheduleExpanded = !scheduleExpanded }
                            )
                        }

                        if (scheduleExpanded) {
                            items(allTemplates) { template ->
                                TemplateCard(
                                    template = template,
                                    onClick = { onNavigateToTemplateDetail(template.id) },
                                    onEdit = { onNavigateToEditTemplate(template.id) },
                                    onDelete = { viewModel.deleteTemplate(template.id) },
                                    viewModel = viewModel
                                )
                            }
                        }
                    }

                    if (recentCompleted.isNotEmpty()) {
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                            CollapsibleSectionHeader(
                                title = "Recently Completed",
                                count = recentCompleted.size,
                                expanded = completedExpanded,
                                onToggle = { completedExpanded = !completedExpanded }
                            )
                        }

                        if (completedExpanded) {
                            items(recentCompleted) { event ->
                                CompletedCard(
                                    event = event,
                                    onClick = { onNavigateToEventDetail(event.id) },
                                    viewModel = viewModel
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun CollapsibleSectionHeader(
    title: String,
    count: Int,
    expanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onToggle,
        modifier = modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Badge(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(count.toString())
                }
            }

            Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (expanded) "Collapse" else "Expand",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EmptyState(
    onCreateTemplate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically)
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        )

        Text(
            text = "No Maintenance Scheduled",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "Create your first maintenance template to start tracking recurring maintenance tasks for your boat.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
