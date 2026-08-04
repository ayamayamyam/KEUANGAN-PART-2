package com.student.finance.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.student.finance.data.local.DataStoreManager
import com.student.finance.data.local.entity.AccountEntity
import com.student.finance.data.repository.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    val accounts: StateFlow<List<AccountEntity>> = accountRepository.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentAccountId: StateFlow<Long> = dataStoreManager.currentAccountId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    fun addAccount(name: String) {
        viewModelScope.launch {
            accountRepository.insert(AccountEntity(name = name))
        }
    }

    fun switchAccount(id: Long) {
        viewModelScope.launch {
            dataStoreManager.setCurrentAccountId(id)
        }
    }

    fun deleteAccount(account: AccountEntity) {
        viewModelScope.launch {
            accountRepository.delete(account)
        }
    }
}
