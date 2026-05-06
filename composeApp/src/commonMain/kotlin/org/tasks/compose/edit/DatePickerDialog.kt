package org.tasks.compose.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.NightsStay
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.tasks.data.entity.Task
import org.tasks.time.DateTimeUtils2.currentTimeMillis

/**
 * Convert local time millis to UTC midnight of the same local calendar date.
 * This is what DatePicker expects as input (initialSelectedDateMillis).
 *
 * Matches DateTime.toUtcDateMillis() from Android app:
 *   val dt = DateTime(localMillis)
 *   return DateTime(dt.year, dt.monthOfYear, dt.dayOfMonth, timeZone = UTC).millis
 */
fun toUtcDateMillis(localMillis: Long): Long {
    if (localMillis <= 0) return 0L
    val localDateTime = Instant.fromEpochMilliseconds(localMillis)
        .toLocalDateTime(TimeZone.currentSystemDefault())
    // Create LocalDateTime at midnight in local timezone date
    val localMidnight = LocalDateTime(
        localDateTime.year,
        localDateTime.monthNumber,
        localDateTime.dayOfMonth,
        0, 0, 0, 0
    )
    // Convert to UTC instant (interpret localMidnight as being in UTC timezone)
    return localMidnight.toInstant(TimeZone.UTC).toEpochMilliseconds()
}

/**
 * Convert UTC midnight millis (from DatePicker) to local time millis of the same calendar date.
 * This gives us the local date that the user picked.
 *
 * Matches DateTime.toLocalDateMillis() from Android app:
 *   val dt = DateTime(utcMillis, UTC)
 *   return DateTime(dt.year, dt.monthOfYear, dt.dayOfMonth).millis
 */
