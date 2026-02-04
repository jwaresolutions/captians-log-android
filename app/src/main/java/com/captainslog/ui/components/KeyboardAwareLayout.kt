package com.captainslog.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * A keyboard-aware layout that automatically adjusts when the virtual keyboard appears.
 * This ensures that input fields remain visible and accessible when typing.
 * 
 * Features:
 * - Automatically handles keyboard insets
 * - Provides scrolling when content is larger than available space
 * - Ensures focused input fields stay visible above the keyboard
 */
@Composable
fun KeyboardAwareColumn(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.ime)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = verticalArrangement,
        content = content
    )
}

/**
 * A keyboard-aware layout specifically designed for forms with input fields.
 * Provides proper spacing and ensures all fields remain accessible.
 */
@Composable
fun KeyboardAwareFormColumn(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    KeyboardAwareColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        content = content
    )
}