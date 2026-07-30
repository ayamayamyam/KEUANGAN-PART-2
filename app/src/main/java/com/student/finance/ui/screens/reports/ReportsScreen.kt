package com.student.finance.ui.screens.reports

import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.student.finance.data.local.entity.TransactionType
import com.student.finance.ui.viewmodel.TransactionViewModel
import com.student.finance.util.CurrencyFormatter

@Composable
fun ReportsScreen(viewModel: TransactionViewModel = hiltViewModel()) {
    val transactions by viewModel.transactions.collectAsState()
    val categories by viewModel.categories.collectAsState()

    val expenseByCategory = transactions
        .filter { it.type == TransactionType.EXPENSE }
        .groupBy { it.categoryId }
        .mapValues { entry -> entry.value.sumOf { it.amount } }
        .toList()
        .sortedByDescending { it.second }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Pengeluaran per Kategori", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        if (expenseByCategory.isEmpty()) {
            Text("Belum ada data pengeluaran bulan ini.")
        } else {
            val total = expenseByCategory.sumOf { it.second }
            expenseByCategory.forEach { (categoryId, amount) ->
                val category = categories.find { it.id == categoryId }
                val percent = if (total > 0) (amount / total * 100) else 0.0
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(category?.name ?: "Tanpa Kategori")
                    Text("${CurrencyFormatter.format(amount, "IDR")} (${"%.0f".format(percent)}%)")
                }
                LinearProgressIndicator(
                    progress = { (percent / 100).toFloat() },
                    modifier = Modifier.fillMaxWidth().height(6.dp)
                )
                Spacer(Modifier.height(4.dp))
            }
        }
    }
}
