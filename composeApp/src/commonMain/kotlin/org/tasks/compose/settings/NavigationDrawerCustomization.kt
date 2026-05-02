package org.tasks.compose.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import org.jetbrains.compose.resources.stringResource
import org.tasks.filters.CaldavFilter
import org.tasks.filters.CustomFilter
import org.tasks.filters.FilterListItem
import org.tasks.filters.NavigationDrawerSubheader
import org.tasks.filters.PlaceFilter
import org.tasks.filters.TagFilter
import tasks.kmp.generated.resources.Res
import tasks.kmp.generated.resources.back
import tasks.kmp.generated.resources.customize_drawer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationDrawerCustomization(
    items: List<FilterListItem>,
    collapsedSections: Set<String>,
    onToggleCollapse: (String?) -> Unit,
    onBack: () -> Unit,
    onReorder: (fromIndex: Int, toIndex: Int) -> Unit,
    onItemClick: (FilterListItem) -> Unit,
    onReset: () -> Unit,
) {
    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.back)
                        )
                    }
                },
                title = { Text(stringResource(Res.string.customize_drawer)) },
                actions = {
                    Text(
                        text = "Reset",
                        modifier = Modifier
                            .clickable { onReset() }
                            .padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
            ) {
                itemsIndexed(
                    items = items,
                    key = { _, item ->
                        when (item) {
                            is CaldavFilter -> "caldav_${item.calendar.id}"
                            is TagFilter -> "tag_${item.tagData.id}"
                            is CustomFilter -> "filter_${item.id}"
                            is PlaceFilter -> "place_${item.place.id}"
                            is NavigationDrawerSubheader -> "subheader_${item.title}"
                            else -> "item_$item"
                        }
                    }
                ) { index, item ->
                    when (item) {
                        is NavigationDrawerSubheader -> {
                            SectionHeader(
                                title = item.title ?: "",
                                isCollapsed = collapsedSections.contains(item.title ?: ""),
                                onToggleCollapse = { onToggleCollapse(item.title) },
                            )
                        }
                        else -> {
                            val sectionTitle = (items.getOrNull(index - 1) as? NavigationDrawerSubheader)?.title
                            if (sectionTitle == null || !collapsedSections.contains(sectionTitle)) {
                                NavigationDrawerCustomizationRow(
                                    item = item,
                                    onClick = { onItemClick(item) },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    isCollapsed: Boolean,
    onToggleCollapse: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleCollapse() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = if (isCollapsed) Icons.Default.ExpandMore else Icons.Default.ExpandLess,
            contentDescription = if (isCollapsed) "Expand" else "Collapse",
            tint = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
fun NavigationDrawerCustomizationRow(
    item: FilterListItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (item.itemType == FilterListItem.Type.ITEM) {
            Icon(
                imageVector = Icons.Default.DragHandle,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.padding(end = 16.dp)
            )
        } else {
            Spacer(modifier = Modifier.width(40.dp))
        }
        Text(
            text = getItemTitle(item),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
    }
}

private fun getItemTitle(item: FilterListItem): String {
    return when (item) {
        is CaldavFilter -> item.title
        is TagFilter -> item.title
        is CustomFilter -> item.title
        is PlaceFilter -> item.title
        is NavigationDrawerSubheader -> item.title ?: ""
        else -> ""
    }
}
