package com.student.finance.ui.screens.category

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.student.finance.data.local.entity.CategoryEntity
import com.student.finance.data.local.entity.TransactionType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryManagementScreen(viewModel: CategoryViewModel = hiltViewModel()) {
    val categories by viewModel.categories.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<CategoryEntity?>(null) }
    var selectedType by remember { mutableStateOf(TransactionType.EXPENSE) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            SegmentedButton(
                selected = selectedType == TransactionType.EXPENSE,
                onClick = { selectedType = TransactionType.EXPENSE },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
            ) {
                Text("Pengeluaran")
            }
            SegmentedButton(
                selected = selectedType == TransactionType.INCOME,
                onClick = { selectedType = TransactionType.INCOME },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
            ) {
                Text("Pemasukan")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        val filteredCategories = categories.filter { it.type == selectedType }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filteredCategories) { category ->
                CategoryCard(
                    category = category,
                    onEdit = {
                        editingCategory = category
                        showDialog = true
                    },
                    onDelete = { viewModel.delete(category) }
                )
            }
        }
    }

    FloatingActionButton(
        onClick = {
            editingCategory = null
            showDialog = true
        },
        modifier = Modifier.padding(16.dp)
    ) {
        Icon(Icons.Default.Add, contentDescription = "Tambah Kategori")
    }

    if (showDialog) {
        CategoryDialog(
            category = editingCategory,
            type = selectedType,
            onDismiss = { showDialog = false },
            onConfirm = { name, icon ->
                if (editingCategory != null) {
                    viewModel.update(editingCategory!!.copy(name = name, icon = icon))
                } else {
                    viewModel.add(name, selectedType, icon)
                }
                showDialog = false
            }
        )
    }
}

@Composable
fun CategoryCard(
    category: CategoryEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                Icon(
                    imageVector = Icons.Default.Label,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 12.dp)
                )
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Hapus")
                }
            }
        }
    }
}

@Composable
fun CategoryDialog(
    category: CategoryEntity?,
    type: TransactionType,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var name by remember { mutableStateOf(category?.name ?: "") }
    var icon by remember { mutableStateOf(category?.icon ?: "label") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (category != null) "Edit Kategori" else "Tambah Kategori") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Kategori") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = icon,
                    onValueChange = { icon = it },
                    label = { Text("Icon (nama material icon)") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name, icon) },
                enabled = name.isNotBlank()
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
