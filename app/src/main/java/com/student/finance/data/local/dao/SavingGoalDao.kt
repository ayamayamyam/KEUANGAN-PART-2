package com.student.finance.data.local.dao

import androidx.room.*
import com.student.finance.data.local.entity.SavingGoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SavingGoalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: SavingGoalEntity): Long

    @Update
    suspend fun update(goal: SavingGoalEntity)

    @Delete
    suspend fun delete(goal: SavingGoalEntity)

    @Query("SELECT * FROM saving_goals WHERE accountId = :accountId ORDER BY deadline ASC")
    fun getAll(accountId: Long): Flow<List<SavingGoalEntity>>

    @Query("SELECT * FROM saving_goals WHERE id = :id")
    suspend fun getById(id: Long): SavingGoalEntity?
}
