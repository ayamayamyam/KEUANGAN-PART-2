package com.student.finance.ui.screens.budget

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.student.finance.ui.components.BudgetProgressBar
import com.student.finance.ui.components.EmptyState
import com.student.finance.ui.viewmodel.BudgetViewModel
import com.student.finance.util.CurrencyFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(addTrigger: Int, viewModel: BudgetViewModel = hiltViewModel()) {
    val budgetsWithSpent by viewModel.budgetsWithSpent.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val totalBudget by viewModel.totalBudget.collectAsState()
    val totalSpent by viewModel.totalSpent.collectAsState()
    val totalRemaining by viewModel.totalRemaining.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(addTrigger) {
        if (addTrigger > 0) showDialog = true
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Ringkasan Anggaran
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Ringkasan Anggaran Bulan Ini", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Total Anggaran", style = MaterialTheme.typography.labelSmall)
                        Text(CurrencyFormatter.format(totalBudget, "IDR"), fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                        Text("Terpakai", style = MaterialTheme.typography.labelSmall)
                        Text(CurrencyFormatter.format(totalSpent, "IDR"), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                    }
                }
                Spacer(Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Sisa Anggaran", style = MaterialTheme.typography.labelSmall)
                    Text(
                        CurrencyFormatter.format(totalRemaining, "IDR"),
                        fontWeight = FontWeight.Bold,
                        color = if (totalRemaining >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                }
                Spacer(Modifier.height(8.dp))
                BudgetProgressBar(spent = totalSpent, limit = totalBudget)
            }
        }

        Spacer(Modifier.height(16.dp))
        Text("Detail per Kategori", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))

        if (budgetsWithSpent.isEmpty()) {
            EmptyState("Belum ada anggaran. Tap + untuk membuat anggaran bulanan.", Modifier.fillMaxSize())
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(budgetsWithSpent) { item ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                        Column(Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(item.category?.name ?: "Kategori", fontWeight = FontWeight.SemiBold)
                                Text(CurrencyFormatter.format(item.budget.limitAmount, "IDR"))
                            }
                            Spacer(Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(
                                    "Terpakai: ${CurrencyFormatter.format(item.spent, "IDR")}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    "Sisa: ${CurrencyFormatter.format(item.remaining, "IDR")}",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (item.remaining > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                            BudgetProgressBar(spent = item.spent, limit = item.budget.limitAmount)
                            if (item.percentage >= item.budget.alertThreshold.toFloat()) {
                                Text(
                                    "Peringatan: Pengeluaran mendekati batas!",
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }

    if (showDialog) {
        AddBudgetDialog(
            categories = categories.filter { it.type == com.student.finance.data.local.entity.TransactionType.EXPENSE },
            onDismiss = { showDialog = false },
            onConfirm = { categoryId, limit ->
                viewModel.addBudget(categoryId, limit)
                showDialog = false
            }
        )
    }
}

@Composable
private fun AddBudgetDialog(
    categories: List<com.student.finance.data.local.entity.CategoryEntity>,
    onDismiss: () -> Unit,
    onConfirm: (Long, Double) -> Unit
) {
    var selectedCategoryId by remember { mutableStateOf(categories.firstOrNull()?.id) }
    var limitText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Anggaran Baru") },
        text = {
            Column {
                categories.forEach { cat ->
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        RadioButton(selected = selectedCategoryId == cat.id, onClick = { selectedCategoryId = cat.id })
                        Text(cat.name)
                    }
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = limitText,
                    onValueChange = { limitText = it.filter { c -> c.isDigit() } },
                    label = { Text("Batas Anggaran (Rp)") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val limit = limitText.toDoubleOrNull() ?: 0.0
                val catId = selectedCategoryId
                if (limit > 0 && catId != null) onConfirm(catId, limit)
            }) { Text("Simpan") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}
