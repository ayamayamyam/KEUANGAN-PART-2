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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(addTrigger: Int, viewModel: BudgetViewModel = hiltViewModel()) {
    val budgets by viewModel.budgets.collectAsState()
    val categories by viewModel.categories.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(addTrigger) {
        if (addTrigger > 0) showDialog = true
    }

    if (budgets.isEmpty()) {
        EmptyState("Belum ada anggaran. Tap + untuk membuat anggaran bulanan.", Modifier.fillMaxSize())
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            items(budgets) { budget ->
                val category = categories.find { it.id == budget.categoryId }
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(category?.name ?: "Kategori", fontWeight = FontWeight.SemiBold)
                            Text(com.student.finance.util.CurrencyFormatter.format(budget.limitAmount, "IDR"))
                        }
                        Spacer(Modifier.height(8.dp))
                        BudgetProgressBar(spent = 0.0, limit = budget.limitAmount)
                    }
                }
            }
            item { Spacer(Modifier.height(80.dp)) }
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
