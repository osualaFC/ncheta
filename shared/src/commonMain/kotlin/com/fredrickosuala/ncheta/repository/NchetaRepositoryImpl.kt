package com.fredrickosuala.ncheta.repository


import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.fredrickosuala.ncheta.data.model.NchetaEntry
import com.fredrickosuala.ncheta.database.Database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class NchetaRepositoryImpl(
    database: Database
) : NchetaRepository {

    private val queries = database.queries

    override suspend fun insertEntry(entry: NchetaEntry) {
        withContext(Dispatchers.Default) {
            queries.insert(
                id = entry.id,
                title = entry.title,
                sourceText = entry.sourceText,
                createdAt = entry.createdAt,
                lastPracticedAt = entry.lastPracticedAt,
                inputSourceType = entry.inputSourceType,
                content = entry.content
            )
        }
    }

    override suspend fun getEntryById(id: String): NchetaEntry? {
        return withContext(Dispatchers.Default) {
            queries.selectById(id)
                .executeAsOneOrNull()
                ?.toDomain()
        }
    }

    override fun getAllEntries(): Flow<List<NchetaEntry>> {
        return queries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { dbEntries ->
                dbEntries.map { it.toDomain() }
            }
    }

    override suspend fun deleteEntryById(id: String) {
        withContext(Dispatchers.Default) {
            queries.deleteById(id)
        }
    }
}