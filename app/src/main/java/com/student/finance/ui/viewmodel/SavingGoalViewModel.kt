package com.student.finance.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.student.finance.data.local.entity.SavingGoalEntity
import com.student.finance.data.local.entity.TransactionEntity
import com.student.finance.data.local.entity.TransactionType
import com.student.finance.data.repository.SavingGoalRepository
import com.student.finance.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavingGoalViewModel @Inject constructor(
    private val repository: SavingGoalRepository,
    private val transactionRepository: TransactionRepository
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
            // Catat sebagai pengeluaran
            transactionRepository.insert(
                TransactionEntity(
                    amount = amount,
                    type = TransactionType.EXPENSE,
                    categoryId = null,
                    date = System.currentTimeMillis(),
                    description = "Tabungan: ${goal.name}"
                )
            )
        }
    }

    fun deleteGoal(goal: SavingGoalEntity) {
        viewModelScope.launch { repository.delete(goal) }
    }
}
