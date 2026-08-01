package com.student.finance.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.student.finance.data.local.DataStoreManager
import com.student.finance.data.local.entity.ReminderEntity
import com.student.finance.data.repository.ReminderRepository
import com.student.finance.worker.ReminderWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val repository: ReminderRepository,
    private val dataStoreManager: DataStoreManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val activeAccountId: StateFlow<Long> = dataStoreManager.activeAccountId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1L)

    val reminders: StateFlow<List<ReminderEntity>> = activeAccountId
        .flatMapLatest { accountId -> repository.getAll(accountId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addReminder(title: String, message: String?, triggerTime: Long, isRecurring: Boolean) {
        viewModelScope.launch {
            val accountId = activeAccountId.value
            val id = repository.insert(
                ReminderEntity(
                    title = title,
                    message = message,
                    triggerTime = triggerTime,
                    isRecurring = isRecurring,
                    isEnabled = true,
                    accountId = accountId
                )
            )
            scheduleReminderWork(id, title, message, triggerTime, isRecurring)
        }
    }

    fun toggleReminder(reminder: ReminderEntity) {
        viewModelScope.launch {
            val updated = reminder.copy(isEnabled = !reminder.isEnabled)
            repository.update(updated)
            if (updated.isEnabled) {
                scheduleReminderWork(updated.id, updated.title, updated.message, updated.triggerTime, updated.isRecurring)
            } else {
                cancelReminderWork(updated.id)
            }
        }
    }

    fun deleteReminder(reminder: ReminderEntity) {
        viewModelScope.launch {
            repository.delete(reminder)
            cancelReminderWork(reminder.id)
        }
    }

    private fun scheduleReminderWork(reminderId: Long, title: String, message: String?, triggerTime: Long, isRecurring: Boolean) {
        val delay = triggerTime - System.currentTimeMillis()
        if (delay <= 0) return

        val inputData = workDataOf(
            "title" to title,
            "message" to (message ?: ""),
            "reminder_id" to reminderId
        )

        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "reminder_$reminderId",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }

    private fun cancelReminderWork(reminderId: Long) {
        WorkManager.getInstance(context).cancelUniqueWork("reminder_$reminderId")
    }
}
