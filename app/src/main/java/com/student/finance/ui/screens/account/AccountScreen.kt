package com.student.finance.ui.screens.account

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.student.finance.data.local.entity.AccountEntity
import com.student.finance.data.repository.AccountRepository
import com.student.finance.ui.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    settingsViewModel: SettingsViewModel = hiltViewModel(),
    accountRepository: AccountRepository = hiltViewModel() // TIDAK BISA, perlu ViewModel sendiri
) {
    // Karena tidak bisa inject Repository langsung di Screen, kita buat AccountViewModel
    AccountScreenContent()
}

// NOTE: Buat juga AccountViewModel.kt di bawah
