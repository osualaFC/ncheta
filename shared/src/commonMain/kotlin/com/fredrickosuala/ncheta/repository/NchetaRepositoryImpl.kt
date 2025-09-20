package com.fredrickosuala.ncheta.repository

import com.fredrickosuala.ncheta.data.model.NchetaEntry
import com.fredrickosuala.ncheta.domain.subscription.SubscriptionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class NchetaRepositoryImpl(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
    private val authRepository: AuthRepository,
    private val subscriptionManager: SubscriptionManager
) : NchetaRepository {

    override suspend fun insertEntry(entry: NchetaEntry, isPremium: Boolean) {
        localDataSource.insertEntry(entry)
        val currentUser = authRepository.getCurrentUser()
        if (currentUser != null && isPremium) {
            remoteDataSource.saveEntry(currentUser.email.orEmpty(), entry)
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
            localDataSource.deleteEntryById(id)
        }
    }

    override suspend fun syncRemoteEntries(isPremium: Boolean) {
        val currentUser = authRepository.getCurrentUser()
        if (isPremium && currentUser != null) {
            try {
                val remoteEntries = remoteDataSource.getEntries(currentUser.email.orEmpty())
                localDataSource.replaceAll(remoteEntries)
            } catch (e: Exception) {
                println("Error syncing remote entries: ${e.message}")
            }
        }
    }

}