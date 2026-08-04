package com.student.finance.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.student.finance.data.local.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : ViewModel() {

    val currency: StateFlow<String> = dataStoreManager.currency
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "IDR")

    val darkMode: StateFlow<Boolean> = dataStoreManager.darkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val pinCode: StateFlow<String?> = dataStoreManager.pinCode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val biometricEnabled: StateFlow<Boolean> = dataStoreManager.biometricEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val appLockEnabled: StateFlow<Boolean> = dataStoreManager.appLockEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val userName: StateFlow<String> = dataStoreManager.userName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Pengguna")

    fun setCurrency(value: String) = viewModelScope.launch { dataStoreManager.setCurrency(value) }
    fun setDarkMode(value: Boolean) = viewModelScope.launch { dataStoreManager.setDarkMode(value) }
    fun setPinCode(value: String?) = viewModelScope.launch { dataStoreManager.setPinCode(value) }
    fun setBiometricEnabled(value: Boolean) = viewModelScope.launch { dataStoreManager.setBiometricEnabled(value) }
    fun setAppLockEnabled(value: Boolean) = viewModelScope.launch { dataStoreManager.setAppLockEnabled(value) }
    fun setUserName(value: String) = viewModelScope.launch { dataStoreManager.setUserName(value) }
}
