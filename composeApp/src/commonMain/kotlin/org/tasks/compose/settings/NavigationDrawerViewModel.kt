package org.tasks.compose.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.tasks.preferences.TasksPreferences

class NavigationDrawerViewModel(
    private val preferences: TasksPreferences,
) : ViewModel() {

    var filtersEnabled by mutableStateOf(true)
        private set
    var showToday by mutableStateOf(true)
        private set
    var showRecentlyModified by mutableStateOf(true)
        private set
    var tagsEnabled by mutableStateOf(true)
        private set
    var hideUnusedTags by mutableStateOf(false)
        private set
    var placesEnabled by mutableStateOf(true)
        private set
    var hideUnusedPlaces by mutableStateOf(false)
        private set

    init {
        refreshState()
    }

    fun refreshState() {
        viewModelScope.launch {
            filtersEnabled = preferences.get(
                TasksPreferences.filtersEnabled,
                true
            )
            showToday = preferences.get(
                TasksPreferences.showTodayFilter,
                true
            )
            showRecentlyModified = preferences.get(
                TasksPreferences.showRecentlyModifiedFilter,
                true
            )
            tagsEnabled = preferences.get(
                TasksPreferences.tagsEnabled,
                true
            )
            hideUnusedTags = preferences.get(
                TasksPreferences.tagsHideUnused,
                false
            )
            placesEnabled = preferences.get(
                TasksPreferences.placesEnabled,
                true
            )
            hideUnusedPlaces = preferences.get(
                TasksPreferences.placesHideUnused,
                false
            )
        }
    }

    fun updateFiltersEnabled(enabled: Boolean) {
        filtersEnabled = enabled
        viewModelScope.launch {
            preferences.set(TasksPreferences.filtersEnabled, enabled)
        }
    }

    fun updateShowToday(enabled: Boolean) {
        showToday = enabled
        viewModelScope.launch {
            preferences.set(TasksPreferences.showTodayFilter, enabled)
        }
    }

    fun updateShowRecentlyModified(enabled: Boolean) {
        showRecentlyModified = enabled
        viewModelScope.launch {
            preferences.set(TasksPreferences.showRecentlyModifiedFilter, enabled)
        }
    }

    fun updateTagsEnabled(enabled: Boolean) {
        tagsEnabled = enabled
        viewModelScope.launch {
            preferences.set(TasksPreferences.tagsEnabled, enabled)
        }
    }

    fun updateHideUnusedTags(enabled: Boolean) {
        hideUnusedTags = enabled
        viewModelScope.launch {
            preferences.set(TasksPreferences.tagsHideUnused, enabled)
        }
    }

    fun updatePlacesEnabled(enabled: Boolean) {
        placesEnabled = enabled
        viewModelScope.launch {
            preferences.set(TasksPreferences.placesEnabled, enabled)
        }
    }

    fun updateHideUnusedPlaces(enabled: Boolean) {
        hideUnusedPlaces = enabled
        viewModelScope.launch {
            preferences.set(TasksPreferences.placesHideUnused, enabled)
        }
    }
}
