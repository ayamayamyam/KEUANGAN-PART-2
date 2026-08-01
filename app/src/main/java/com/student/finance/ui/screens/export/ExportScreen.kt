package com.student.finance.ui.screens.export

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ExportScreen(viewModel: ExportViewModel = hiltViewModel()) {
    var selectedFormat by remember { mutableStateOf("CSV") }
    var isExporting by remember { mutableStateOf(false) }
    val formats = listOf("CSV", "PDF")

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

        Text("Format File", style = MaterialTheme.typography.titleMedium)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            formats.forEach { format ->
                FilterChip(
                    selected = selectedFormat == format,
                    onClick = { selectedFormat = format },
                    label = { Text(format) }
                )
            }
        }

        Button(
            onClick = {
                isExporting = true
                viewModel.exportData(
                    format = selectedFormat,
                    startDate = null,
                    endDate = null,
                    onComplete = {
                        isExporting = false
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
