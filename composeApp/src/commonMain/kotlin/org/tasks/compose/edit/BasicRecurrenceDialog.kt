package org.tasks.compose.edit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import tasks.kmp.generated.resources.Res
import tasks.kmp.generated.resources.repeat_custom
import tasks.kmp.generated.resources.repeat_option_custom
import tasks.kmp.generated.resources.repeat_option_does_not_repeat
import tasks.kmp.generated.resources.repeat_option_every_day
import tasks.kmp.generated.resources.repeat_option_every_month
import tasks.kmp.generated.resources.repeat_option_every_week
import tasks.kmp.generated.resources.repeat_option_every_year
import tasks.kmp.generated.resources.cancel
import tasks.kmp.generated.resources.ok

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasicRecurrenceDialog(
    recurrence: String?,
    onDismiss: () -> Unit,
    onRecurrenceSelected: (String?) -> Unit,
) {
    val options = listOf(
        stringResource(Res.string.repeat_option_does_not_repeat),
        stringResource(Res.string.repeat_option_every_day),
        stringResource(Res.string.repeat_option_every_week),
        stringResource(Res.string.repeat_option_every_month),
        stringResource(Res.string.repeat_option_every_year),
        stringResource(Res.string.repeat_option_custom),
    )

    val initialSelected = remember(recurrence) { indexForRrule(recurrence) }
    var selectedIndex by remember { mutableStateOf(initialSelected) }
    var showCustomField by remember { mutableStateOf(initialSelected == 5) }
    var customRrule by remember { mutableStateOf(if (initialSelected == 5) recurrence ?: "" else "") }

    BasicAlertDialog(
        onDismissRequest = onDismiss,
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.sizeIn(maxWidth = 400.dp, maxHeight = 600.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
            ) {
                Text(
                    text = stringResource(Res.string.repeat_custom),
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                ) {
                    options.forEachIndexed { index, label ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedIndex = index
                                    if (index != 5) {
                                        showCustomField = false
                                    } else {
                                        showCustomField = true
                                    }
                                }
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = selectedIndex == index,
                                onClick = {
                                    selectedIndex = index
                                    if (index != 5) {
                                        showCustomField = false
                                    } else {
                                        showCustomField = true
                                    }
                                },
                            )
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 16.dp),
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                        if (index < options.lastIndex) {
                            HorizontalDivider()
                        }
                    }
                    if (showCustomField) {
                        Spacer(modifier = Modifier.height(16.dp))
                        TextField(
                            value = customRrule,
                            onValueChange = { customRrule = it },
                            label = { Text("RRULE") },
                            placeholder = { Text("FREQ=WEEKLY;BYDAY=MO,WE,FR") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(Res.string.cancel))
                    }
                    TextButton(
                        onClick = {
                            val result = when (selectedIndex) {
                                0 -> null
                                1 -> "FREQ=DAILY;INTERVAL=1"
                                2 -> "FREQ=WEEKLY;INTERVAL=1"
                                3 -> "FREQ=MONTHLY;INTERVAL=1"
                                4 -> "FREQ=YEARLY;INTERVAL=1"
                                5 -> customRrule.ifBlank { null }
                                else -> null
                            }
                            onRecurrenceSelected(result)
                            onDismiss()
                        },
                    ) {
                        Text(stringResource(Res.string.ok))
                    }
                }
            }
        }
    }
}

private fun indexForRrule(recurrence: String?): Int {
    if (recurrence.isNullOrBlank()) return 0
    val r = recurrence.uppercase().trim()
    if (!r.contains("FREQ=")) return 5
    if (r.contains("FREQ=DAILY") && r.let { !it.contains("BYDAY") && !it.contains("UNTIL") && !it.contains("COUNT") && it.contains("INTERVAL=1") }) return 1
    if (r.contains("FREQ=WEEKLY") && r.let { !it.contains("BYDAY") && !it.contains("UNTIL") && !it.contains("COUNT") && it.contains("INTERVAL=1") }) return 2
    if (r.contains("FREQ=MONTHLY") && r.let { !it.contains("BYDAY") && !it.contains("UNTIL") && !it.contains("COUNT") && it.contains("INTERVAL=1") }) return 3
    if (r.contains("FREQ=YEARLY") && r.let { !it.contains("BYDAY") && !it.contains("UNTIL") && !it.contains("COUNT") && it.contains("INTERVAL=1") }) return 4
    return 5
}
