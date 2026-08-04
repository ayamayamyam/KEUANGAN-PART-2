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
                color = if (it.startsWith("Berhasil")) MaterialTheme.colorScheme.primary
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
): String {
    return withContext(Dispatchers.IO) {
        try {
            val sb = StringBuilder()
            sb.append("{\n")
            sb.append("  \"exportedAt\": \"${escapeJson(SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("in", "ID")).format(Date()))}\",\n")
            sb.append("  \"categories\": [\n")
            
            categories.forEachIndexed { index, cat ->
                sb.append("    {\n")
                sb.append("      \"id\": ${cat.id},\n")
                sb.append("      \"name\": \"${escapeJson(cat.name)}\",\n")
                sb.append("      \"iconName\": \"${escapeJson(cat.iconName)}\",\n")
                sb.append("      \"colorHex\": \"${escapeJson(cat.colorHex)}\",\n")
                sb.append("      \"type\": \"${cat.type.name}\"\n")
                sb.append("    }")
                if (index < categories.size - 1) sb.append(",")
                sb.append("\n")
            }
            
            sb.append("  ],\n")
            sb.append("  \"transactions\": [\n")
            
            transactions.forEachIndexed { index, tx ->
                sb.append("    {\n")
                sb.append("      \"id\": ${tx.id},\n")
                sb.append("      \"amount\": ${tx.amount},\n")
                sb.append("      \"type\": \"${tx.type.name}\",\n")
                sb.append("      \"categoryId\": ${tx.categoryId ?: "null"},\n")
                sb.append("      \"date\": ${tx.date},\n")
                sb.append("      \"description\": ${if (tx.description != null) "\"${escapeJson(tx.description)}\"" else "null"},\n")
                sb.append("      \"isRecurring\": ${tx.isRecurring},\n")
                sb.append("      \"recurringInterval\": ${if (tx.recurringInterval != null) "\"${escapeJson(tx.recurringInterval)}\"" else "null"}\n")
                sb.append("    }")
                if (index < transactions.size - 1) sb.append(",")
                sb.append("\n")
            }
            
            sb.append("  ]\n")
            sb.append("}")
            
            val file = getLastExportFile(context)
            file.writeText(sb.toString())
            
            "Berhasil diekspor: ${file.name}"
        } catch (e: Exception) {
            "Gagal: ${e.localizedMessage}"
        }
    }
}

private fun escapeJson(input: String?): String {
    if (input == null) return ""
    return input
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\t", "\\t")
}

private fun shareLastExport(context: Context) {
    val file = getLastExportFile(context)
    if (!file.exists()) return

    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "application/json"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_SUBJECT, "Student Finance Export")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    val chooser = Intent.createChooser(shareIntent, "Bagikan via")
    chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(chooser)
}
