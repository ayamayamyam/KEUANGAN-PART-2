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
import com.student.finance.ui.components.BudgetProgressBar
import com.student.finance.ui.components.EmptyState
import com.student.finance.ui.components.SummaryCard
import com.student.finance.ui.components.TransactionRow
import com.student.finance.ui.theme.ExpenseRed
import com.student.finance.ui.theme.IncomeGreen
import com.student.finance.ui.viewmodel.BudgetViewModel
import com.student.finance.ui.viewmodel.DashboardViewModel
import com.student.finance.ui.viewmodel.DebtViewModel
import com.student.finance.ui.viewmodel.SavingGoalViewModel
import com.student.finance.ui.viewmodel.TransactionViewModel
import com.student.finance.util.CurrencyFormatter
import com.student.finance.util.DateUtils
import java.util.*

@Composable
fun DashboardScreen(
    dashboardViewModel: DashboardViewModel = hiltViewModel(),
    transactionViewModel: TransactionViewModel = hiltViewModel(),
    budgetViewModel: BudgetViewModel = hiltViewModel(),
    debtViewModel: DebtViewModel = hiltViewModel(),
    savingGoalViewModel: SavingGoalViewModel = hiltViewModel()
) {
    val uiState by dashboardViewModel.uiState.collectAsState()
    val transactions by transactionViewModel.transactions.collectAsState()
    val categories by transactionViewModel.categories.collectAsState()
    val budgetsWithSpent by budgetViewModel.budgetsWithSpent.collectAsState()
    val totalRemaining by budgetViewModel.totalRemaining.collectAsState()
    val debtUiState by debtViewModel.uiState.collectAsState()
    val goals by savingGoalViewModel.goals.collectAsState()

    val calendar = Calendar.getInstance()
    val todayStart = calendar.apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }.timeInMillis
    val todayEnd = todayStart + 86400000 - 1
    val weekStart = todayStart - ((calendar.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY + 7) % 7) * 86400000L
    val monthStart = DateUtils.startOfMonth(DateUtils.currentMonth(), DateUtils.currentYear())
    val monthEnd = DateUtils.endOfMonth(DateUtils.currentMonth(), DateUtils.currentYear())

    val todayIncome = transactions.filter { it.type == com.student.finance.data.local.entity.TransactionType.INCOME && it.date in todayStart..todayEnd }.sumOf { it.amount }
    val todayExpense = transactions.filter { it.type == com.student.finance.data.local.entity.TransactionType.EXPENSE && it.date in todayStart..todayEnd }.sumOf { it.amount }
    val weekIncome = transactions.filter { it.type == com.student.finance.data.local.entity.TransactionType.INCOME && it.date >= weekStart }.sumOf { it.amount }
    val weekExpense = transactions.filter { it.type == com.student.finance.data.local.entity.TransactionType.EXPENSE && it.date >= weekStart }.sumOf { it.amount }
    val monthIncome = transactions.filter { it.type == com.student.finance.data.local.entity.TransactionType.INCOME && it.date in monthStart..monthEnd }.sumOf { it.amount }
    val monthExpense = transactions.filter { it.type == com.student.finance.data.local.entity.TransactionType.EXPENSE && it.date in monthStart..monthEnd }.sumOf { it.amount }

    val totalSavings = goals.sumOf { it.savedAmount }
    val totalSavingsTarget = goals.sumOf { it.targetAmount }

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
                        CurrencyFormatter.format(uiState.balance, uiState.currency),
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
            Spacer(Modifier.height(12.dp))

            // Card Arus Kas
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Arus Kas", style = MaterialTheme.typography.labelSmall)
                    Text(
                        "Net: ${CurrencyFormatter.format(debtUiState.netDebt, uiState.currency)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (debtUiState.netDebt >= 0) IncomeGreen else ExpenseRed
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Menghutangi: ${CurrencyFormatter.format(debtUiState.totalLent, uiState.currency)} | Berhutang: ${CurrencyFormatter.format(debtUiState.totalBorrowed, uiState.currency)}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Spacer(Modifier.height(12.dp))

            // Card Tabungan
            if (goals.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Tabungan", style = MaterialTheme.typography.labelSmall)
                        Text(
                            "${CurrencyFormatter.format(totalSavings, uiState.currency)} / ${CurrencyFormatter.format(totalSavingsTarget, uiState.currency)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(8.dp))
                        BudgetProgressBar(spent = totalSavings, limit = totalSavingsTarget)
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            // Sisa Anggaran
            if (budgetsWithSpent.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Sisa Anggaran", style = MaterialTheme.typography.labelSmall)
                        Text(
                            CurrencyFormatter.format(totalRemaining, uiState.currency),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (totalRemaining >= 0) IncomeGreen else ExpenseRed
                        )
                        Spacer(Modifier.height(8.dp))
                        val totalBudget = budgetsWithSpent.sumOf { it.budget.limitAmount }
                        val totalSpent = budgetsWithSpent.sumOf { it.spent }
                        BudgetProgressBar(spent = totalSpent, limit = totalBudget)
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            // Rekap Harian / Mingguan / Bulanan
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Rekap", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    RecapRow("Hari Ini", todayIncome, todayExpense, uiState.currency)
                    HorizontalDivider(Modifier.padding(vertical = 4.dp))
                    RecapRow("Minggu Ini", weekIncome, weekExpense, uiState.currency)
                    HorizontalDivider(Modifier.padding(vertical = 4.dp))
                    RecapRow("Bulan Ini", monthIncome, monthExpense, uiState.currency)
                }
            }
            Spacer(Modifier.height(12.dp))

            // Anggaran Hampir Habis
            val almostEmpty = budgetsWithSpent.filter { it.percentage >= it.budget.alertThreshold.toFloat() && it.remaining > 0 }
            if (almostEmpty.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Anggaran Hampir Habis", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.error)
                        almostEmpty.take(3).forEach {
                            Text(
                                "• ${it.category?.name ?: "Kategori"}: sisa ${CurrencyFormatter.format(it.remaining, uiState.currency)}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

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

@Composable
private fun RecapRow(label: String, income: Double, expense: Double, currency: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontWeight = FontWeight.SemiBold)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("+${CurrencyFormatter.format(income, currency)}", color = IncomeGreen, fontWeight = FontWeight.SemiBold)
            Text("-${CurrencyFormatter.format(expense, currency)}", color = ExpenseRed, fontWeight = FontWeight.SemiBold)
        }
    }
}
