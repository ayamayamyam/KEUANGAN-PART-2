package com.student.finance.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.student.finance.data.local.entity.DebtEntity
import com.student.finance.data.local.entity.DebtStatus
import com.student.finance.data.local.entity.DebtType
import com.student.finance.data.repository.DebtRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
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
    private val debtRepository: DebtRepository
) : ViewModel() {

    val allDebts: StateFlow<List<DebtEntity>> = debtRepository.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lentDebts: StateFlow<List<DebtEntity>> = debtRepository.getByType(DebtType.LENT)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val borrowedDebts: StateFlow<List<DebtEntity>> = debtRepository.getByType(DebtType.BORROWED)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val uiState: StateFlow<DebtUiState> = combine(
        debtRepository.getTotalUnpaidByType(DebtType.LENT),
        debtRepository.getTotalUnpaidByType(DebtType.BORROWED)
    ) { lent, borrowed ->
        DebtUiState(
            totalLent = lent,
            totalBorrowed = borrowed,
            netDebt = lent - borrowed
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DebtUiState())

    fun addDebt(
        personName: String,
        amount: Double,
        type: DebtType,
        description: String?,
        date: Long,
        dueDate: Long?
    ) {
        viewModelScope.launch {
            debtRepository.insert(
                DebtEntity(
                    personName = personName,
                    amount = amount,
                    type = type,
                    description = description,
                    date = date,
                    dueDate = dueDate
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
