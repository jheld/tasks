package org.tasks.compose.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.tasks.compose.edit.TaskEditRow
import org.tasks.kmp.formatDate
import org.tasks.kmp.org.tasks.time.DateStyle

@Composable
fun InfoRow(
    creationDate: Long,
    modificationDate: Long,
    completionDate: Long,
) {
    TaskEditRow(
        icon = {
            Text(
                text = "ℹ",
                style = MaterialTheme.typography.titleMedium,
            )
        },
        content = {
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                if (creationDate > 0) {
                    Text(
                        text = "Created: ${formatDate(creationDate, DateStyle.MEDIUM)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                if (modificationDate > 0) {
                    Text(
                        text = "Modified: ${formatDate(modificationDate, DateStyle.MEDIUM)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                if (completionDate > 0) {
                    Text(
                        text = "Completed: ${formatDate(completionDate, DateStyle.MEDIUM)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        },
    )
}
