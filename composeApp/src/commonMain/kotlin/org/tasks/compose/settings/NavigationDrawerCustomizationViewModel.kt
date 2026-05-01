package org.tasks.compose.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.tasks.data.dao.CaldavDao
import org.tasks.data.dao.FilterDao
import org.tasks.data.dao.LocationDao
import org.tasks.data.dao.TagDataDao
import org.tasks.filters.FilterListItem
import org.tasks.filters.FilterProvider

class NavigationDrawerCustomizationViewModel(
    private val filterProvider: FilterProvider,
    private val filterDao: FilterDao,
    private val caldavDao: CaldavDao,
    private val tagDataDao: TagDataDao,
    private val locationDao: LocationDao,
) : ViewModel() {

    private val _items = MutableStateFlow<List<FilterListItem>>(emptyList())
    val items: StateFlow<List<FilterListItem>> = _items.asStateFlow()

    init {
        loadItems()
    }

    fun loadItems() {
        viewModelScope.launch {
            _items.value = filterProvider.drawerCustomizationItems()
        }
    }

    fun swapItems(fromIndex: Int, toIndex: Int) {
        viewModelScope.launch {
            val currentList = _items.value.toMutableList()
            if (fromIndex < 0 || fromIndex >= currentList.size ||
                toIndex < 0 || toIndex >= currentList.size) {
                return@launch
            }

            val fromItem = currentList[fromIndex]
            val toItem = currentList[toIndex]

            // Only allow swapping items of the same type
            if (fromItem::class.java != toItem::class.java) {
                return@launch
            }

            // For CaldavFilter, only allow swapping within same account
            if (fromItem is org.tasks.filters.CaldavFilter &&
                toItem is org.tasks.filters.CaldavFilter) {
                if (fromItem.account != toItem.account) {
                    return@launch
                }
            }

            // Perform swap in the list
            currentList.removeAt(fromIndex)
            currentList.add(toIndex, fromItem)

            _items.value = currentList

            // Update orders in database for items of the same type
            updateOrders(currentList, fromItem)
        }
    }

    fun resetOrders() {
        viewModelScope.launch {
            filterDao.resetOrders()
            caldavDao.resetOrders()
            tagDataDao.resetOrders()
            locationDao.resetOrders()
            loadItems()
        }
    }

    private suspend fun updateOrders(
        items: List<FilterListItem>,
        referenceItem: FilterListItem,
    ) {
        val itemsToUpdate = items.filter { it::class.java == referenceItem::class.java }

        // For CaldavFilter, filter by account
        val filteredItems = when (referenceItem) {
            is org.tasks.filters.CaldavFilter -> {
                itemsToUpdate.filterIsInstance<org.tasks.filters.CaldavFilter>()
                    .filter { it.account == referenceItem.account }
            }
            else -> itemsToUpdate
        }

        filteredItems.forEachIndexed { index, item ->
            when (item) {
                is org.tasks.filters.CaldavFilter -> {
                    item.calendar.id?.let { caldavDao.setOrder(it, index) }
                }
                is org.tasks.filters.TagFilter -> {
                    tagDataDao.setOrder(item.tagData.id!!, index)
                }
                is org.tasks.filters.CustomFilter -> {
                    filterDao.setOrder(item.id, index)
                }
                is org.tasks.filters.PlaceFilter -> {
                    locationDao.setOrder(item.place.id, index)
                }
            }
        }
    }
}
