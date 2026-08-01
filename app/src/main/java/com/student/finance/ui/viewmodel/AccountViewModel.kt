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
    private val repository: AccountRepository,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    val accounts: StateFlow<List<AccountEntity>> = repository.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeAccountId: StateFlow<Long> = dataStoreManager.activeAccountId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1L)

    fun addAccount(name: String) {
        viewModelScope.launch {
            repository.insert(AccountEntity(name = name))
        }
    }

    fun switchAccount(accountId: Long) {
        viewModelScope.launch {
            repository.switchAccount(accountId)
            dataStoreManager.setActiveAccountId(accountId)
        }
    }

    fun deleteAccount(account: AccountEntity) {
        viewModelScope.launch { repository.delete(account) }
    }
}
