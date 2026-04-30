package org.tasks.compose.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
    onMorning: () -> Unit,
    onAfternoon: () -> Unit,
    onEvening: () -> Unit,
    onNight: () -> Unit,
    onAutoDismissInfo: () -> Unit,
    onAutoDismissList: (Boolean) -> Unit,
    onAutoDismissEdit: (Boolean) -> Unit,
    onAutoDismissWidget: (Boolean) -> Unit,
    showAutoDismissInfo: Boolean = false,
    onDismissAutoDismissInfo: () -> Unit = {},
) {
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
                    onClick = onMorning,
                )
            }
            SettingsItemCard(position = CardPosition.Middle) {
                PreferenceRow(
                    title = "Afternoon",
                    summary = afternoonSummary,
                    onClick = onAfternoon,
                )
            }
            SettingsItemCard(position = CardPosition.Middle) {
                PreferenceRow(
                    title = "Evening",
                    summary = eveningSummary,
                    onClick = onEvening,
                )
            }
            SettingsItemCard(position = CardPosition.Last) {
                PreferenceRow(
                    title = "Night",
                    summary = nightSummary,
                    onClick = onNight,
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
            AlertDialog(
                onDismissRequest = onDismissAutoDismissInfo,
                title = { Text("Auto-dismiss date time picker") },
                text = { Text("When enabled, the date time picker will automatically close after you select a time. This can be useful for quickly setting times without having to manually close the picker.") },
                confirmButton = {
                    TextButton(onClick = onDismissAutoDismissInfo) {
                        Text("OK")
                    }
                },
            )
        }
    }
}
