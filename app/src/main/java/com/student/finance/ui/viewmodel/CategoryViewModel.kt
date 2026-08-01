package com.student.finance.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.student.finance.data.local.DataStoreManager
import com.student.finance.data.local.entity.CategoryEntity
import com.student.finance.data.local.entity.TransactionType
import com.student.finance.data.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val activeAccountId: StateFlow<Long> = dataStoreManager.activeAccountId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1L)

    val incomeCategories = activeAccountId
        .flatMapLatest { accountId -> categoryRepository.getByType(accountId, TransactionType.INCOME) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expenseCategories = activeAccountId
        .flatMapLatest { accountId -> categoryRepository.getByType(accountId, TransactionType.EXPENSE) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addCategory(name: String, type: TransactionType) {
        viewModelScope.launch {
            val accountId = activeAccountId.value
            categoryRepository.insert(
                CategoryEntity(name = name, type = type, isCustom = true, accountId = accountId)
            )
        }
    }

    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch { categoryRepository.delete(category) }
    }
}
