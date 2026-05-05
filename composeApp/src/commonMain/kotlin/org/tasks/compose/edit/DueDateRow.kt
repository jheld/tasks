package org.tasks.compose.edit

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.tasks.compose.edit.TaskEditRow
import org.tasks.data.entity.Task
import org.tasks.kmp.org.tasks.time.DateStyle
import org.tasks.kmp.org.tasks.time.getRelativeDateTime
import org.tasks.time.DateTimeUtils2.currentTimeMillis

@Composable
fun DueDateRow(
    dueDate: Long,
    hasDueDateAlarm: Boolean = false,
    is24HourFormat: Boolean = false,
    alwaysDisplayFullDate: Boolean = false,
    onClick: () -> Unit,
) {
    val overdue = remember(dueDate) {
        if (dueDate <= 0) false
        else if (Task.hasDueTime(dueDate)) {
            dueDate < currentTimeMillis()
        } else {
            dueDate < currentTimeMillis()
        }
    }
    // For now, just show simple date or "No due date"
    val displayText = if (dueDate == 0L) {
        "No due date"
    } else {
        // Simple date display - in production this would use getRelativeDateTime
        "Due: $dueDate"
    }
    val color = when {
        overdue -> MaterialTheme.colorScheme.error
        dueDate == 0L && hasDueDateAlarm -> MaterialTheme.colorScheme.error
        dueDate == 0L -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        else -> MaterialTheme.colorScheme.onSurface
    }
    TaskEditRow(
        icon = {
            Text(
                text = "📅",
                style = MaterialTheme.typography.titleMedium,
            )
        },
        content = {
            Text(
                text = displayText,
                style = MaterialTheme.typography.bodyLarge,
                color = color,
            )
        },
        onClick = onClick,
    )
}
