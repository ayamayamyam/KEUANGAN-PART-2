package com.student.finance.data.local.dao

import androidx.room.*
import com.student.finance.data.local.entity.CategoryEntity
import com.student.finance.data.local.entity.TransactionType
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: CategoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(categories: List<CategoryEntity>)

    @Update
    suspend fun update(category: CategoryEntity)

    @Delete
    suspend fun delete(category: CategoryEntity)

    @Query("SELECT * FROM categories WHERE accountId = :accountId ORDER BY name ASC")
    fun getAll(accountId: Long): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE accountId = :accountId AND type = :type ORDER BY name ASC")
    fun getByType(accountId: Long, type: TransactionType): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getById(id: Long): CategoryEntity?

    @Query("SELECT COUNT(*) FROM categories WHERE accountId = :accountId")
    suspend fun count(accountId: Long): Int
}
