package com.student.finance.ui.screens.savings

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
import com.student.finance.data.local.entity.SavingGoalEntity
import com.student.finance.ui.components.BudgetProgressBar
import com.student.finance.ui.components.EmptyState
import com.student.finance.ui.viewmodel.SavingGoalViewModel
import com.student.finance.util.CurrencyFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavingGoalScreen(addTrigger: Int, viewModel: SavingGoalViewModel = hiltViewModel()) {
    val goals by viewModel.goals.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var depositTarget by remember { mutableStateOf<SavingGoalEntity?>(null) }

    LaunchedEffect(addTrigger) {
        if (addTrigger > 0) showAddDialog = true
    }

    if (goals.isEmpty()) {
        EmptyState("Belum ada target menabung.", Modifier.fillMaxSize())
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            items(goals) { goal ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    onClick = { depositTarget = goal }
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(goal.name, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "${CurrencyFormatter.format(goal.savedAmount, "IDR")} / ${CurrencyFormatter.format(goal.targetAmount, "IDR")}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(Modifier.height(8.dp))
                        BudgetProgressBar(spent = goal.savedAmount, limit = goal.targetAmount)
                    }
                }
            }
            item { Spacer(Modifier.height(80.dp)) }
        }
    }

    if (showAddDialog) {
        AddGoalDialog(onDismiss = { showAddDialog = false }, onConfirm = { name, target ->
            viewModel.addGoal(name, target, null)
            showAddDialog = false
        })
    }

    depositTarget?.let { goal ->
        DepositDialog(
            goal = goal,
            onDismiss = { depositTarget = null },
            onConfirm = { amount ->
                viewModel.addToSavings(goal, amount)
                depositTarget = null
            }
        )
    }
}

@Composable
private fun AddGoalDialog(onDismiss: () -> Unit, onConfirm: (String, Double) -> Unit) {
    var name by remember { mutableStateOf("") }
    var target by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Target Menabung Baru") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama target") })
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = target,
                    onValueChange = { target = it.filter { c -> c.isDigit() } },
                    label = { Text("Target (Rp)") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val amount = target.toDoubleOrNull() ?: 0.0
                if (name.isNotBlank() && amount > 0) onConfirm(name, amount)
            }) { Text("Simpan") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}

@Composable
private fun DepositDialog(goal: SavingGoalEntity, onDismiss: () -> Unit, onConfirm: (Double) -> Unit) {
    var amountText by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Tabungan: ${goal.name}") },
        text = {
            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it.filter { c -> c.isDigit() } },
                label = { Text("Jumlah (Rp)") },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        },
        confirmButton = {
            TextButton(onClick = {
                val amount = amountText.toDoubleOrNull() ?: 0.0
                if (amount > 0) onConfirm(amount)
            }) { Text("Tambah") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}
