package org.tasks.compose.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateAndTimeScreen(
    fullDateEnabled: Boolean,
    morningSummary: String,
    afternoonSummary: String,
    eveningSummary: String,
    nightSummary: String,
    autoDismissListEnabled: Boolean,
    autoDismissEditEnabled: Boolean,
    autoDismissWidgetEnabled: Boolean,
    onFullDate: (Boolean) -> Unit,
    onMorning: (Int) -> Unit,
    onAfternoon: (Int) -> Unit,
    onEvening: (Int) -> Unit,
    onNight: (Int) -> Unit,
    onAutoDismissInfo: () -> Unit,
    onAutoDismissList: (Boolean) -> Unit,
    onAutoDismissEdit: (Boolean) -> Unit,
    onAutoDismissWidget: (Boolean) -> Unit,
    showAutoDismissInfo: Boolean = false,
    onDismissAutoDismissInfo: () -> Unit = {},
) {
    var showMorningPicker by remember { mutableStateOf(false) }
    var showAfternoonPicker by remember { mutableStateOf(false) }
    var showEveningPicker by remember { mutableStateOf(false) }
    var showNightPicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Full date island
        SettingsItemCard(modifier = Modifier.padding(horizontal = 16.dp)) {
            SwitchPreferenceRow(
                title = "Always display full date",
                checked = fullDateEnabled,
                onCheckedChange = onFullDate,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Time shortcuts island
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            SettingsItemCard(position = CardPosition.First) {
                PreferenceRow(
                    title = "Morning",
                    summary = morningSummary,
                    onClick = { showMorningPicker = true },
                )
            }
            SettingsItemCard(position = CardPosition.Middle) {
                PreferenceRow(
                    title = "Afternoon",
                    summary = afternoonSummary,
                    onClick = { showAfternoonPicker = true },
                )
            }
            SettingsItemCard(position = CardPosition.Middle) {
                PreferenceRow(
                    title = "Evening",
                    summary = eveningSummary,
                    onClick = { showEveningPicker = true },
                )
            }
            SettingsItemCard(position = CardPosition.Last) {
                PreferenceRow(
                    title = "Night",
                    summary = nightSummary,
                    onClick = { showNightPicker = true },
                )
            }
        }

        // Autoclose date time picker section
        SectionHeader(
            "Auto-dismiss date time picker",
            modifier = Modifier.padding(horizontal = 16.dp),
            onClick = onAutoDismissInfo,
        )
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            SettingsItemCard(position = CardPosition.First) {
                SwitchPreferenceRow(
                    title = "Task list",
                    summary = "Close date time picker after choosing a time on task list",
                    checked = autoDismissListEnabled,
                    onCheckedChange = onAutoDismissList,
                )
            }
            SettingsItemCard(position = CardPosition.Middle) {
                SwitchPreferenceRow(
                    title = "Task edit",
                    summary = "Close date time picker after choosing a time on task edit",
                    checked = autoDismissEditEnabled,
                    onCheckedChange = onAutoDismissEdit,
                )
            }
            SettingsItemCard(position = CardPosition.Last) {
                SwitchPreferenceRow(
                    title = "Widget",
                    summary = "Close date time picker after choosing a time on widget",
                    checked = autoDismissWidgetEnabled,
                    onCheckedChange = onAutoDismissWidget,
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (showAutoDismissInfo) {
            BasicAlertDialog(
                onDismissRequest = onDismissAutoDismissInfo,
            ) {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surface,
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = "Auto-dismiss date time picker",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 16.dp),
                        )
                        Text(
                            text = "When enabled, the date time picker will automatically close after you select a time. This can be useful for quickly setting times without having to manually close the picker.",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 24.dp),
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                        ) {
                            TextButton(onClick = onDismissAutoDismissInfo) {
                                Text("OK")
                            }
                        }
                    }
                }
            }
        }

        if (showMorningPicker) {
            TimePickerDialog(
                initialTimeMillis = parseTimeString(morningSummary),
                onTimeSelected = {
                    onMorning(it)
                    showMorningPicker = false
                },
                onDismiss = { showMorningPicker = false },
            )
        }

        if (showAfternoonPicker) {
            TimePickerDialog(
                initialTimeMillis = parseTimeString(afternoonSummary),
                onTimeSelected = {
                    onAfternoon(it)
                    showAfternoonPicker = false
                },
                onDismiss = { showAfternoonPicker = false },
            )
        }

        if (showEveningPicker) {
            TimePickerDialog(
                initialTimeMillis = parseTimeString(eveningSummary),
                onTimeSelected = {
                    onEvening(it)
                    showEveningPicker = false
                },
                onDismiss = { showEveningPicker = false },
            )
        }

        if (showNightPicker) {
            TimePickerDialog(
                initialTimeMillis = parseTimeString(nightSummary),
                onTimeSelected = {
                    onNight(it)
                    showNightPicker = false
                },
                onDismiss = { showNightPicker = false },
            )
        }
    }
}

fun parseTimeString(timeString: String): Int {
    return try {
        val parts = timeString.split(":")
        val hour = parts[0].toInt()
        val minute = parts[1].toInt()
        (hour * 3600 + minute * 60) * 1000
    } catch (e: Exception) {
        9 * 3600 * 1000 // Default to 9:00
    }
}
