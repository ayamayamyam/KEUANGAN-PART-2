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
    dataStoreManager: DataStoreManager
) : ViewModel() {

    val currentAccountId: StateFlow<Long> = dataStoreManager.currentAccountId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val allDebts: StateFlow<List<DebtEntity>> = currentAccountId
        .flatMapLatest { accountId -> debtRepository.getAll(accountId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lentDebts: StateFlow<List<DebtEntity>> = currentAccountId
        .flatMapLatest { accountId -> debtRepository.getByType(accountId, DebtType.LENT) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val borrowedDebts: StateFlow<List<DebtEntity>> = currentAccountId
        .flatMapLatest { accountId -> debtRepository.getByType(accountId, DebtType.BORROWED) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val uiState: StateFlow<DebtUiState> = currentAccountId
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
        .stateIn(view
