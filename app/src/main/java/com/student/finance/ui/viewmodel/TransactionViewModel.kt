package com.student.finance.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.student.finance.data.local.DataStoreManager
import com.student.finance.data.local.entity.CategoryEntity
import com.student.finance.data.local.entity.TransactionEntity
import com.student.finance.data.local.entity.TransactionType
import com.student.finance.data.repository.CategoryRepository
import com.student.finance.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    dataStoreManager: DataStoreManager
) : ViewModel() {

    val currentAccountId: StateFlow<Long> = dataStoreManager.currentAccountId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val transactions: StateFlow<List<TransactionEntity>> = currentAccountId
        .flatMapLatest { accountId -> transactionRepository.getAll(accountId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<CategoryEntity>> = currentAccountId
        .flatMapLatest { accountId -> categoryRepository.getAll(accountId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addTransaction(
        amount: Double,
        type: TransactionType,
        categoryId: Long?,
        date: Long,
        description: String?
    ) {
        viewModelScope.launch {
            transactionRepository.insert(
                TransactionEntity(
                    amount = amount,
                    type = type,
                    categoryId = categoryId,
                    date = date,
                    description = description,
                    accountId = currentAccountId.value
                )
            )
        }
    }

    fun updateTransaction(transaction: TransactionEntity) {
        viewModelScope.launch { transactionRepository.update(transaction) }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch { transactionRepository.delete(transaction) }
    }

    fun addCategory(name: String, type: TransactionType) {
        viewModelScope.launch {
            categoryRepository.insert(
                CategoryEntity(
                    name = name,
                    iconName = "category",
                    colorHex = "#78909C",
                    type = type,
                    accountId = currentAccountId.value
                )
            )
        }
    }

    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch { categoryRepository.delete(category) }
    }
}
