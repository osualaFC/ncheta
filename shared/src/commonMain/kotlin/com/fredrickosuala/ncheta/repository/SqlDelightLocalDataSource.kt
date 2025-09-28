package com.fredrickosuala.ncheta.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.fredrickosuala.ncheta.data.model.NchetaEntry
import com.fredrickosuala.ncheta.database.Database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SqlDelightLocalDataSource(database: Database) : LocalDataSource {

    private val queries = database.queries

    override fun getAllEntries(): Flow<List<NchetaEntry>> {
        return queries.selectAll().asFlow().mapToList(Dispatchers.Default)
            .map { dbEntries -> dbEntries.map { it.toDomain() } }
    }

    override suspend fun insertEntry(entry: NchetaEntry) {
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

    override suspend fun getEntryById(id: String): NchetaEntry? {
        return queries.selectById(id)
            .executeAsOneOrNull()
            ?.toDomain()
    }

    override suspend fun deleteEntryById(id: String) {
        queries.deleteById(id)
    }

    override suspend fun addAll(entries: List<NchetaEntry>) {
        queries.transaction {

            queries.deleteAll()

            entries.forEach { entry ->
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
    }

}