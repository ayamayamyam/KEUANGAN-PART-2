package com.student.finance.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.student.finance.ui.components.EmptyState
import com.student.finance.ui.components.SummaryCard
import com.student.finance.ui.components.TransactionRow
import com.student.finance.ui.theme.ExpenseRed
import com.student.finance.ui.theme.IncomeGreen
import com.student.finance.ui.viewmodel.DashboardViewModel
import com.student.finance.ui.viewmodel.TransactionViewModel

@Composable
fun DashboardScreen(
    dashboardViewModel: DashboardViewModel = hiltViewModel(),
    transactionViewModel: TransactionViewModel = hiltViewModel()
) {
    val uiState by dashboardViewModel.uiState.collectAsState()
    val transactions by transactionViewModel.transactions.collectAsState()
    val categories by transactionViewModel.categories.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(Modifier.padding(20.dp)) {
                    Text("Saldo Bulan Ini", style = MaterialTheme.typography.labelSmall)
                    Text(
                        com.student.finance.util.CurrencyFormatter.format(uiState.balance, uiState.currency),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryCard("Pemasukan", uiState.totalIncome, uiState.currency, IncomeGreen, Modifier.weight(1f))
                SummaryCard("Pengeluaran", uiState.totalExpense, uiState.currency, ExpenseRed, Modifier.weight(1f))
            }
            Spacer(Modifier.height(20.dp))
            Text("Transaksi Terbaru", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
        }

        if (transactions.isEmpty()) {
            item { EmptyState("Belum ada transaksi. Tap + untuk menambahkan.") }
        } else {
            items(transactions.take(10)) { tx ->
                val category = categories.find { it.id == tx.categoryId }
                TransactionRow(tx, category, uiState.currency)
                HorizontalDivider()
            }
        }

        item { Spacer(Modifier.height(80.dp)) }
    }
}
