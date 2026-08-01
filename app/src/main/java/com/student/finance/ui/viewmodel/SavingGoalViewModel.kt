package com.student.finance.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.student.finance.data.local.DataStoreManager
import com.student.finance.data.local.entity.SavingGoalEntity
import com.student.finance.data.local.entity.TransactionEntity
import com.student.finance.data.local.entity.TransactionType
import com.student.finance.data.repository.SavingGoalRepository
import com.student.finance.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavingGoalViewModel @Inject constructor(
    private val repository: SavingGoalRepository,
    private val transactionRepository: TransactionRepository,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val activeAccountId: StateFlow<Long> = dataStoreManager.activeAccountId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1L)

    val goals: StateFlow<List<SavingGoalEntity>> = activeAccountId
        .flatMapLatest { accountId -> repository.getAll(accountId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addGoal(name: String, targetAmount: Double, deadline: Long?) {
        viewModelScope.launch {
            val accountId = activeAccountId.value
            repository.insert(SavingGoalEntity(name = name, targetAmount = targetAmount, deadline = deadline, accountId = accountId))
        }
    }

    fun addToSavings(goal: SavingGoalEntity, amount: Double) {
        viewModelScope.launch {
            repository.update(goal.copy(savedAmount = goal.savedAmount + amount))
            transactionRepository.insert(
                TransactionEntity(
                    amount = amount,
                    type = TransactionType.EXPENSE,
                    categoryId = null,
                    date = System.currentTimeMillis(),
                    description = "Tabungan: ${goal.name}",
                    accountId = goal.accountId
                )
            )
        }
    }

    fun deleteGoal(goal: SavingGoalEntity) {
        viewModelScope.launch { repository.delete(goal) }
    }
}
