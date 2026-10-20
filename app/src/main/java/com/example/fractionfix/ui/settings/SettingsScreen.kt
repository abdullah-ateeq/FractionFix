package com.example.fractionfix.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fractionfix.data.model.FractionFixUiState
import com.example.fractionfix.data.model.RoundingModeOption
import com.example.fractionfix.data.model.ThemeMode
import com.example.fractionfix.ui.FractionFixViewModel

@Composable
fun SettingsScreen(
    uiState: FractionFixUiState,
    viewModel: FractionFixViewModel,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val scrollState = rememberScrollState()

    var showClearHistoryDialog by remember { mutableStateOf(false) }
    var showResetSettingsDialog by remember { mutableStateOf(false) }

    fun triggerHaptic() {
        if (uiState.hapticFeedbackEnabled) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
    }

    // Clear History Dialog
    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            title = { Text("Clear History") },
            text = { Text("Clear all recent fraction calculations?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        triggerHaptic()
                        viewModel.clearAllHistory()
                        showClearHistoryDialog = false
                    }
                ) {
                    Text("Clear", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Reset Settings Dialog
    if (showResetSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showResetSettingsDialog = false },
            title = { Text("Reset Settings") },
            text = { Text("Reset all preferences to default values? History will not be affected.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        triggerHaptic()
                        viewModel.resetSettings()
                        showResetSettingsDialog = false
                    }
                ) {
                    Text("Reset", color = MaterialTheme.colorScheme.primary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetSettingsDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Title
        Column(modifier = Modifier.padding(bottom = 16.dp)) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Preferences and display configuration",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Appearance Section
        SettingsSectionHeader(title = "APPEARANCE")
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Theme Mode Selector
                ThemeModeRow(
                    selectedTheme = uiState.themeMode,
                    onThemeSelected = {
                        triggerHaptic()
                        viewModel.updateThemeMode(it)
                    }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Dynamic Color Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Dynamic Color",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Adapt colors to your system wallpaper (Android 12+)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = uiState.dynamicColor,
                        onCheckedChange = {
                            triggerHaptic()
                            viewModel.updateDynamicColor(it)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Behavior Section
        SettingsSectionHeader(title = "BEHAVIOR")
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Haptic Feedback
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Haptic Feedback",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Tactile vibration for touches and calculations",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = uiState.hapticFeedbackEnabled,
                        onCheckedChange = {
                            viewModel.updateHapticFeedback(it)
                            if (it) triggerHaptic()
                        }
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Animations Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Animations",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Smooth transitions when results update",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = uiState.animationsEnabled,
                        onCheckedChange = {
                            triggerHaptic()
                            viewModel.updateAnimations(it)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Decimal Display Section
        SettingsSectionHeader(title = "DECIMAL DISPLAY")
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Precision Selector (4, 6, 8, 10, 12)
                PrecisionRow(
                    currentPrecision = uiState.decimalPrecision,
                    onPrecisionSelected = {
                        triggerHaptic()
                        viewModel.updateDecimalPrecision(it)
                    }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Rounding Mode Selector
                RoundingModeRow(
                    currentRounding = uiState.roundingMode,
                    onRoundingSelected = {
                        triggerHaptic()
                        viewModel.updateRoundingMode(it)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // History Section
        SettingsSectionHeader(title = "HISTORY & STORAGE")
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Confirm History Delete
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Confirm History Delete",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Ask for confirmation before deleting calculations",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = uiState.confirmHistoryDelete,
                        onCheckedChange = {
                            triggerHaptic()
                            viewModel.updateConfirmHistoryDelete(it)
                        }
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Preserve Last Input
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Preserve Last Input",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Restore previous values on next app launch",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = uiState.preserveLastInput,
                        onCheckedChange = {
                            triggerHaptic()
                            viewModel.updatePreserveLastInput(it)
                        }
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                // Action Buttons: Clear History & Reset Settings
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { showClearHistoryDialog = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Clear History", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = { showResetSettingsDialog = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Reset Settings", fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp
        ),
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
    )
}

@Composable
private fun ThemeModeRow(
    selectedTheme: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Theme",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Choose light, dark, or system appearance",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Box {
            TextButton(onClick = { expanded = true }) {
                Text(
                    text = selectedTheme.displayName,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                ThemeMode.entries.forEach { mode ->
                    DropdownMenuItem(
                        text = { Text(mode.displayName) },
                        onClick = {
                            onThemeSelected(mode)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PrecisionRow(
    currentPrecision: Int,
    onPrecisionSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val precisionOptions = listOf(4, 6, 8, 10, 12)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Decimal Precision",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Number of decimal places displayed",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Box {
            TextButton(onClick = { expanded = true }) {
                Text(
                    text = "$currentPrecision digits",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                precisionOptions.forEach { p ->
                    DropdownMenuItem(
                        text = { Text("$p decimal places") },
                        onClick = {
                            onPrecisionSelected(p)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun RoundingModeRow(
    currentRounding: RoundingModeOption,
    onRoundingSelected: (RoundingModeOption) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Rounding Mode",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Method for rounding finite decimal results",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Box {
            TextButton(onClick = { expanded = true }) {
                Text(
                    text = currentRounding.displayName,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                RoundingModeOption.entries.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.displayName) },
                        onClick = {
                            onRoundingSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
