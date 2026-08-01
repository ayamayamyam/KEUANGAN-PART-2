package com.student.finance.ui.screens.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.student.finance.data.local.entity.CategoryEntity
import com.student.finance.data.local.entity.TransactionType
import com.student.finance.data.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val repository: CategoryRepository
) : ViewModel() {

    val categories = repository.getAll(accountId = 1)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun add(name: String, type: TransactionType, icon: String) {
        viewModelScope.launch {
            repository.insert(
                CategoryEntity(
                    name = name,
                    type = type,
                    icon = icon
                )
            )
        }
    }

    fun update(category: CategoryEntity) {
        viewModelScope.launch {
            repository.insert(category)
        }
    }

    fun delete(category: CategoryEntity) {
        viewModelScope.launch {
            repository.delete(category)
        }
    }
}
