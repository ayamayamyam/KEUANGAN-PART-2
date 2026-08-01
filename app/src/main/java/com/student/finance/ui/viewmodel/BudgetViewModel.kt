package com.student.finance.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.student.finance.data.local.DataStoreManager
import com.student.finance.data.local.entity.BudgetEntity
import com.student.finance.data.local.entity.CategoryEntity
import com.student.finance.data.repository.BudgetRepository
import com.student.finance.data.repository.CategoryRepository
import com.student.finance.data.repository.TransactionRepository
import com.student.finance.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BudgetWithSpent(
    val budget: BudgetEntity,
    val category: CategoryEntity?,
    val spent: Double,
    val remaining: Double,
    val percentage: Float
)

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val budgetRepository: BudgetRepository,
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val activeAccountId: StateFlow<Long> = dataStoreManager.activeAccountId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1L)

    private val start = DateUtils.startOfMonth(DateUtils.currentMonth(), DateUtils.currentYear())
    private val end = DateUtils.endOfMonth(DateUtils.currentMonth(), DateUtils.currentYear())

    val budgets: StateFlow<List<BudgetEntity>> = activeAccountId
        .flatMapLatest { accountId -> budgetRepository.getForMonth(accountId, DateUtils.currentMonth(), DateUtils.currentYear()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories = activeAccountId
        .flatMapLatest { accountId -> categoryRepository.getAll(accountId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val budgetsWithSpent: StateFlow<List<BudgetWithSpent>> = activeAccountId
        .flatMapLatest { accountId ->
            budgets.map { budgetList ->
                budgetList.map { budget ->
                    val spent = try {
                        transactionRepository.getExpenseForCategoryInRange(accountId, budget.categoryId, start, end)
                    } catch (e: Exception) {
                        0.0
                    }
                    val category = categories.value.find { it.id == budget.categoryId }
                    val remaining = (budget.limitAmount - spent).coerceAtLeast(0.0)
                    val percentage = if (budget.limitAmount > 0) (spent / budget.limitAmount).toFloat().coerceIn(0f, 1f) else 0f

                    BudgetWithSpent(
                        budget = budget,
                        category = category,
                        spent = spent,
                        remaining = remaining,
                        percentage = percentage
                    )
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalBudget: StateFlow<Double> = budgets
        .map { list -> list.sumOf { it.limitAmount } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalSpent: StateFlow<Double> = budgetsWithSpent
        .map { list -> list.sumOf { it.spent } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalRemaining: StateFlow<Double> = budgetsWithSpent
        .map { list -> list.sumOf { it.remaining } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun addBudget(categoryId: Long, limitAmount: Double, alertThreshold: Double = 0.8) {
        viewModelScope.launch {
            val accountId = activeAccountId.value
            budgetRepository.insert(
                BudgetEntity(
                    categoryId = categoryId,
                    limitAmount = limitAmount,
                    month = DateUtils.currentMonth(),
                    year = DateUtils.currentYear(),
                    alertThreshold = alertThreshold,
                    accountId = accountId
                )
            )
        }
    }

    fun deleteBudget(budget: BudgetEntity) {
        viewModelScope.launch { budgetRepository.delete(budget) }
    }
}
