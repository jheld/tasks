package org.tasks.compose.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.tasks.compose.edit.TaskEditRow
import org.tasks.data.entity.Task

@Composable
fun RepeatRow(
    recurrence: String?,
    repeatFrom: Int,
    onClick: () -> Unit,
    onRepeatFromChanged: (Int) -> Unit,
) {
    TaskEditRow(
        icon = {
            Text(
                text = "🔄",
                style = MaterialTheme.typography.titleMedium,
            )
        },
        content = {
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Spacer(modifier = Modifier.height(12.dp))
                if (recurrence.isNullOrBlank()) {
                    Text(
                        text = "Does not repeat",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(end = 16.dp),
                    )
                } else {
                    Text(
                        text = recurrence,
                        modifier = Modifier.padding(end = 16.dp),
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Repeats from: ",
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        OutlinedButton(
                            onClick = {
                                onRepeatFromChanged(
                                    if (repeatFrom == Task.RepeatFrom.DUE_DATE)
                                        Task.RepeatFrom.COMPLETION_DATE
                                    else
                                        Task.RepeatFrom.DUE_DATE
                                )
                            },
                        ) {
                            Text(
                                text = if (repeatFrom == Task.RepeatFrom.COMPLETION_DATE)
                                    "Completion"
                                else
                                    "Due date",
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        },
        onClick = onClick,
    )
}
