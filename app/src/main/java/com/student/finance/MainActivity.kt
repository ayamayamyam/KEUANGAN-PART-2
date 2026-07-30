package com.student.finance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.student.finance.ui.navigation.StudentFinanceNavHost
import com.student.finance.ui.theme.StudentFinanceTheme
import com.student.finance.ui.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudentFinanceRoot()
        }
    }
}

@Composable
fun StudentFinanceRoot() {
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val darkMode by settingsViewModel.darkMode.collectAsState()

    StudentFinanceTheme(darkTheme = darkMode) {
        Surface(modifier = Modifier.fillMaxSize()) {
            StudentFinanceNavHost()
        }
    }
}
