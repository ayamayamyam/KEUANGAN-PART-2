package com.student.finance.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.student.finance.data.local.entity.BudgetEntity
import com.student.finance.data.local.entity.CategoryEntity
import com.student.finance.data.repository.BudgetRepository
import com.student.finance.data.repository.CategoryRepository
import com.student.finance.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BudgetViewModel @Inject constructor(
    private val budgetRepository: BudgetRepository,
    categoryRepository: CategoryRepository
) : ViewModel() {

    val budgets: StateFlow<List<BudgetEntity>> =
        budgetRepository.getForMonth(DateUtils.currentMonth(), DateUtils.currentYear())
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<CategoryEntity>> = categoryRepository.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addBudget(categoryId: Long, limitAmount: Double, alertThreshold: Double = 0.8) {
        viewModelScope.launch {
            budgetRepository.insert(
                BudgetEntity(
                    categoryId = categoryId,
                    limitAmount = limitAmount,
                    month = DateUtils.currentMonth(),
                    year = DateUtils.currentYear(),
                    alertThreshold = alertThreshold
                )
            )
        }
    }

    fun deleteBudget(budget: BudgetEntity) {
        viewModelScope.launch { budgetRepository.delete(budget) }
    }
}
