package com.student.finance.ui.screens.debt

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.student.finance.data.local.entity.DebtEntity
import com.student.finance.data.local.entity.DebtStatus
import com.student.finance.data.local.entity.DebtType
import com.student.finance.ui.components.EmptyState
import com.student.finance.ui.viewmodel.DebtViewModel
import com.student.finance.util.CurrencyFormatter
import com.student.finance.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebtScreen(
    addTrigger: Int,
    viewModel: DebtViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val allDebts by viewModel.allDebts.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) }

    LaunchedEffect(addTrigger) {
        if (addTrigger > 0) showDialog = true
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Ringkasan Arus Kas
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Ringkasan Arus Kas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("Menghutangi", style = MaterialTheme.typography.labelSmall)
                        Text(CurrencyFormatter.format(uiState.totalLent, "IDR"), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Berhutang", style = MaterialTheme.typography.labelSmall)
                        Text(CurrencyFormatter.format(uiState.totalBorrowed, "IDR"), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                    }
                }
                Spacer(Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(Modifier.height(4.dp))
                Text(
                    "Net: ${CurrencyFormatter.format(uiState.netDebt, "IDR")}",
                    fontWeight = FontWeight.Bold,
                    color = if (uiState.netDebt >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }
        }

        Spacer(Modifier.height(12.dp))
        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) { Text("Semua") }
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) { Text("Menghutangi") }
            Tab(selected = selectedTab == 2, onClick = { selectedTab = 2 }) { Text("Berhutang") }
        }

        val filteredDebts = when (selectedTab) {
            1 -> allDebts.filter { it.type == DebtType.LENT }
            2 -> allDebts.filter { it.type == DebtType.BORROWED }
            else -> allDebts
        }

        if (filteredDebts.isEmpty()) {
            EmptyState("Belum ada data arus kas. Tap + untuk menambahkan.", Modifier.fillMaxSize())
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filteredDebts) { debt ->
                    DebtCard(debt = debt, onMarkPaid = { viewModel.markAsPaid(debt) }, onDelete = { viewModel.deleteDebt(debt) })
                    HorizontalDivider()
                }
                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }

    if (showDialog) {
        AddDebtDialog(
            onDismiss = { showDialog = false },
            onConfirm = { name, amount, type, desc, due ->
                viewModel.addDebt(name, amount, type, desc, System.currentTimeMillis(), due)
                showDialog = false
            }
        )
    }
}

@Composable
private fun DebtCard(
    debt: DebtEntity,
    onMarkPaid: () -> Unit,
    onDelete: () -> Unit
) {
    val isLent = debt.type == DebtType.LENT
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Column(Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(debt.personName, fontWeight = FontWeight.SemiBold)
                    Text(
                        if (isLent) "Menghutangi" else "Berhutang ke",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isLent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                }
                Text(
                    CurrencyFormatter.format(debt.amount, "IDR"),
                    fontWeight = FontWeight.Bold,
                    color = if (isLent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }
            debt.description?.let {
                Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text("Tanggal: ${DateUtils.formatDate(debt.date)}", style = MaterialTheme.typography.bodySmall)
            debt.dueDate?.let {
                Text("Jatuh tempo: ${DateUtils.formatDate(it)}", style = MaterialTheme.typography.bodySmall)
            }
            if (debt.status == DebtStatus.PAID) {
                Text("LUNAS", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            } else {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onMarkPaid) { Text("Tandai Lunas") }
                    TextButton(onClick = onDelete) { Text("Hapus", color = MaterialTheme.colorScheme.error) }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddDebtDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Double, DebtType, String?, Long?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(DebtType.LENT) }
    var description by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf<Long?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Arus Kas") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Orang") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it.filter { c -> c.isDigit() } },
                    label = { Text("Jumlah (Rp)") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = type == DebtType.LENT,
                        onClick = { type = DebtType.LENT },
                        shape = SegmentedButtonDefaults.itemShape(0, 2)
                    ) { Text("Menghutangi") }
                    SegmentedButton(
                        selected = type == DebtType.BORROWED,
                        onClick = { type = DebtType.BORROWED },
                        shape = SegmentedButtonDefaults.itemShape(1, 2)
                    ) { Text("Berhutang") }
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Keterangan (opsional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = { showDatePicker = true }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Filled.CalendarToday, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Jatuh Tempo: ${dueDate?.let { DateUtils.formatDate(it) } ?: \"Tidak ada\"}")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val amount = amountText.toDoubleOrNull() ?: 0.0
                if (name.isNotBlank() && amount > 0) {
                    onConfirm(name, amount, type, description.takeIf { it.isNotBlank() }, dueDate)
                }
            }) { Text("Simpan") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )

    if (showDatePicker) {
        val pickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { dueDate = it }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Batal") } }
        ) { DatePicker(state = pickerState) }
    }
}
