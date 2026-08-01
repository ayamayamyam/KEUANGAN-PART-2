package com.student.finance.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.student.finance.data.local.DataStoreManager
import com.student.finance.data.local.entity.DebtEntity
import com.student.finance.data.local.entity.DebtStatus
import com.student.finance.data.local.entity.DebtType
import com.student.finance.data.repository.DebtRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DebtUiState(
    val totalLent: Double = 0.0,
    val totalBorrowed: Double = 0.0,
    val netDebt: Double = 0.0
)

@HiltViewModel
class DebtViewModel @Inject constructor(
    private val debtRepository: DebtRepository,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val activeAccountId: StateFlow<Long> = dataStoreManager.activeAccountId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1L)

    val allDebts: StateFlow<List<DebtEntity>> = activeAccountId
        .flatMapLatest { accountId -> debtRepository.getAll(accountId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val uiState: StateFlow<DebtUiState> = activeAccountId
        .flatMapLatest { accountId ->
            combine(
                debtRepository.getTotalUnpaidByType(accountId, DebtType.LENT),
                debtRepository.getTotalUnpaidByType(accountId, DebtType.BORROWED)
            ) { lent, borrowed ->
                DebtUiState(
                    totalLent = lent,
                    totalBorrowed = borrowed,
                    netDebt = lent - borrowed
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DebtUiState())

    fun addDebt(
        personName: String,
        amount: Double,
        type: DebtType,
        description: String?,
        date: Long,
        dueDate: Long?
    ) {
        viewModelScope.launch {
            val accountId = activeAccountId.value
            debtRepository.insert(
                DebtEntity(
                    personName = personName,
                    amount = amount,
                    type = type,
                    description = description,
                    date = date,
                    dueDate = dueDate,
                    accountId = accountId
                )
            )
        }
    }

    fun markAsPaid(debt: DebtEntity) {
        viewModelScope.launch {
            debtRepository.update(
                debt.copy(
                    isPaid = true,
                    status = DebtStatus.PAID,
                    paidDate = System.currentTimeMillis()
                )
            )
        }
    }

    fun deleteDebt(debt: DebtEntity) {
        viewModelScope.launch { debtRepository.delete(debt) }
    }
}
