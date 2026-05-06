package org.tasks.compose.edit

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import org.jetbrains.compose.resources.stringResource
import org.tasks.compose.edit.TaskEditRow
import org.tasks.data.entity.Task
import org.tasks.kmp.org.tasks.time.DateStyle
import org.tasks.kmp.org.tasks.time.getRelativeDateTime
import org.tasks.time.DateTimeUtils2.currentTimeMillis
import kotlinx.coroutines.runBlocking
import tasks.kmp.generated.resources.Res
import tasks.kmp.generated.resources.no_due_date

@Composable
fun DueDateRow(
    dueDate: Long,
    hasDueDateAlarm: Boolean = false,
    is24HourFormat: Boolean = false,
    alwaysDisplayFullDate: Boolean = false,
    onClick: () -> Unit,
) {
    val overdue = remember(dueDate) {
        if (dueDate <= 0) {
            false
        } else if (Task.hasDueTime(dueDate)) {
            dueDate < currentTimeMillis()
        } else {
            // Date without time - compare against end of day
            dueDate < currentTimeMillis()
        }
    }

    DueDateRow(
        dueDate = if (dueDate == 0L) {
            stringResource(Res.string.no_due_date)
        } else {
            runBlocking {
                getRelativeDateTime(
                    dueDate,
                    is24HourFormat,
                    DateStyle.FULL,
                    alwaysDisplayFullDate = alwaysDisplayFullDate
                )
            }
        },
        color = when {
            overdue -> MaterialTheme.colorScheme.error
            dueDate == 0L && hasDueDateAlarm -> MaterialTheme.colorScheme.error
            dueDate == 0L -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            else -> MaterialTheme.colorScheme.onSurface
        },
        onClick = { onClick() },
    )
}

@Composable
private fun DueDateRow(
    dueDate: String,
    color: Color,
    onClick: () -> Unit,
) {
    TaskEditRow(
        icon = {
            Text(
                text = "📅",
                style = MaterialTheme.typography.titleMedium,
            )
        },
        content = {
            DueDate(
                dueDate = dueDate,
                color = color,
            )
        },
        onClick = onClick,
    )
}

@Composable
fun DueDate(
    dueDate: String,
    color: Color,
) {
    Text(
        text = dueDate,
        color = color,
        modifier = Modifier.padding(top = 20.dp, bottom = 20.dp, end = 16.dp)
    )
}
