package com.student.finance.data.repository

import com.student.finance.data.local.dao.CategoryDao
import com.student.finance.data.local.entity.CategoryEntity
import com.student.finance.data.local.entity.TransactionType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(private val dao: CategoryDao) {
    fun getAll(): Flow<List<CategoryEntity>> = dao.getAll()
    fun getByType(type: TransactionType): Flow<List<CategoryEntity>> = dao.getByType(type)
    suspend fun getById(id: Long) = dao.getById(id)
    suspend fun insert(category: CategoryEntity) = dao.insert(category)
    suspend fun update(category: CategoryEntity) = dao.update(category)
    suspend fun delete(category: CategoryEntity) = dao.delete(category)

    suspend fun seedDefaultsIfEmpty() {
        if (dao.count() > 0) return
        val defaults = listOf(
            CategoryEntity(name = "Makanan", iconName = "restaurant", colorHex = "#FF7043", type = TransactionType.EXPENSE),
            CategoryEntity(name = "Transportasi", iconName = "directions_bus", colorHex = "#42A5F5", type = TransactionType.EXPENSE),
            CategoryEntity(name = "Jajan", iconName = "fastfood", colorHex = "#AB47BC", type = TransactionType.EXPENSE),
            CategoryEntity(name = "Buku & Alat Tulis", iconName = "menu_book", colorHex = "#8D6E63", type = TransactionType.EXPENSE),
            CategoryEntity(name = "Hiburan", iconName = "movie", colorHex = "#EC407A", type = TransactionType.EXPENSE),
            CategoryEntity(name = "Lainnya", iconName = "category", colorHex = "#78909C", type = TransactionType.EXPENSE),
            CategoryEntity(name = "Uang Saku", iconName = "attach_money", colorHex = "#66BB6A", type = TransactionType.INCOME),
            CategoryEntity(name = "Beasiswa", iconName = "school", colorHex = "#26A69A", type = TransactionType.INCOME),
            CategoryEntity(name = "Hadiah", iconName = "card_giftcard", colorHex = "#FFCA28", type = TransactionType.INCOME),
            CategoryEntity(name = "Freelance", iconName = "work", colorHex = "#5C6BC0", type = TransactionType.INCOME)
        )
        insertAll(defaults)
    }

    private suspend fun insertAll(categories: List<CategoryEntity>) = dao.insertAll(categories)
}
