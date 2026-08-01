package com.student.finance.data.repository

import com.student.finance.data.local.dao.CategoryDao
import com.student.finance.data.local.entity.CategoryEntity
import com.student.finance.data.local.entity.TransactionType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(private val dao: CategoryDao) {
    fun getAll(accountId: Long): Flow<List<CategoryEntity>> = dao.getAll(accountId)
    fun getByType(accountId: Long, type: TransactionType): Flow<List<CategoryEntity>> = dao.getByType(accountId, type)
    suspend fun getById(id: Long) = dao.getById(id)
    suspend fun insert(category: CategoryEntity) = dao.insert(category)
    suspend fun delete(category: CategoryEntity) = dao.delete(category)
}
