package org.tasks.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.tasks.preferences.TasksPreferences

class DateAndTimeViewModel(
    private val preferences: TasksPreferences,
) : ViewModel() {

    var fullDateEnabled by mutableStateOf(false)
        private set
    var morningSummary by mutableStateOf("09:00")
        private set
    var afternoonSummary by mutableStateOf("14:00")
        private set
    var eveningSummary by mutableStateOf("18:00")
        private set
    var nightSummary by mutableStateOf("21:00")
        private set
    var autoDismissListEnabled by mutableStateOf(false)
        private set
    var autoDismissEditEnabled by mutableStateOf(false)
        private set
    var autoDismissWidgetEnabled by mutableStateOf(false)
        private set
    var showAutoDismissInfo by mutableStateOf(false)
        private set

    init {
        refreshState()
    }

    fun refreshState() {
        viewModelScope.launch {
            fullDateEnabled = preferences.get(
                TasksPreferences.alwaysDisplayFullDate,
                false
            )
            morningSummary = formatTime(
                preferences.get(TasksPreferences.morningShortcut, 9 * 60 * 60 * 1000)
            )
            afternoonSummary = formatTime(
                preferences.get(TasksPreferences.afternoonShortcut, 14 * 60 * 60 * 1000)
            )
            eveningSummary = formatTime(
                preferences.get(TasksPreferences.eveningShortcut, 18 * 60 * 60 * 1000)
            )
            nightSummary = formatTime(
                preferences.get(TasksPreferences.nightShortcut, 21 * 60 * 60 * 1000)
            )
            autoDismissListEnabled = preferences.get(TasksPreferences.autoDismissDateTimeList, false)
            autoDismissEditEnabled = preferences.get(TasksPreferences.autoDismissDateTimeEdit, false)
            autoDismissWidgetEnabled = preferences.get(TasksPreferences.autoDismissDateTimeWidget, false)
        }
    }

    fun updateFullDate(enabled: Boolean) {
        fullDateEnabled = enabled
        viewModelScope.launch {
            preferences.set(TasksPreferences.alwaysDisplayFullDate, enabled)
        }
    }

    fun updateAutoDismissList(enabled: Boolean) {
        autoDismissListEnabled = enabled
        viewModelScope.launch {
            preferences.set(TasksPreferences.autoDismissDateTimeList, enabled)
        }
    }

    fun updateAutoDismissEdit(enabled: Boolean) {
        autoDismissEditEnabled = enabled
        viewModelScope.launch {
            preferences.set(TasksPreferences.autoDismissDateTimeEdit, enabled)
        }
    }

    fun updateAutoDismissWidget(enabled: Boolean) {
        autoDismissWidgetEnabled = enabled
        viewModelScope.launch {
            preferences.set(TasksPreferences.autoDismissDateTimeWidget, enabled)
        }
    }

    fun openAutoDismissInfo() {
        showAutoDismissInfo = true
    }

    fun dismissAutoDismissInfo() {
        showAutoDismissInfo = false
    }

    private fun formatTime(millisOfDay: Int): String {
        val totalSeconds = millisOfDay / 1000
        val hours = (totalSeconds / 3600) % 24
        val minutes = (totalSeconds % 3600) / 60
        return String.format("%02d:%02d", hours, minutes)
    }
}
