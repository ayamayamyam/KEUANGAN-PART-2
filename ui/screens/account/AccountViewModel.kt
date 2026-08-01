package com.student.finance.ui.screens.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.student.finance.data.local.entity.AccountEntity
import com.student.finance.data.repository.AccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val repository: AccountRepository
) : ViewModel() {

    val accounts = repository.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun add(name: String, type: String, balance: Double, icon: String, color: String) {
        viewModelScope.launch {
            repository.insert(
                AccountEntity(
                    name = name,
                    type = type,
                    balance = balance,
                    icon = icon,
                    color = color
                )
            )
        }
    }

    fun update(account: AccountEntity) {
        viewModelScope.launch {
            repository.update(account)
        }
    }

    fun delete(account: AccountEntity) {
        viewModelScope.launch {
            repository.delete(account)
        }
    }

    fun setActive(accountId: Long) {
        viewModelScope.launch {
            repository.setActive(accountId)
        }
    }
}
