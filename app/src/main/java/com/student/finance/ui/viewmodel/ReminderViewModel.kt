package com.student.finance.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.student.finance.data.local.entity.ReminderEntity
import com.student.finance.data.repository.ReminderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val repository: ReminderRepository
) : ViewModel() {

    val reminders: StateFlow<List<ReminderEntity>> = repository.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addReminder(title: String, message: String?, triggerTime: Long, isRecurring: Boolean) {
        viewModelScope.launch {
            repository.insert(
                ReminderEntity(
                    title = title,
                    message = message,
                    triggerTime = triggerTime,
                    isRecurring = isRecurring
                )
            )
        }
    }

    fun toggleReminder(reminder: ReminderEntity) {
        viewModelScope.launch { repository.update(reminder.copy(isEnabled = !reminder.isEnabled)) }
    }

    fun deleteReminder(reminder: ReminderEntity) {
        viewModelScope.launch { repository.delete(reminder) }
    }
}
