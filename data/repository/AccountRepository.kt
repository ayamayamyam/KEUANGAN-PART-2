package com.student.finance.data.repository

import com.student.finance.data.local.dao.AccountDao
import com.student.finance.data.local.entity.AccountEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepository @Inject constructor(private val dao: AccountDao) {
    fun getAll(): Flow<List<AccountEntity>> = dao.getAll()
    suspend fun getActive(): AccountEntity? = dao.getActive()
    suspend fun getById(id: Long): AccountEntity? = dao.getById(id)
    suspend fun insert(account: AccountEntity) = dao.insert(account)
    suspend fun update(account: AccountEntity) = dao.update(account)
    suspend fun delete(account: AccountEntity) = dao.delete(account)

    suspend fun switchAccount(accountId: Long) {
        dao.clearActive()
        dao.setActive(accountId)
    }
}
