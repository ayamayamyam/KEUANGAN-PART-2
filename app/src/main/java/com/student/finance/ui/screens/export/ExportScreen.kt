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
import com.student.finance.ui.viewmodel.SettingsViewModel
import com.student.finance.ui.viewmodel.TransactionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ExportScreen(
    viewModel: TransactionViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val transactions by viewModel.transactions.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val currency by settingsViewModel.currency.collectAsState()

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
                    message = backupCsvToUri(context, uri, transactions, categories, currency)
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
            "Export ke format Excel (CSV) yang bisa dibuka di Microsoft Excel",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))

        Button(
            onClick = {
                scope.launch {
                    isExporting = true
                    message = exportCsv(context, transactions, categories, currency)
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
            Text("Export ke Excel (CSV)")
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
                    type = "text/csv"
                    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale("in", "ID")).format(Date())
                    putExtra(Intent.EXTRA_TITLE, "smartmoney_export_$timestamp.csv")
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
    return File(context.cacheDir, "student_finance_export.csv")
}

private suspend fun exportCsv(
    context: Context,
    transactions: List<com.student.finance.data.local.entity.TransactionEntity>,
    categories: List<com.student.finance.data.local.entity.CategoryEntity>,
    currency: String
): String {
    return withContext(Dispatchers.IO) {
        try {
            val csv = buildCsv(transactions, categories, currency)
            val file = getLastExportFile(context)
            file.writeText(csv, Charsets.UTF_8)
            "Berhasil diekspor: ${file.name}"
        } catch (e: Exception) {
            "Gagal: ${e.localizedMessage}"
        }
    }
}

private suspend fun backupCsvToUri(
    context: Context,
    uri: android.net.Uri,
    transactions: List<com.student.finance.data.local.entity.TransactionEntity>,
    categories: List<com.student.finance.data.local.entity.CategoryEntity>,
    currency: String
): String {
    return withContext(Dispatchers.IO) {
        try {
            val csv = buildCsv(transactions, categories, currency)
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(csv.toByteArray(Charsets.UTF_8))
            }
            "Berhasil backup ke Google Drive!"
        } catch (e: Exception) {
            "Gagal backup: ${e.localizedMessage}"
        }
    }
}

private fun buildCsv(
    transactions: List<com.student.finance.data.local.entity.TransactionEntity>,
    categories: List<com.student.finance.data.local.entity.CategoryEntity>,
    currency: String
): String {
    val sb = StringBuilder()
    // BOM untuk Excel Indonesia
    sb.append('\uFEFF')
    // Header
    sb.append("No;Tanggal;Tipe;Kategori;Jumlah ($currency);Deskripsi\n")

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("in", "ID"))
    val catMap = categories.associateBy { it.id }

    transactions.sortedByDescending { it.date }.forEachIndexed { index, tx ->
        val catName = tx.categoryId?.let { catMap[it]?.name } ?: "Tanpa Kategori"
        val dateStr = dateFormat.format(Date(tx.date))
        val typeStr = if (tx.type.name == "INCOME") "Pemasukan" else "Pengeluaran"
        val amountStr = String.format(Locale("in", "ID"), "%,.2f", tx.amount)

        sb.append("${index + 1};")
        sb.append("$dateStr;")
        sb.append("$typeStr;")
        sb.append("$catName;")
        sb.append("$amountStr;")
        sb.append("${tx.description ?: "-"}\n")
    }

    // Summary
    val totalIncome = transactions.filter { it.type.name == "INCOME" }.sumOf { it.amount }
    val totalExpense = transactions.filter { it.type.name == "EXPENSE" }.sumOf { it.amount }
    val balance = totalIncome - totalExpense

    sb.append("\n;Ringkasan;;;\n")
    sb.append(";;Total Pemasukan;${String.format(Locale("in", "ID"), "%,.2f", totalIncome)};\n")
    sb.append(";;Total Pengeluaran;${String.format(Locale("in", "ID"), "%,.2f", totalExpense)};\n")
    sb.append(";;Saldo;${String.format(Locale("in", "ID"), "%,.2f", balance)};\n")

    return sb.toString()
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
        type = "text/csv"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_SUBJECT, "Smart Money Export")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    val chooser = Intent.createChooser(shareIntent, "Bagikan via")
    chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(chooser)
}
