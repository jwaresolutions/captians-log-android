package com.captainslog.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class BreadcrumbItem(
    val label: String,
    val onClick: (() -> Unit)? = null // null = current (not clickable)
)

/**
 * Consistent top bar used across all screens in the app.
 * Features purple background with breadcrumb navigation on left and action buttons on right.
 * Supports highlighting active buttons.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    breadcrumbs: List<BreadcrumbItem>,
    onQrImportClick: () -> Unit = {},
    qrImportActive: Boolean = false,
    onNotesClick: () -> Unit = {},
    onTodosClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    notesActive: Boolean = false,
    todosActive: Boolean = false,
    settingsActive: Boolean = false,
    onTitleClick: (() -> Unit)? = null
) {
    TopAppBar(
        navigationIcon = {},
        title = {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // "Captain's Log" is always first, clickable to go home
                val isHomeOnly = breadcrumbs.size == 1 && breadcrumbs.first().label == "Captain's Log"
                Text(
                    text = "Captain's Log",
                    fontWeight = if (isHomeOnly) FontWeight.Bold else FontWeight.Normal,
                    color = if (isHomeOnly) Color.White else Color.White.copy(alpha = 0.7f),
                    modifier = if (!isHomeOnly && onTitleClick != null) {
                        Modifier.clickable { onTitleClick.invoke() }
                    } else {
                        Modifier
                    }
                )
                // Show remaining breadcrumbs (skip "Captain's Log" if it's the first)
                val crumbsToShow = if (breadcrumbs.firstOrNull()?.label == "Captain's Log") {
                    breadcrumbs.drop(1)
                } else {
                    breadcrumbs
                }
                crumbsToShow.forEachIndexed { index, item ->
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                    val isLast = index == crumbsToShow.lastIndex
                    Text(
                        text = item.label,
                        fontWeight = if (isLast) FontWeight.Bold else FontWeight.Normal,
                        color = if (isLast) Color.White else Color.White.copy(alpha = 0.7f),
                        modifier = if (!isLast && item.onClick != null) {
                            Modifier.clickable { item.onClick.invoke() }
                        } else {
                            Modifier
                        }
                    )
                }
            }
        },
        actions = {
            // QR Import button
            IconButton(onClick = onQrImportClick) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Import via QR",
                    tint = if (qrImportActive) Color.Yellow else Color.White
                )
            }

            // Notes button
            IconButton(onClick = onNotesClick) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Notes",
                    tint = if (notesActive) Color.Yellow else Color.White
                )
            }

            // Todos button
            IconButton(onClick = onTodosClick) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Todos",
                    tint = if (todosActive) Color.Yellow else Color.White
                )
            }

            // Settings button
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings",
                    tint = if (settingsActive) Color.Yellow else Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF9C27B0), // Purple color
            titleContentColor = Color.White,
            actionIconContentColor = Color.White
        )
    )
}
