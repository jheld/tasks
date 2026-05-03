package org.tasks.compose.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
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
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.tasks.compose.pickers.Icon
import org.tasks.compose.pickers.IconPickerDialog
import org.tasks.compose.pickers.IconPickerViewModel
import org.tasks.compose.settings.ColorPickerDialog
import org.tasks.compose.settings.PickerColor
import org.tasks.data.dao.TagDataDao
import org.tasks.data.entity.TagData
import org.tasks.kmp.org.tasks.themes.ColorProvider
import org.tasks.kmp.org.tasks.themes.ThemeColor
import org.tasks.themes.TasksIcons
import org.tasks.compose.components.TasksIcon
import tasks.kmp.generated.resources.Res
import tasks.kmp.generated.resources.back
import tasks.kmp.generated.resources.settings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagSettingsScreen(
    tagId: String?,
    onBack: () -> Unit,
    onSave: (String) -> Unit,
) {
    val tagDataDao = koinInject<TagDataDao>()
    val scope = rememberCoroutineScope()
    val iconPickerViewModel = remember { IconPickerViewModel() }

    var name by remember { mutableStateOf("") }
    var color by remember { mutableStateOf(0) }
    var icon by remember { mutableStateOf(TasksIcons.LABEL) }
    var showDiscardDialog by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }
    var showIconPicker by remember { mutableStateOf(false) }
    var saveCompleted by remember { mutableStateOf(false) }
    var originalName by remember { mutableStateOf("") }
    var originalColor by remember { mutableStateOf(0) }
    var originalIcon by remember { mutableStateOf(TasksIcons.LABEL) }

    // Convert ThemeColor to PickerColor
    val pickerColors = remember {
        ColorProvider.PRESET_COLORS.map { colorValue ->
            PickerColor(
                originalColor = colorValue,
                primaryColor = colorValue,
                colorOnPrimary = if (colorValue == 0) 0xFF000000.toInt() else 0xFFFFFFFF.toInt(),
                isFree = true, // For now, make all colors free
            )
        }
    }

    // Load existing tag
    LaunchedEffect(tagId) {
        if (tagId != null) {
            val id = tagId.toLongOrNull() ?: return@LaunchedEffect
            val tags = tagDataDao.getAll()
            val tag = tags.find { it.id == id }
            name = tag?.name ?: ""
            color = tag?.color ?: 0
            icon = tag?.icon ?: TasksIcons.LABEL
            originalName = name
            originalColor = color
            originalIcon = icon
        }
    }

    val hasChanges = name != originalName || color != originalColor || icon != originalIcon

    LaunchedEffect(saveCompleted) {
        if (saveCompleted) {
            saveCompleted = false
            onBack()
        }
    }

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

    if (showColorPicker) {
        ColorPickerDialog(
            hasPro = true, // For now, assume pro
            colors = pickerColors,
            onDismiss = { showColorPicker = false },
            onColorSelected = { pickerColor ->
                color = pickerColor.originalColor
                showColorPicker = false
            },
        )
    }

    if (showIconPicker) {
        IconPickerDialog(
            viewModel = iconPickerViewModel,
            onDismiss = { showIconPicker = false },
            onIconSelected = { selectedIcon ->
                icon = selectedIcon.name
                showIconPicker = false
            },
            hasPro = true, // For now, assume pro
            subscribe = { /* TODO: Handle subscription */ },
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = {
                        if (tagId != null && hasChanges) {
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
                title = { Text(if (tagId == null) "New Tag" else stringResource(Res.string.settings)) },
                actions = {
                    TextButton(onClick = {
                        scope.launch {
                            try {
                                if (tagId == null) {
                                    val newTag = TagData(
                                        name = name,
                                        color = color,
                                        icon = icon,
                                    )
                                    tagDataDao.insert(newTag)
                                } else {
                                    val id = tagId.toLongOrNull() ?: return@launch
                                    val tags = tagDataDao.getAll()
                                    val tag = tags.find { it.id == id } ?: return@launch
                                    val updated = tag.copy(
                                        name = name,
                                        color = color,
                                        icon = icon,
                                    )
                                    tagDataDao.update(updated)
                                }
                                onSave(name)
                                saveCompleted = true
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }) {
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
                text = "Tag Name",
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

            HorizontalDivider(modifier = Modifier.padding(top = 4.dp, bottom = 16.dp))

            // Color Picker Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showColorPicker = true }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    modifier = Modifier.size(32.dp),
                    color = if (color == 0) MaterialTheme.colorScheme.primary else Color(color),
                    shape = CircleShape,
                ) {}
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Color",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Icon Picker Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showIconPicker = true }
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TasksIcon(
                    label = icon,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Icon",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }

            if (tagId != null) {
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = { /* TODO: Delete tag */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Delete Tag")
                }
            }
        }
    }
}
