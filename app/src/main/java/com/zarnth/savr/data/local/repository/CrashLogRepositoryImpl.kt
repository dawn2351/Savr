package com.zarnth.savr.data.local.repository

import com.zarnth.savr.data.local.dao.CrashLogDao
import com.zarnth.savr.data.local.mapper.toDomain
import com.zarnth.savr.domain.model.CrashLog
import com.zarnth.savr.domain.repository.CrashLogRepository
import com.zarnth.savr.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class CrashLogRepositoryImpl(
    private val dao: CrashLogDao
) : CrashLogRepository {

    override fun observeCrashes(): Flow<Resource<List<CrashLog>>> {
        return dao.observeAll()
            .map { list ->
                Resource.Success(list.map { it.toDomain() }) as Resource<List<CrashLog>>
            }
            .onStart { emit(Resource.Loading()) }
            .catch { e -> emit(Resource.Error(e.message ?: "Unknown error")) }
    }

    override suspend fun clearAll() {
        dao.clearAll()
    }
}