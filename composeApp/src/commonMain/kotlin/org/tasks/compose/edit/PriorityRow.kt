package org.tasks.compose.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.tasks.compose.edit.TaskEditRow
import org.tasks.data.entity.Task

@Composable
fun PriorityRow(
    priority: Int,
    onChangePriority: (Int) -> Unit,
) {
    TaskEditRow(
        icon = {
            Text(
                text = "!",
                style = MaterialTheme.typography.titleMedium,
                color = priorityColor(priority),
            )
        },
        content = {
            PriorityLabeled(
                selected = priority,
                onClick = { onChangePriority(it) },
            )
        },
    )
}

@Composable
fun PriorityLabeled(
    selected: Int,
    onClick: (Int) -> Unit = {},
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Importance",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(end = 16.dp),
        )
        Spacer(modifier = Modifier.weight(1f))
        Priority(
            selected = selected,
            onClick = onClick,
        )
    }
}

@Composable
fun Priority(
    selected: Int,
    onClick: (Int) -> Unit = {},
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        for (i in Task.Priority.NONE downTo Task.Priority.HIGH) {
            PriorityButton(
                priority = i,
                selected = selected,
                onClick = onClick,
            )
        }
    }
}

@Composable
fun PriorityButton(
    @Task.Priority priority: Int,
    selected: Int,
    onClick: (Int) -> Unit,
) {
    val color = priorityColor(priority)
    RadioButton(
        selected = priority == selected,
        onClick = { onClick(priority) },
        colors = RadioButtonDefaults.colors(
            selectedColor = color,
            unselectedColor = color.copy(alpha = 0.5f),
        ),
    )
}

@Composable
fun priorityColor(priority: Int): Color {
    return when (priority) {
        Task.Priority.HIGH -> Color(0xFFE65100)
        Task.Priority.MEDIUM -> Color(0xFFF57C00)
        Task.Priority.LOW -> Color(0xFF2E7D32)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
}
