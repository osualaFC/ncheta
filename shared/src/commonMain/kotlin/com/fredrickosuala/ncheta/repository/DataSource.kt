package com.fredrickosuala.ncheta.repository

import com.fredrickosuala.ncheta.data.model.NchetaEntry
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    fun getAllEntries(): Flow<List<NchetaEntry>>
    suspend fun insertEntry(entry: NchetaEntry)
    suspend fun getEntryById(id: String): NchetaEntry?
    suspend fun deleteEntryById(id: String)
    suspend fun addAll(entries: List<NchetaEntry>)
}

interface RemoteDataSource {
    suspend fun saveEntry(userId: String, entry: NchetaEntry)
    suspend fun getEntries(userId: String): List<NchetaEntry>
    suspend fun deleteEntry(userId: String, entryId: String)
    suspend fun saveEntries(userId: String, entries: List<NchetaEntry>)
}