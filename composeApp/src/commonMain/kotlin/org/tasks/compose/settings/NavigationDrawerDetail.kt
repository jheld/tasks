package org.tasks.compose.settings

import org.tasks.themes.TasksIcons
import org.tasks.compose.components.SymbolIcon
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.tasks.viewmodel.NavigationDrawerViewModel
import tasks.kmp.generated.resources.Res
import tasks.kmp.generated.resources.back
import tasks.kmp.generated.resources.customize_drawer
import tasks.kmp.generated.resources.navigation_drawer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationDrawerDetail(
    onNavigateBack: () -> Unit,
    onItemClick: (org.tasks.filters.FilterListItem) -> Unit = {},
    onCreateNew: (org.tasks.filters.NavigationDrawerSubheader) -> Unit = {},
    onEditTag: (String) -> Unit = {},
    onEditList: (Long) -> Unit = {},
) {
    val viewModel = koinViewModel<NavigationDrawerViewModel>()
    val customizationVM = koinViewModel<NavigationDrawerCustomizationViewModel>()
    var showCustomization by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (showCustomization) stringResource(Res.string.customize_drawer) else stringResource(Res.string.navigation_drawer)) },
                navigationIcon = {
                    IconButton(onClick = { if (showCustomization) { showCustomization = false } else { onNavigateBack() } }) {
                        SymbolIcon(
                            name = TasksIcons.ARROW_BACK,
                            contentDescription = stringResource(Res.string.back),
                        )
                    }
                },
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            if (showCustomization) {
                NavigationDrawerCustomization(
                    items = customizationVM.items.collectAsState(emptyList()).value,
                    collapsedSections = customizationVM.collapsedSections.collectAsState(emptySet()).value,
                    onToggleCollapse = { customizationVM.toggleSectionCollapse(it) },
                    onBack = { showCustomization = false },
                    onReorder = { from, to -> customizationVM.swapItems(from, to) },
                    onItemClick = onItemClick,
                    onCreateNew = onCreateNew,
                    onEditTag = onEditTag,
                    onEditList = onEditList,
                )
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Customize drawer button — first item before other settings
                    Column(modifier = Modifier.padding(all = 8.dp)) {
                        TextButton(
                            onClick = { showCustomization = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(
                                text = "Customize navigation drawer",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                    HorizontalDivider()
                    // Other settings items below
                    NavigationDrawerContent(
                        viewModel = viewModel,
                    )
                }
            }
        }
    }
}
