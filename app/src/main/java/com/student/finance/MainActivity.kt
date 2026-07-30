package com.student.finance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import com.student.finance.ui.navigation.StudentFinanceNavHost
import com.student.finance.ui.screens.security.PinLockScreen
import com.student.finance.ui.screens.splash.SplashScreen
import com.student.finance.ui.theme.StudentFinanceTheme
import com.student.finance.ui.viewmodel.SettingsViewModel
import com.student.finance.util.BiometricHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            val appLockEnabled by settingsViewModel.appLockEnabled.collectAsState()
            val biometricEnabled by settingsViewModel.biometricEnabled.collectAsState()

            var showSplash by remember { mutableStateOf(true) }
            var isUnlocked by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                delay(1500)
                showSplash = false
            }

            StudentFinanceTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    when {
                        showSplash -> SplashScreen()
                        !appLockEnabled -> StudentFinanceRoot()
                        isUnlocked -> StudentFinanceRoot()
                        else -> PinLockScreen(
                            onUnlock = { isUnlocked = true },
                            onUseBiometric = if (biometricEnabled && BiometricHelper.canAuthenticate(this)) {
                                {
                                    BiometricHelper.showBiometricPrompt(
                                        activity = this,
                                        onSuccess = { isUnlocked = true },
                                        onError = { }
                                    )
                                }
                            } else null
                        )
                    }
                }
            }
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
