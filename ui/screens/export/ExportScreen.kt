package com.student.finance.ui.screens.export

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportScreen(viewModel: ExportViewModel = hiltViewModel()) {
    val context = LocalContext.current
    var selectedFormat by remember { mutableStateOf("CSV") }
    var showDatePicker by remember { mutableStateOf(false) }
    var startDate by remember { mutableStateOf<Long?>(null) }
    var endDate by remember { mutableStateOf<Long?>(null) }
    var isExporting by remember { mutableStateOf(false) }

    val formats = listOf("CSV", "PDF", "Excel")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Ekspor Data Transaksi",
            style = MaterialTheme.typography.headlineSmall
        )

        // Format Selection
        Text("Format File", style = MaterialTheme.typography.titleMedium)
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            formats.forEachIndexed { index, format ->
                SegmentedButton(
                    selected = selectedFormat == format,
                    onClick = { selectedFormat = format },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = formats.size)
                ) {
                    Text(format)
                }
            }
        }

        // Date Range
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Rentang Tanggal", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    DateButton(
                        label = "Dari",
                        date = startDate,
                        onClick = { /* Show date picker for start */ }
                    )
                    DateButton(
                        label = "Sampai",
                        date = endDate,
                        onClick = { /* Show date picker for end */ }
                    )
                }
            }
        }

        Button(
            onClick = {
                isExporting = true
                viewModel.exportData(
                    format = selectedFormat,
                    startDate = startDate,
                    endDate = endDate,
                    onComplete = { file ->
                        isExporting = false
                        shareFile(context, file)
                    }
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isExporting
        ) {
            if (isExporting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Icon(Icons.Default.Download, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Ekspor Data")
        }
    }
}

@Composable
fun DateButton(label: String, date: Long?, onClick: () -> Unit) {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale("id"))
    OutlinedButton(onClick = onClick) {
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall)
            Text(
                date?.let { formatter.format(Date(it)) } ?: "Pilih Tanggal",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

fun shareFile(context: Context, file: File) {
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = context.contentResolver.getType(uri) ?: "*/*"
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(Intent.createChooser(intent, "Bagikan File"))
}
