package com.student.finance.ui.screens.security

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.student.finance.ui.viewmodel.SettingsViewModel

@Composable
fun PinLockScreen(
    onUnlock: () -> Unit,
    onUseBiometric: (() -> Unit)? = null,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    var pin by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    val storedPin by viewModel.pinCode.collectAsState()

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = null,
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(20.dp))
            Text(
                "Aplikasi Terkunci",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Masukkan PIN untuk melanjutkan",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(32.dp))

            OutlinedTextField(
                value = pin,
                onValueChange = {
                    val filtered = it.filter { c -> c.isDigit() }.take(4)
                    pin = filtered
                    error = false
                },
                label = { Text("PIN") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                isError = error,
                modifier = Modifier.fillMaxWidth()
            )
            if (error) {
                Text(
                    "PIN salah",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    if (pin == storedPin) {
                        onUnlock()
                    } else {
                        error = true
                        pin = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Buka Kunci") }

            if (onUseBiometric != null) {
                Spacer(Modifier.height(12.dp))
                TextButton(onClick = onUseBiometric) {
                    Text("Gunakan Biometrik")
                }
            }
        }
    }
}
