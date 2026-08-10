package com.zarnth.savr.domain.repository

import com.zarnth.savr.domain.model.CrashLog
import com.zarnth.savr.utils.Resource
import kotlinx.coroutines.flow.Flow

interface CrashLogRepository {
    fun observeCrashes(): Flow<Resource<List<CrashLog>>>
    suspend fun clearAll()
}