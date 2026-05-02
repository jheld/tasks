package org.tasks.compose.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.stringResource
import tasks.kmp.generated.resources.Res
import tasks.kmp.generated.resources.back
import tasks.kmp.generated.resources.settings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaldavCalendarSettingsScreen(
    accountName: String,
    accountId: String? = null,
    calendarName: String?,
    isNew: Boolean = calendarName == null,
    onBack: () -> Unit,
    onSave: (String) -> Unit,
) {
    var name by remember { mutableStateOf(calendarName ?: "") }
    var showDiscardDialog by remember { mutableStateOf(false) }

    if (showDiscardDialog) {
        BasicAlertDialog(
            onDismissRequest = { showDiscardDialog = false },
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "Discard changes?",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Text(
                        text = "You have unsaved changes. Are you sure you want to discard them?",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        TextButton(onClick = { showDiscardDialog = false }) {
                            Text("Cancel")
                        }
                        TextButton(onClick = {
                            showDiscardDialog = false
                            onBack()
                        }) {
                            Text("Discard")
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = {
                        if (!isNew && name != calendarName) {
                            showDiscardDialog = true
                        } else {
                            onBack()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.back)
                        )
                    }
                },
                title = { Text(if (isNew) "New List" else stringResource(Res.string.settings)) },
                actions = {
                    TextButton(onClick = { onSave(name) }) {
                        Text("Save")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Account: $accountName",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "Calendar Name",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            BasicTextField(
                value = name,
                onValueChange = { name = it },
                textStyle = TextStyle(
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )

            HorizontalDivider(modifier = Modifier.padding(top = 4.dp))
        }
    }
}
