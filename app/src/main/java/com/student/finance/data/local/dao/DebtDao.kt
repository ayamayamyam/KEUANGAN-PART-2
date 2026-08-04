package com.student.finance.data.local.dao

import androidx.room.*
import com.student.finance.data.local.entity.DebtEntity
import com.student.finance.data.local.entity.DebtStatus
import com.student.finance.data.local.entity.DebtType
import kotlinx.coroutines.flow.Flow

@Dao
interface DebtDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(debt: DebtEntity): Long

    @Update
    suspend fun update(debt: DebtEntity)

    @Delete
    suspend fun delete(debt: DebtEntity)

    @Query("SELECT * FROM debts WHERE accountId = :accountId ORDER BY date DESC")
    fun getAll(accountId: Long): Flow<List<DebtEntity>>

    @Query("SELECT * FROM debts WHERE accountId = :accountId AND type = :type ORDER BY date DESC")
    fun getByType(accountId: Long, type: DebtType): Flow<List<DebtEntity>>

    @Query("SELECT * FROM debts WHERE accountId = :accountId AND status = :status ORDER BY date DESC")
    fun getByStatus(accountId: Long, status: DebtStatus): Flow<List<DebtEntity>>

    @Query("SELECT COALESCE(SUM(amount),0) FROM debts WHERE accountId = :accountId AND type = :type AND isPaid = 0")
    fun getTotalUnpaidByType(accountId: Long, type: DebtType): Flow<Double>

    @Query("SELECT * FROM debts WHERE id = :id")
    suspend fun getById(id: Long): DebtEntity?
}
