package org.tasks.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.updateAndGet
import org.tasks.data.TaskCreator
import org.tasks.data.TaskMover
import org.tasks.data.TaskSaver
import org.tasks.data.dao.AlarmDao
import org.tasks.data.dao.CaldavDao
import org.tasks.data.dao.TaskDao
import org.tasks.data.entity.Alarm
import org.tasks.data.entity.CaldavTask
import org.tasks.data.entity.Task
import org.tasks.filters.CaldavFilter
import org.tasks.preferences.TasksPreferences
import org.tasks.time.DateTimeUtils2.currentTimeMillis

class TaskEditViewModel(
    private val taskDao: TaskDao,
    private val taskSaver: TaskSaver,
    private val caldavDao: CaldavDao,
    private val taskMover: TaskMover,
    private val taskCreator: TaskCreator = TaskCreator(),
    private val alarmDao: AlarmDao,
    private val tasksPreferences: TasksPreferences,
) : ViewModel() {

    private val log = Logger.withTag("TaskEditViewModel")

    data class State(
        val isLoading: Boolean = true,
        val task: Task = Task(),
        val originalTask: Task = Task(),
        val list: CaldavFilter? = null,
        val originalList: CaldavFilter? = null,
        val deleted: Boolean = false,
        val showKeyboard: Boolean = false,
        val backButtonSavesTask: Boolean = false,
        val isReadOnly: Boolean = false,
        val alwaysDisplayFullDate: Boolean = false,
        val alarms: List<Alarm> = emptyList(),
    ) {
        val hasDueDateAlarm: Boolean get() = alarms.any { it.type == Alarm.TYPE_REL_END }
        val isNew: Boolean get() = originalTask.isNew
        val hasChanges: Boolean get() = task != originalTask || list != originalList
        val isCompleted: Boolean get() = task.isCompleted
        val isRecurring: Boolean get() = task.isRecurring
    }

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    private val _closeEvents = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val closeEvents: SharedFlow<Unit> = _closeEvents.asSharedFlow()

    private val _saveError = MutableStateFlow(false)
    val saveError: StateFlow<Boolean> = _saveError.asStateFlow()

    private val _discardEvents = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val discardEvents: SharedFlow<Unit> = _discardEvents.asSharedFlow()

    private val saveMutex = Mutex()
    private var initializeJob: Job? = null
    private var watchJob: Job? = null

    fun initialize(taskId: Long?, currentFilter: CaldavFilter? = null) {
        watchJob?.cancel()
        val normalized = taskId?.takeIf { it != Task.NO_ID }
        initializeJob?.cancel()
        initializeJob = viewModelScope.launch {
            _state.value = State(isLoading = true)
            val loaded: Task
            val list: CaldavFilter?
            if (normalized == null) {
                loaded = taskCreator.createBlankTask()
                list = currentFilter ?: firstCaldavList()
            } else {
                loaded = taskDao.fetch(normalized) ?: taskCreator.createBlankTask()
                val caldavTask = caldavDao.getTask(normalized)
                val calendar = caldavTask?.calendar?.let { caldavDao.getCalendarByUuid(it) }
                val account = calendar?.account?.let { caldavDao.getAccountByUuid(it) }
                list = if (calendar != null && account != null) {
                    CaldavFilter(calendar = calendar, account = account)
                } else {
                    firstCaldavList()
                }
            }
            // Load alarms for the task
            val alarms = if (normalized != null) {
                alarmDao.getAlarms(normalized)
            } else {
                emptyList()
            }
            _state.value = State(
                isLoading = false,
                task = loaded,
                originalTask = loaded.copy(),
                list = list,
                originalList = list,
                alwaysDisplayFullDate = tasksPreferences.get(TasksPreferences.alwaysDisplayFullDate, false),
                alarms = alarms,
            )
            if (normalized != null) {
                watchJob = viewModelScope.launch {
                    taskDao.watch(normalized)
                        .filterNotNull()
                        .distinctUntilChanged()
                        .collect { dbTask -> mergeDbUpdate(dbTask) }
                }
            }
        }
    }

    suspend fun saveCurrentTask() {
        withContext(NonCancellable) {
            saveMutex.withLock {
                val snapshot = _state.value
                if (!snapshot.isLoading) {
                    try {
                        saveIfNeeded(snapshot)
                        _state.update {
                            it.copy(
                                originalTask = snapshot.task.copy(),
                                originalList = snapshot.list,
                            )
                        }
                    } catch (e: Exception) {
                        log.e(e) { "Failed to save task" }
                        _saveError.value = true
                    }
                }
            }
        }
    }

    /**
     * Merge a database update into the current state. For each field:
     * - If the user hasn't modified it (current == original), adopt the DB value.
     * - If the user HAS modified it, keep the user's value.
     * The original is always updated to the latest DB state so hasChanges stays accurate.
     */
    private fun mergeDbUpdate(dbTask: Task) {
        val shouldClose = _state.updateAndGet { state ->
            if (state.isLoading) return@updateAndGet state
            val current = state.task
            val original = state.originalTask
            // Skip if nothing changed in the DB.
            if (dbTask == original) return@updateAndGet state
            if (dbTask.isDeleted && !original.isDeleted) {
                return@updateAndGet state.copy(deleted = true)
            }
            val merged = current.copy(
                title = merge(current.title, original.title, dbTask.title),
                priority = merge(current.priority, original.priority, dbTask.priority),
                dueDate = merge(current.dueDate, original.dueDate, dbTask.dueDate),
                hideUntil = merge(current.hideUntil, original.hideUntil, dbTask.hideUntil),
                completionDate = merge(current.completionDate, original.completionDate, dbTask.completionDate),
                deletionDate = merge(current.deletionDate, original.deletionDate, dbTask.deletionDate),
                notes = merge(current.notes, original.notes, dbTask.notes),
                estimatedSeconds = merge(current.estimatedSeconds, original.estimatedSeconds, dbTask.estimatedSeconds),
                elapsedSeconds = merge(current.elapsedSeconds, original.elapsedSeconds, dbTask.elapsedSeconds),
                timerStart = merge(current.timerStart, original.timerStart, dbTask.timerStart),
                ringFlags = merge(current.ringFlags, original.ringFlags, dbTask.ringFlags),
                recurrence = merge(current.recurrence, original.recurrence, dbTask.recurrence),
                repeatFrom = merge(current.repeatFrom, original.repeatFrom, dbTask.repeatFrom),
                calendarURI = merge(current.calendarURI, original.calendarURI, dbTask.calendarURI),
                isCollapsed = merge(current.isCollapsed, original.isCollapsed, dbTask.isCollapsed),
                parent = merge(current.parent, original.parent, dbTask.parent),
                order = merge(current.order, original.order, dbTask.order),
                readOnly = merge(current.readOnly, original.readOnly, dbTask.readOnly),
                modificationDate = dbTask.modificationDate,
                reminderLast = dbTask.reminderLast,
            )
            state.copy(task = merged, originalTask = dbTask)
        }.deleted
        if (shouldClose) {
            _closeEvents.tryEmit(Unit)
        }
    }

    private fun <T> merge(current: T, original: T, db: T): T =
        if (current == original) db else current

    private suspend fun firstCaldavList(): CaldavFilter? {
        val calendar = caldavDao.getCalendars()
            .firstOrNull { !it.readOnly() } ?: return null
        val account = calendar.account?.let { caldavDao.getAccountByUuid(it) } ?: return null
        return CaldavFilter(calendar = calendar, account = account)
    }

    fun clearSaveError() {
        _saveError.value = false
    }

    fun setTitle(title: String) {
        _state.update { it.copy(task = it.task.copy(title = title)) }
    }

    fun setDescription(description: String) {
        _state.update { it.copy(task = it.task.copy(notes = description)) }
    }

    fun setList(list: CaldavFilter) {
        _state.update { it.copy(list = list) }
    }
    fun setPriority(priority: Int) {
        _state.update { it.copy(task = it.task.copy(priority = priority)) }
    }

    fun setDueDate(dueDate: Long) {
        _state.update { it.copy(task = it.task.copy(dueDate = dueDate)) }
    }

    fun setRepeatFrom(@Task.RepeatFrom repeatFrom: Int) {
        _state.update { it.copy(task = it.task.copy(repeatFrom = repeatFrom)) }
    }

    fun setRecurrence(recurrence: String?) {
        _state.update { it.copy(task = it.task.copy(recurrence = recurrence)) }
    }

    fun setComplete(completed: Boolean) {
        _state.update {
            val task = if (completed) {
                it.task.copy(completionDate = currentTimeMillis())
            } else {
                it.task.copy(completionDate = 0L)
            }
            it.copy(task = task)
        }
    }

    fun save() {
        viewModelScope.launch {
            var success = true
            withContext(NonCancellable) {
                saveMutex.withLock {
                    val snapshot = _state.value
                    if (!snapshot.isLoading) {
                        try {
                            saveIfNeeded(snapshot)
                            _state.update {
                                it.copy(
                                    originalTask = snapshot.task.copy(),
                                    originalList = snapshot.list,
                                )
                            }
                        } catch (e: Exception) {
                            log.e(e) { "Failed to save task" }
                            _saveError.value = true
                            success = false
                        }
                    }
                }
            }
            if (success) {
                _closeEvents.emit(Unit)
            }
        }
    }

    fun discard() {
        viewModelScope.launch {
            withContext(NonCancellable) {
                saveMutex.withLock {
                    val snapshot = _state.value
                    if (snapshot.isNew) {
                        // New task - close without saving
                        _discardEvents.emit(Unit)
                    }
                    // For existing tasks, just close without saving changes
                    // (changes were already reverted in the UI state)
                }
            }
            // Always close the screen
            _closeEvents.emit(Unit)
        }
    }

    fun delete() {
        viewModelScope.launch {
            val snapshot = _state.value
            if (!snapshot.isNew) {
                // Mark task as deleted
                val task = snapshot.task.copy(deletionDate = currentTimeMillis())
                taskSaver.save(task, snapshot.originalTask)
            }
            _closeEvents.emit(Unit)
        }
    }

    private suspend fun saveIfNeeded(snapshot: State) {
        val list = snapshot.list ?: return
        if (!snapshot.hasChanges) return
        val task = snapshot.task
        if (snapshot.isNew) {
            taskDao.createNew(task)
            caldavDao.insert(
                task = task,
                caldavTask = CaldavTask(task = task.id, calendar = list.uuid),
                addToTop = false,
            )
            taskSaver.save(task, null)
        } else {
            taskSaver.save(task, snapshot.originalTask)
            if (snapshot.list != snapshot.originalList) {
                taskMover.move(listOf(task.id), list)
            }
        }
    }
}
