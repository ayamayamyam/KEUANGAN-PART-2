package com.student.finance.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.student.finance.ui.navigation.Screen
import com.student.finance.ui.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    navController: NavHostController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val currency by viewModel.currency.collectAsState()
    val darkMode by viewModel.darkMode.collectAsState()
    val appLockEnabled by viewModel.appLockEnabled.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Pengaturan",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(20.dp))

        // TAMPILAN
        SectionTitle("Tampilan")
        SettingRow(
            label = "Mode Gelap",
            icon = Icons.Filled.DarkMode,
            trailing = {
                Switch(
                    checked = darkMode,
                    onCheckedChange = { viewModel.setDarkMode(it) }
                )
            }
        )
        HorizontalDivider()
        SettingRow(
            label = "Mata Uang",
            icon = Icons.Filled.AttachMoney,
            trailing = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = currency == "IDR",
                        onClick = { viewModel.setCurrency("IDR") },
                        label = { Text("IDR") }
                    )
                    FilterChip(
                        selected = currency == "USD",
                        onClick = { viewModel.setCurrency("USD") },
                        label = { Text("USD") }
                    )
                }
            }
        )
        HorizontalDivider()

        Spacer(Modifier.height(16.dp))

        // DATA & AKUN (v4.0)
        SectionTitle("Data & Akun")
        SettingRow(
            label = "Kelola Akun",
            icon = Icons.Filled.AccountCircle,
            onClick = { navController.navigate(Screen.Account.route) }
        )
        HorizontalDivider()
        SettingRow(
            label = "Kelola Kategori",
            icon = Icons.Filled.Category,
            onClick = { navController.navigate(Screen.Category.route) }
        )
        HorizontalDivider()
        SettingRow(
            label = "Ekspor Data",
            icon = Icons.Filled.FileDownload,
            onClick = { navController.navigate(Screen.Export.route) }
        )
        HorizontalDivider()

        Spacer(Modifier.height(16.dp))

        // KEAMANAN
        SectionTitle("Keamanan")
        SettingRow(
            label = "Kunci Aplikasi (PIN)",
            icon = Icons.Filled.Lock,
            trailing = {
                Switch(
                    checked = appLockEnabled,
                    onCheckedChange = { enabled ->
                        if (enabled) {
                            viewModel.setAppLockEnabled(true)
                        } else {
                            viewModel.setAppLockEnabled(false)
                            viewModel.setPinCode(null)
                            viewModel.setBiometricEnabled(false)
                        }
                    }
                )
            }
        )
        HorizontalDivider()

        Spacer(Modifier.height(24.dp))

        Text(
            "Semua data disimpan secara lokal di perangkat ini. Tidak ada data yang dikirim ke server.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun SettingRow(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge
        )
        if (trailing != null) {
            trailing()
        } else if (onClick != null) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
