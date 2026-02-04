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
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
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
            if (uiState.error != null || uiState.message != null) {
                SnackbarHost(hostState = remember { SnackbarHostState() }) {
                    Snackbar(
                        containerColor = if (uiState.error != null)
                            MaterialTheme.colorScheme.errorContainer
                        else
                            MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = uiState.error ?: uiState.message ?: "",
                            color = if (uiState.error != null)
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
            uiState.isLoading -> {
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
private fun SummaryCard(
    overdueCount: Int,
    dueSoonCount: Int,
    scheduledCount: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SummaryItem(
                count = overdueCount,
                label = "Overdue",
                color = Color(0xFFD32F2F)
            )

            VerticalDivider(modifier = Modifier.height(40.dp))

            SummaryItem(
                count = dueSoonCount,
                label = "Due Soon",
                color = Color(0xFFF57C00)
            )

            VerticalDivider(modifier = Modifier.height(40.dp))

            SummaryItem(
                count = scheduledCount,
                label = "Scheduled",
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
private fun SummaryItem(
    count: Int,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(color)
            )

            Text(
                text = count.toString(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AttentionEventCard(
    event: MaintenanceEventEntity,
    isOverdue: Boolean,
    onClick: () -> Unit,
    onComplete: () -> Unit,
    viewModel: MaintenanceTemplateViewModel,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    val daysUntilDue = viewModel.getDaysUntilDue(event)
    val template by viewModel.getTemplateById(event.templateId).collectAsStateWithLifecycle(initialValue = null)

    val borderColor = if (isOverdue) Color(0xFFD32F2F) else Color(0xFFF57C00)

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(borderColor)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = template?.title ?: "Maintenance Event",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        template?.component?.let { component ->
                            Text(
                                text = component,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = if (isOverdue) "${-daysUntilDue} days" else "$daysUntilDue days",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = borderColor
                        )

                        Text(
                            text = if (isOverdue) "OVERDUE" else "DUE SOON",
                            style = MaterialTheme.typography.labelSmall,
                            color = borderColor
                        )
                    }
                }

                HorizontalDivider()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Due: ${dateFormat.format(event.dueDate)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Button(
                        onClick = onComplete,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Complete")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompactEventCard(
    event: MaintenanceEventEntity,
    onClick: () -> Unit,
    viewModel: MaintenanceTemplateViewModel,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    val template by viewModel.getTemplateById(event.templateId).collectAsStateWithLifecycle(initialValue = null)

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = template?.title ?: "Maintenance Event",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                template?.component?.let { component ->
                    Text(
                        text = component,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = dateFormat.format(event.dueDate),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TemplateCard(
    template: MaintenanceTemplateEntity,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    viewModel: MaintenanceTemplateViewModel,
    modifier: Modifier = Modifier
) {
    val recurrenceText = viewModel.formatRecurrence(template)
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = template.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = template.component,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = recurrenceText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (template.estimatedCost != null || template.estimatedTime != null) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            template.estimatedCost?.let { cost ->
                                Text(
                                    text = "$${String.format("%.0f", cost)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            template.estimatedTime?.let { time ->
                                Text(
                                    text = "${time}min",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = { Text("Delete Template?") },
            text = {
                Text("This will delete \"${template.title}\" and all future events. This action cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompletedCard(
    event: MaintenanceEventEntity,
    onClick: () -> Unit,
    viewModel: MaintenanceTemplateViewModel,
    modifier: Modifier = Modifier
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    val template by viewModel.getTemplateById(event.templateId).collectAsStateWithLifecycle(initialValue = null)

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Completed",
                    tint = Color(0xFF388E3C),
                    modifier = Modifier.size(24.dp)
                )

                Column {
                    Text(
                        text = template?.title ?: "Maintenance Event",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = dateFormat.format(event.completedAt!!),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        event.actualCost?.let { cost ->
                            Text(
                                text = "$${String.format("%.0f", cost)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        event.actualTime?.let { time ->
                            Text(
                                text = "${time}min",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
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
