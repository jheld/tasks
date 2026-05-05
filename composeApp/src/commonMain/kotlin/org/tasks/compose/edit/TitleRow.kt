package org.tasks.compose.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.tasks.compose.edit.MarkdownEditField

@Composable
fun TitleRow(
    text: String?,
    onChanged: (String?) -> Unit,
    isCompleted: Boolean,
    isRecurring: Boolean,
    priority: Int,
    onComplete: () -> Unit,
    requestFocus: Boolean = false,
    multiline: Boolean = false,
    save: () -> Unit = {},
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Spacer(modifier = Modifier.height(3.dp))
        MarkdownEditField(
            value = text.orEmpty(),
            onValueChange = { onChanged(it) },
            placeholder = "Task title",
            textStyle = MaterialTheme.typography.titleLarge.copy(
                color = if (isCompleted) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface,
            ),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(11.dp))
    }
}
