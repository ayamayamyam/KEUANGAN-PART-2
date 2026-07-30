package com.student.finance.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.student.finance.ui.viewmodel.SettingsViewModel
import com.student.finance.util.BiometricHelper
import androidx.compose.ui.platform.LocalContext

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val currency by viewModel.currency.collectAsState()
    val darkMode by viewModel.darkMode.collectAsState()
    val appLockEnabled by viewModel.appLockEnabled.collectAsState()
    val biometricEnabled by viewModel.biometricEnabled.collectAsState()
    val context = LocalContext.current

    var showPinSetup by remember { mutableStateOf(false) }
    var showPinDialog by remember { mutableStateOf(false) }

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
        SettingRow(label = "Kunci Aplikasi (PIN)") {
            Switch(
                checked = appLockEnabled,
                onCheckedChange = { enabled ->
                    if (enabled) {
                        showPinSetup = true
                    } else {
                        viewModel.setAppLockEnabled(false)
                        viewModel.setPinCode(null)
                        viewModel.setBiometricEnabled(false)
                    }
                }
            )
        }
        if (appLockEnabled && BiometricHelper.canAuthenticate(context)) {
            SettingRow(label = "Gunakan Biometrik") {
                Switch(
                    checked = biometricEnabled,
                    onCheckedChange = { viewModel.setBiometricEnabled(it) }
                )
            }
        }
        HorizontalDivider(Modifier.padding(vertical = 12.dp))

        Text(
            "Semua data disimpan secara lokal di perangkat ini. Tidak ada data yang dikirim ke server.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    if (showPinSetup) {
        com.student.finance.ui.screens.security.PinSetupScreen(
            onPinSet = { pin ->
                viewModel.setPinCode(pin)
                viewModel.setAppLockEnabled(true)
                if (BiometricHelper.canAuthenticate(context)) {
                    viewModel.setBiometricEnabled(true)
                }
                showPinSetup = false
            },
            onCancel = {
                showPinSetup = false
                viewModel.setAppLockEnabled(false)
            }
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