fun toLocalDateMillis(utcMillis: Long): Long {
    if (utcMillis <= 0) return 0L
    // Interpret utcMillis as UTC datetime
    val utcDateTime = Instant.fromEpochMilliseconds(utcMillis)
        .toLocalDateTime(TimeZone.UTC)
    // Create LocalDateTime at midnight with that UTC date
    val localMidnight = LocalDateTime(
        utcDateTime.year,
        utcDateTime.monthNumber,
        utcDateTime.dayOfMonth,
        0, 0, 0, 0
    )
    // Convert to instant in local timezone (this gives UTC millis of local midnight)
    return localMidnight.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    initialDate: Long,
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit,
) {
    var selectedHour by remember { mutableStateOf(0) }
    var selectedMinute by remember { mutableStateOf(0) }
    var showTimePicker by remember { mutableStateOf(false) }

    // Initialize time from initialDate if it has time, otherwise use current time
    LaunchedEffect(initialDate) {
        if (initialDate > 0 && Task.hasDueTime(initialDate)) {
            // Task has a due time - extract local time from the timestamp
            val localDateTime = Instant.fromEpochMilliseconds(initialDate)
                .toLocalDateTime(TimeZone.currentSystemDefault())
            selectedHour = localDateTime.hour
            selectedMinute = localDateTime.minute
        } else {
            // No due time - use current local time
            val now = currentTimeMillis()
            val localDateTime = Instant.fromEpochMilliseconds(now)
                .toLocalDateTime(TimeZone.currentSystemDefault())
            selectedHour = localDateTime.hour
            selectedMinute = localDateTime.minute
        }
    }

    // Date picker needs UTC midnight of the local date to display
    val dateForPicker = toUtcDateMillis(if (initialDate > 0) initialDate else currentTimeMillis())

    BasicAlertDialog(
        onDismissRequest = onDismiss,
    ) {
        BoxWithConstraints {
            val dialogWidth = min(maxWidth, 400.dp)
            val dialogHeight = min(maxHeight, 700.dp)
            val useCompactMode = maxHeight < 600.dp
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = dateForPicker,
                initialDisplayMode = if (useCompactMode) DisplayMode.Input else DisplayMode.Picker,
            )
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .sizeIn(maxWidth = dialogWidth, maxHeight = dialogHeight)
                    .padding(16.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "Select Due Date & Time",
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    DatePicker(
                        state = datePickerState,
                        showModeToggle = !useCompactMode,
                        title = { },
                    )

                    // Time shortcuts
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp, alignment = Alignment.CenterHorizontally),
                    ) {
                        TimeShortcutButton(
                            icon = Icons.Outlined.WbSunny,
                            text = "Morning",
                            selected = selectedHour == 9,
                            onClick = {
                                selectedHour = 9
                                selectedMinute = 0
                            }
                        )
                        TimeShortcutButton(
                            icon = Icons.Outlined.Coffee,
                            text = "Afternoon",
                            selected = selectedHour == 14,
                            onClick = {
                                selectedHour = 14
                                selectedMinute = 0
                            }
                        )
                        TimeShortcutButton(
                            icon = Icons.Outlined.WbSunny,
                            text = "Evening (6PM)",
                            selected = selectedHour == 18,
                            onClick = {
                                selectedHour = 18
                                selectedMinute = 0
                            }
                        )
                        TimeShortcutButton(
                            icon = Icons.Outlined.AccessTime,
                            text = "Night (9PM)",
                            selected = selectedHour == 21,
                            onClick = {
                                selectedHour = 21
                                selectedMinute = 0
                            }
                        )
                    }

                    // Selected time display and time picker toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // Show selected time - clickable to open time picker
                        val timeDisplay = "%02d:%02d".format(selectedHour, selectedMinute)
                        val isCustomTime = selectedHour !in listOf(9, 14, 18, 21) ||
                                (selectedHour in listOf(9, 14, 18, 21) && selectedMinute != 0)
                        TextButton(
                            onClick = { showTimePicker = true },
                            colors = androidx.compose.material3.ButtonDefaults.textButtonColors(
                                contentColor = if (isCustomTime)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurface,
                            )
                        ) {
                            Text("Time: $timeDisplay")
                        }
                        // Custom time picker button
                        TextButton(onClick = { showTimePicker = true }) {
                            Text("Custom Time")
                        }
                    }

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("Cancel")
                        }
                        Button(onClick = {
                            val selectedDateMillis = datePickerState.selectedDateMillis
                                ?: dateForPicker
                            // Convert selectedDateMillis (UTC midnight) to local date
                            // This matches DateTime.toLocalDateMillis() from Android app
                            val localDateMillis = toLocalDateMillis(selectedDateMillis)
                            val localDate = Instant.fromEpochMilliseconds(localDateMillis)
                                .toLocalDateTime(TimeZone.currentSystemDefault()).date
                            // Build LocalDateTime with selected time on that local date
                            val localDateTime = LocalDateTime(
                                localDate.year,
                                localDate.monthNumber,
                                localDate.dayOfMonth,
                                selectedHour,
                                selectedMinute,
                                1, // 1 second indicates specific time (not just a date)
                                0
                            )
                            // Convert back to UTC milliseconds
                            val result = localDateTime
                                .toInstant(TimeZone.currentSystemDefault())
                                .toEpochMilliseconds()
                            onDateSelected(result)
                        }) {
                            Text("OK")
                        }
                    }
                }
            }
        }
    }

    // Time Picker Dialog
    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = selectedHour,
            initialMinute = selectedMinute,
        )
        BasicAlertDialog(onDismissRequest = { showTimePicker = false }) {
            BoxWithConstraints {
                val timeDialogWidth = min(maxWidth, 400.dp)
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .sizeIn(maxWidth = timeDialogWidth)
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text("Select Time", style = MaterialTheme.typography.titleMedium)
                        TimePicker(state = timePickerState)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                        ) {
                            TextButton(onClick = { showTimePicker = false }) {
                                Text("Cancel")
                            }
                            Button(onClick = {
                                selectedHour = timePickerState.hour
                                selectedMinute = timePickerState.minute
                                showTimePicker = false
                            }) {
                                Text("OK")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TimeShortcutButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
    TextButton(
        onClick = onClick,
        colors = androidx.compose.material3.ButtonDefaults.textButtonColors(contentColor = color)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = color,
            )
        }
    }
}
