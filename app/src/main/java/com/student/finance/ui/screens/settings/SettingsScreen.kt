package com.student.finance.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.student.finance.ui.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val currency by viewModel.currency.collectAsState()
    val darkMode by viewModel.darkMode.collectAsState()
    val appLockEnabled by viewModel.appLockEnabled.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Tampilan", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        SettingRow(label = "Mode Gelap") {
            Switch(checked = darkMode, onCheckedChange = { viewModel.setDarkMode(it) })
        }
        HorizontalDivider(Modifier.padding(vertical = 12.dp))

        Text("Mata Uang", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("IDR", "USD").forEach { code ->
                FilterChip(
                    selected = currency == code,
                    onClick = { viewModel.setCurrency(code) },
                    label = { Text(code) }
                )
            }
        }
        HorizontalDivider(Modifier.padding(vertical = 12.dp))

        Text("Keamanan", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        SettingRow(label = "Kunci Aplikasi (PIN/Biometrik)") {
            Switch(checked = appLockEnabled, onCheckedChange = { viewModel.setAppLockEnabled(it) })
        }
        HorizontalDivider(Modifier.padding(vertical = 12.dp))

        Text(
            "Semua data disimpan secara lokal di perangkat ini. Tidak ada data yang dikirim ke server.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SettingRow(label: String, content: @Composable () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Text(label)
        content()
    }
}
