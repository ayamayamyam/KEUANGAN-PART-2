package com.student.finance.ui.screens.export

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.student.finance.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExportViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    fun exportData(
        format: String,
        startDate: Long?,
        endDate: Long?,
        onComplete: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            // TODO: Implementasi export CSV/PDF sesuai kebutuhan
            onComplete(true)
        }
    }
}
