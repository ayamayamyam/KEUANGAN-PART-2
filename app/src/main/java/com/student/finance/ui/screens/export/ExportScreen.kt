package com.student.finance.ui.screens.export

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.student.finance.ui.viewmodel.TransactionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ExportScreen(viewModel: TransactionViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val transactions by viewModel.transactions.collectAsState()
    val categories by viewModel.categories.collectAsState()

    var isExporting by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.FileDownload,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Ekspor Data",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Simpan semua transaksi & kategori ke file JSON",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                scope.launch {
                    isExporting = true
                    message = exportData(context, transactions, categories)
                    isExporting = false
                }
            },
            enabled = !isExporting && transactions.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isExporting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(Modifier.width(8.dp))
            }
            Text("Export ke JSON")
        }

        Spacer(Modifier.height(12.dp))
        OutlinedButton(
            onClick = {
                scope.launch {
                    shareLastExport(context)
                }
            },
            enabled = getLastExportFile(context).exists(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Bagikan File Terakhir")
        }

        message?.let {
            Spacer(Modifier.height(16.dp))
            Text(
                it,
                style = MaterialTheme.typography.bodyMedium,
                color = if (it.startsWith("✅")) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.error
            )
        }

        if (transactions.isEmpty()) {
            Spacer(Modifier.height(16.dp))
            Text(
                "Belum ada data untuk diekspor.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun getLastExportFile(context: Context): File {
    return File(context.cacheDir, "student_finance_export.json")
}

private suspend fun exportData(
    context: Context,
    transactions: List<com.student.finance.data.local.entity.TransactionEntity>,
    categories: List<com.student.finance.data.local.entity.CategoryEntity>
): String = withContext(Dispatchers.IO) {
    return@withContext try {
        val exportData = ExportData(
            exportedAt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("in", "ID")).format(Date()),
            categories = categories.map { CategoryExport(it.id, it.name, it.iconName, it.colorHex, it.type.name) },
            transactions = transactions.map {
                TransactionExport(
                    id = it.id,
                    amount = it.amount,
                    type = it.type.name,
                    categoryId = it.categoryId,
                    date = it.date,
                    description = it.description,
                    isRecurring = it.isRecurring,
                    recurringInterval = it.recurringInterval
                )
            }
        )

        val json = Json { prettyPrint = true }.encodeToString(exportData)
        val file = getLastExportFile(context
