package org.tasks.compose.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import tasks.kmp.generated.resources.Res
import tasks.kmp.generated.resources.ok

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    initialTimeMillis: Int,
    onTimeSelected: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val totalSeconds = initialTimeMillis / 1000
    var hour by remember { mutableIntStateOf((totalSeconds / 3600) % 24) }
    var minute by remember { mutableIntStateOf((totalSeconds % 3600) / 60) }
    var showHourPicker by remember { mutableStateOf(false) }
    var showMinutePicker by remember { mutableStateOf(false) }

    BasicAlertDialog(
        onDismissRequest = onDismiss,
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Select Time",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp),
                )
                Column(
                    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 24.dp),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        // Hour selector
                        Button(onClick = { showHourPicker = true }) {
                            Text(String.format("%02d", hour))
                        }
                        Text(":", Modifier.padding(top = 8.dp))
                        // Minute selector
                        Button(onClick = { showMinutePicker = true }) {
                            Text(String.format("%02d", minute))
                        }
                    }

                    if (showHourPicker) {
                        SimpleNumberPicker(
                            title = "Hour",
                            range = 0..23,
                            initialValue = hour,
                            onValueSelected = {
                                hour = it
                                showHourPicker = false
                            },
                            onDismiss = { showHourPicker = false },
                        )
                    }

                    if (showMinutePicker) {
                        SimpleNumberPicker(
                            title = "Minute",
                            range = 0..59,
                            initialValue = minute,
                            onValueSelected = {
                                minute = it
                                showMinutePicker = false
                            },
                            onDismiss = { showMinutePicker = false },
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    TextButton(onClick = {
                        val millisOfDay = (hour * 3600 + minute * 60) * 1000
                        onTimeSelected(millisOfDay)
                    }) {
                        Text(stringResource(Res.string.ok))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleNumberPicker(
    title: String,
    range: IntRange,
    initialValue: Int,
    onValueSelected: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    BasicAlertDialog(
        onDismissRequest = onDismiss,
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp),
                )
                Column(
                    modifier = Modifier.height(300.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    range.forEach { value ->
                        Button(
                            onClick = { onValueSelected(value) },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(String.format("%02d", value))
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}
