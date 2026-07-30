package com.student.finance.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.student.finance.data.local.entity.SavingGoalEntity
import com.student.finance.data.repository.SavingGoalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavingGoalViewModel @Inject constructor(
    private val repository: SavingGoalRepository
) : ViewModel() {

    val goals: StateFlow<List<SavingGoalEntity>> = repository.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addGoal(name: String, targetAmount: Double, deadline: Long?) {
        viewModelScope.launch {
            repository.insert(SavingGoalEntity(name = name, targetAmount = targetAmount, deadline = deadline))
        }
    }

    fun addToSavings(goal: SavingGoalEntity, amount: Double) {
        viewModelScope.launch {
            repository.update(goal.copy(savedAmount = goal.savedAmount + amount))
        }
    }

    fun deleteGoal(goal: SavingGoalEntity) {
        viewModelScope.launch { repository.delete(goal) }
    }
}
