package com.student.finance.ui.screens.export

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
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
    var isBackingUp by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }

    val driveLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                scope.launch {
                    isBackingUp = true
                    message = backupToUri(context, uri, transactions, categories)
                    isBackingUp = false
                }
            }
        }
    }

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
            "Ekspor & Backup Data",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Simpan atau bagikan semua data keuanganmu",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))

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
            Text("Export ke File JSON")
        }

        Spacer(Modifier.height(12.dp))
        OutlinedButton(
            onClick = { scope.launch { shareLastExport(context) } },
            enabled = getLastExportFile(context).exists(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Bagikan File Terakhir")
        }

        Spacer(Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "application/json"
                    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale("in", "ID")).format(Date())
                    putExtra(Intent.EXTRA_TITLE, "smartmoney_backup_$timestamp.json")
                }
                driveLauncher.launch(intent)
            },
            enabled = !isBackingUp && transactions.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            if (isBackingUp) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onSecondary
                )
                Spacer(Modifier.width(8.dp))
            }
            Icon(Icons.Filled.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Backup ke Google Drive")
        }

        Spacer(Modifier.height(8.dp))
        Text(
            "Pilih Google Drive saat muncul folder",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        message?.let {
            Spacer(Modifier.height(20.dp))
            Text(
                it,
                style = MaterialTheme.typography.bodyMedium,
                color = if (it.startsWith("Berhasil")) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.error,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
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
            val json = buildJson(transactions, categories)
            val file = getLastExportFile(context)
            file.writeText(json)
            "Berhasil diekspor: ${file.name}"
        } catch (e: Exception) {
            "Gagal: ${e.localizedMessage}"
        }
    }
}

private suspend fun backupToUri(
    context: Context,
    uri: android.net.Uri,
    transactions: List<com.student.finance.data.local.entity.TransactionEntity>,
    categories: List<com.student.finance.data.local.entity.CategoryEntity>
): String {
    return withContext(Dispatchers.IO) {
        try {
            val json = buildJson(transactions, categories)
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(json.toByteArray(Charsets.UTF_8))
            }
            "Berhasil backup ke Google Drive!"
        } catch (e: Exception) {
            "Gagal backup: ${e.localizedMessage}"
        }
    }
}

private fun buildJson(
    transactions: List<com.student.finance.data.local.entity.TransactionEntity>,
    categories: List<com.student.finance.data.local.entity.CategoryEntity>
): String {
    val sb = StringBuilder()
    sb.append("{\n")
    sb.append("  \"exportedAt\": \"${escapeJson(SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("in", "ID")).format(Date()))}\",\n")
    sb.append("  \"appVersion\": \"3.1\",\n")
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
    return sb.toString()
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
