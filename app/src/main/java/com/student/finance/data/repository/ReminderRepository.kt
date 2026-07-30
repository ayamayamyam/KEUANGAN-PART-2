package com.student.finance.data.repository

import com.student.finance.data.local.dao.ReminderDao
import com.student.finance.data.local.entity.ReminderEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderRepository @Inject constructor(private val dao: ReminderDao) {
    fun getAll(): Flow<List<ReminderEntity>> = dao.getAll()
    fun getEnabled(): Flow<List<ReminderEntity>> = dao.getEnabled()
    suspend fun insert(reminder: ReminderEntity) = dao.insert(reminder)
    suspend fun update(reminder: ReminderEntity) = dao.update(reminder)
    suspend fun delete(reminder: ReminderEntity) = dao.delete(reminder)
}
