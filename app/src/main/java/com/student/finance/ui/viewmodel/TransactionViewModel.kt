package com.student.finance.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.student.finance.data.local.DataStoreManager
import com.student.finance.data.local.entity.TransactionEntity
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
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    private val activeAccountId: StateFlow<Long> = dataStoreManager.activeAccountId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1L)

    val transactions: StateFlow<List<TransactionEntity>> = activeAccountId
        .flatMapLatest { accountId -> transactionRepository.getAll(accountId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories = activeAccountId
        .flatMapLatest { accountId -> categoryRepository.getAll(accountId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addTransaction(
        amount: Double,
        type: com.student.finance.data.local.entity.TransactionType,
        categoryId: Long?,
        date: Long,
        description: String?
    ) {
        viewModelScope.launch {
            val accountId = activeAccountId.value
            transactionRepository.insert(
                TransactionEntity(
                    amount = amount,
                    type = type,
                    categoryId = categoryId,
                    date = date,
                    description = description,
                    accountId = accountId
                )
            )
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch { transactionRepository.delete(transaction) }
    }
}
