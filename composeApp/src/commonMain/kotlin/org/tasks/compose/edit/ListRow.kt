package org.tasks.compose.edit

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.tasks.compose.edit.TaskEditRow
import org.tasks.filters.CaldavFilter

@Composable
fun ListRow(
    list: CaldavFilter?,
    onClick: () -> Unit,
) {
    TaskEditRow(
        icon = {
            Text(
                text = if (list != null) "●" else "○",
                style = MaterialTheme.typography.titleMedium,
                color = if (list != null) {
                    Color(list.tint)
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
            )
        },
        content = {
            Text(
                text = list?.title ?: "No list selected",
                style = MaterialTheme.typography.bodyLarge,
                color = if (list != null) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                },
            )
        },
        onClick = onClick,
    )
}
