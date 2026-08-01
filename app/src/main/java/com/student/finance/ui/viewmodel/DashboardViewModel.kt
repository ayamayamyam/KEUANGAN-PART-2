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
    val previousMonthBalance: Double = 0.0,
    val currency: String = "IDR"
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    transactionRepository: TransactionRepository,
    dataStoreManager: DataStoreManager
) : ViewModel() {

    private val currentMonth = DateUtils.currentMonth()
    private val currentYear = DateUtils.currentYear()
    private val start = DateUtils.startOfMonth(currentMonth, currentYear)
    private val end = DateUtils.endOfMonth(currentMonth, currentYear)

    private val prevMonth = if (currentMonth == 1) 12 else currentMonth - 1
    private val prevYear = if (currentMonth == 1) currentYear - 1 else currentYear
    private val prevStart = DateUtils.startOfMonth(prevMonth, prevYear)
    private val prevEnd = DateUtils.endOfMonth(prevMonth, prevYear)

    val uiState: StateFlow<DashboardUiState> = combine(
        transactionRepository.getTotalIncome(start, end),
        transactionRepository.getTotalExpense(start, end),
        transactionRepository.getTotalIncome(prevStart, prevEnd),
        transactionRepository.getTotalExpense(prevStart, prevEnd),
        dataStoreManager.currency
    ) { income, expense, prevIncome, prevExpense, currency ->
        val prevBalance = prevIncome - prevExpense
        DashboardUiState(
            totalIncome = income,
            totalExpense = expense,
            balance = income - expense,
            previousMonthBalance = prevBalance,
            currency = currency
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardUiState())
}
