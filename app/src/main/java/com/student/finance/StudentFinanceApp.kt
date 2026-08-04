package com.student.finance

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.student.finance.data.local.DataStoreManager
import com.student.finance.data.local.entity.AccountEntity
import com.student.finance.data.repository.AccountRepository
import com.student.finance.data.repository.CategoryRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class StudentFinanceApp : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var categoryRepository: CategoryRepository
    @Inject lateinit var accountRepository: AccountRepository
    @Inject lateinit var dataStoreManager: DataStoreManager

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            val currentId = dataStoreManager.currentAccountId.first()
            if (currentId == 0L) {
                val accounts = accountRepository.getAll().first()
                val accountId = if (accounts.isEmpty()) {
                    accountRepository.insert(AccountEntity(name = "Akun Utama"))
                } else {
                    accounts.first().id
                }
                dataStoreManager.setCurrentAccountId(accountId)
                categoryRepository.seedDefaultsIfEmpty(accountId)
            } else {
                categoryRepository.seedDefaultsIfEmpty(currentId)
            }
        }
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
