package com.student.finance.ui.screens.transaction

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.student.finance.ui.components.EmptyState
import com.student.finance.ui.components.TransactionRow
import com.student.finance.ui.viewmodel.SettingsViewModel
import com.student.finance.ui.viewmodel.TransactionViewModel

@Composable
fun TransactionListScreen(
    transactionViewModel: TransactionViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val transactions by transactionViewModel.transactions.collectAsState()
    val categories by transactionViewModel.categories.collectAsState()
    val currency by settingsViewModel.currency.collectAsState()

    if (transactions.isEmpty()) {
        EmptyState("Belum ada transaksi.", Modifier.fillMaxSize())
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            items(transactions) { tx ->
                val category = categories.find { it.id == tx.categoryId }
                TransactionRow(tx, category, currency)
                HorizontalDivider()
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}
