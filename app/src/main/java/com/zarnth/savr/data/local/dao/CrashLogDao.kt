package com.zarnth.savr.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.zarnth.savr.data.local.entity.CrashLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CrashLogDao {

    @Query("SELECT * FROM crash_logs ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<CrashLogEntity>>

    @Insert
    fun insert(entity: CrashLogEntity)

    @Query("DELETE FROM crash_logs")
    suspend fun clearAll()
}