package com.student.finance.data.repository

import com.student.finance.data.local.dao.CategoryDao
import com.student.finance.data.local.entity.CategoryEntity
import com.student.finance.data.local.entity.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(private val dao: CategoryDao) {
    fun getAll(accountId: Long): Flow<List<CategoryEntity>> = dao.getAll(accountId)
    fun getByType(accountId: Long, type: TransactionType): Flow<List<CategoryEntity>> = dao.getByType(accountId, type)
    suspend fun getById(id: Long) = dao.getById(id)
    suspend fun insert(category: CategoryEntity) = dao.insert(category)
    suspend fun delete(category: CategoryEntity) = dao.delete(category)

    suspend fun seedDefaultsIfEmpty() {
        val existing = dao.getAll(accountId = 1).firstOrNull()
        if (existing.isNullOrEmpty()) {
            val defaults = listOf(
                CategoryEntity(name = "Makanan", type = TransactionType.EXPENSE, icon = "restaurant"),
                CategoryEntity(name = "Transportasi", type = TransactionType.EXPENSE, icon = "directions_car"),
                CategoryEntity(name = "Belanja", type = TransactionType.EXPENSE, icon = "shopping_cart"),
                CategoryEntity(name = "Hiburan", type = TransactionType.EXPENSE, icon = "movie"),
                CategoryEntity(name = "Kesehatan", type = TransactionType.EXPENSE, icon = "local_hospital"),
                CategoryEntity(name = "Pendidikan", type = TransactionType.EXPENSE, icon = "school"),
                CategoryEntity(name = "Gaji", type = TransactionType.INCOME, icon = "payments"),
                CategoryEntity(name = "Bonus", type = TransactionType.INCOME, icon = "card_giftcard"),
                CategoryEntity(name = "Investasi", type = TransactionType.INCOME, icon = "trending_up"),
                CategoryEntity(name = "Lainnya", type = TransactionType.INCOME, icon = "more_horiz")
            )
            defaults.forEach { dao.insert(it) }
        }
    }
}
