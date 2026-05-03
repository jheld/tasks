package org.tasks.compose.pickers

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IconPickerDialog(
    viewModel: IconPickerViewModel,
    onDismiss: () -> Unit,
    onIconSelected: (Icon) -> Unit,
    hasPro: Boolean,
    subscribe: () -> Unit,
) {
    val viewState by viewModel.viewState.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    BasicAlertDialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(16.dp)
        ) {
            IconPicker(
                icons = viewState.icons,
                searchResults = searchResults,
                collapsed = viewState.collapsed,
                query = viewState.query,
                onQueryChange = { viewModel.onQueryChange(it) },
                onSelected = {
                    onIconSelected(it)
                    onDismiss()
                },
                toggleCollapsed = { category, collapsed ->
                    viewModel.setCollapsed(category, collapsed)
                },
                hasPro = hasPro,
                subscribe = subscribe,
            )
        }
    }
}
