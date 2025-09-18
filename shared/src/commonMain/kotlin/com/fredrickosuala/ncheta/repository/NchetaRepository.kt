package com.fredrickosuala.ncheta.repository

import com.fredrickosuala.ncheta.data.model.NchetaEntry
import kotlinx.coroutines.flow.Flow

interface NchetaRepository {
    /**
     * Inserts a new NchetaEntry into the database.
     */
    suspend fun insertEntry(entry: NchetaEntry, isPremium: Boolean)

    /**
     * Retrieves a single NchetaEntry by its unique ID.
     * @return The NchetaEntry if found, otherwise null.
     */
    suspend fun getEntryById(id: String): NchetaEntry?

    /**
     * Gets all saved NchetaEntry objects as a Flow.
     * The Flow will automatically emit a new list whenever the data changes.
     */
    fun getAllEntries(): Flow<List<NchetaEntry>>

    /**
     * Deletes a specific NchetaEntry by its unique ID.
     */
    suspend fun deleteEntryById(id: String)

    /**
     * Synchronizes local entries with the remote data source.
     */
    suspend fun syncRemoteEntries(isPremium: Boolean)

}