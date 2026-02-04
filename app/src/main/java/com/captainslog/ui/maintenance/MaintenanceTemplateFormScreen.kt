package com.captainslog.ui.maintenance

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.captainslog.viewmodel.BoatViewModel
import com.captainslog.viewmodel.MaintenanceTemplateViewModel
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaintenanceTemplateFormScreen(
    templateId: String?,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MaintenanceTemplateViewModel,
    boatViewModel: BoatViewModel
) {
    val boats by boatViewModel.getAllBoats().collectAsStateWithLifecycle(initialValue = emptyList())
    val template by remember(templateId) {
        if (templateId != null) {
            viewModel.getTemplateById(templateId)
        } else {
            flowOf(null)
        }
    }.collectAsStateWithLifecycle(initialValue = null)

    var selectedBoatId by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var component by remember { mutableStateOf("") }
    var estimatedCost by remember { mutableStateOf("") }
    var estimatedTime by remember { mutableStateOf("") }
    var recurrenceType by remember { mutableStateOf("months") }
    var recurrenceInterval by remember { mutableStateOf("6") }
    var expanded by remember { mutableStateOf(false) }

    // Initialize form with template data if editing
    LaunchedEffect(template) {
        template?.let { tmpl ->
            selectedBoatId = tmpl.boatId
            title = tmpl.title
            description = tmpl.description
            component = tmpl.component
            estimatedCost = tmpl.estimatedCost?.toString() ?: ""
            estimatedTime = tmpl.estimatedTime?.toString() ?: ""
            recurrenceType = tmpl.recurrenceType
            recurrenceInterval = tmpl.recurrenceInterval.toString()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .windowInsetsPadding(WindowInsets.ime),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Boat selection
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = boats.find { it.id == selectedBoatId }?.name ?: "Select Boat",
                onValueChange = { },
                readOnly = true,
                label = { Text("Boat") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                boats.forEach { boat ->
                    DropdownMenuItem(
                        text = { Text(boat.name) },
                        onClick = {
                            selectedBoatId = boat.id
                            expanded = false
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )

        OutlinedTextField(
            value = component,
            onValueChange = { component = it },
            label = { Text("Component/System") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = estimatedCost,
                onValueChange = { estimatedCost = it },
                label = { Text("Estimated Cost") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
                value = estimatedTime,
                onValueChange = { estimatedTime = it },
                label = { Text("Time (min)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }

        Text(
            text = "Recurrence Schedule",
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Every")

            OutlinedTextField(
                value = recurrenceInterval,
                onValueChange = { recurrenceInterval = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.width(80.dp)
            )

            var typeExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = typeExpanded,
                onExpandedChange = { typeExpanded = !typeExpanded }
            ) {
                OutlinedTextField(
                    value = recurrenceType,
                    onValueChange = { },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                    modifier = Modifier
                        .weight(1f)
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = typeExpanded,
                    onDismissRequest = { typeExpanded = false }
                ) {
                    listOf("days", "weeks", "months", "years", "engine_hours").forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                recurrenceType = type
                                typeExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val cost = estimatedCost.toDoubleOrNull()
                val time = estimatedTime.toIntOrNull()
                val interval = recurrenceInterval.toIntOrNull() ?: 1

                if (templateId == null) {
                    viewModel.createTemplate(
                        boatId = selectedBoatId,
                        title = title,
                        description = description,
                        component = component,
                        estimatedCost = cost,
                        estimatedTime = time,
                        recurrenceType = recurrenceType,
                        recurrenceInterval = interval
                    )
                } else {
                    template?.let { tmpl ->
                        viewModel.updateTemplate(
                            tmpl.copy(
                                boatId = selectedBoatId,
                                title = title,
                                description = description,
                                component = component,
                                estimatedCost = cost,
                                estimatedTime = time,
                                recurrenceType = recurrenceType,
                                recurrenceInterval = interval
                            )
                        )
                    }
                }
                onNavigateBack()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedBoatId.isNotEmpty() && title.isNotEmpty() &&
                     description.isNotEmpty() && component.isNotEmpty()
        ) {
            Text(if (templateId == null) "Create Template" else "Update Template")
        }
    }
}
