package com.student.finance.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.student.finance.data.local.DataStoreManager
import com.student.finance.data.local.entity.AccountEntity
import com.student.finance.data.repository.AccountRepository
import com.student.finance.data.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    val accounts: StateFlow<List<AccountEntity>> = accountRepository.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentAccountId: StateFlow<Long> = dataStoreManager.currentAccountId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    fun addAccount(name: String) {
        viewModelScope.launch {
            val id = accountRepository.insert(AccountEntity(name = name))
            categoryRepository.seedDefaultsIfEmpty(id)
            dataStoreManager.setCurrentAccountId(id)
        }
    }

    fun switchAccount(id: Long) {
        viewModelScope.launch {
            categoryRepository.seedDefaultsIfEmpty(id)
            dataStoreManager.setCurrentAccountId(id)
        }
    }

    fun deleteAccount(account: AccountEntity) {
        viewModelScope.launch {
            accountRepository.delete(account)
            val remainingAccounts = accounts.value.filter { it.id != account.id }
            if (currentAccountId.value == account.id) {
                if (remainingAccounts.isNotEmpty()) {
                    val nextId = remainingAccounts.first().id
                    categoryRepository.seedDefaultsIfEmpty(nextId)
                    dataStoreManager.setCurrentAccountId(nextId)
                } else {
                    val defaultId = accountRepository.insert(AccountEntity(name = "Akun Utama"))
                    categoryRepository.seedDefaultsIfEmpty(defaultId)
                    dataStoreManager.setCurrentAccountId(defaultId)
                }
            }
        }
    }
}
