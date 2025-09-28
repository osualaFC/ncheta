package com.fredrickosuala.ncheta.repository

import com.fredrickosuala.ncheta.data.model.NchetaEntry
import com.fredrickosuala.ncheta.domain.subscription.SubscriptionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class NchetaRepositoryImpl(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
    private val authRepository: AuthRepository,
    private val subscriptionManager: SubscriptionManager
) : NchetaRepository {

    val currentUser = authRepository.getCurrentUser()

    override suspend fun insertEntry(entry: NchetaEntry, isPremium: Boolean) {
        localDataSource.insertEntry(entry)
        if (currentUser != null && isPremium) {
            remoteDataSource.saveEntry(currentUser.uid, entry)
        }
    }

    override suspend fun getEntryById(id: String): NchetaEntry? {
        return withContext(Dispatchers.Default) {
           localDataSource.getEntryById(id)
        }
    }

    override fun getAllEntries(): Flow<List<NchetaEntry>> {
        return localDataSource.getAllEntries()
    }

    override suspend fun deleteEntryById(id: String) {
        withContext(Dispatchers.Default) {
            remoteDataSource.deleteEntry(currentUser!!.uid, id)
            localDataSource.deleteEntryById(id)
        }
    }

    override suspend fun syncRemoteEntries(isPremium: Boolean) {
        if (!isPremium || currentUser == null) return

        try {
            val remoteEntries = remoteDataSource.getEntries(currentUser.uid)
            val localEntries = localDataSource.getAllEntries().first()

            val merged = (remoteEntries + localEntries)
                .associateBy { it.id }
                .values
                .toList()

            localDataSource.addAll(merged)

            remoteDataSource.saveEntries(currentUser.uid, merged)

        } catch (e: Exception) {
            println("Error syncing remote entries: ${e.message}")
        }
    }


}