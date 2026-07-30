package com.student.finance.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.student.finance.data.local.DataStoreManager
import com.student.finance.data.repository.TransactionRepository
import com.student.finance.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class DashboardUiState(
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val balance: Double = 0.0,
    val currency: String = "IDR"
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    transactionRepository: TransactionRepository,
    dataStoreManager: DataStoreManager
) : ViewModel() {

    private val start = DateUtils.startOfMonth(DateUtils.currentMonth(), DateUtils.currentYear())
    private val end = DateUtils.endOfMonth(DateUtils.currentMonth(), DateUtils.currentYear())

    val uiState: StateFlow<DashboardUiState> = combine(
        transactionRepository.getTotalIncome(start, end),
        transactionRepository.getTotalExpense(start, end),
        dataStoreManager.currency
    ) { income, expense, currency ->
        DashboardUiState(
            totalIncome = income,
            totalExpense = expense,
            balance = income - expense,
            currency = currency
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardUiState())
}
