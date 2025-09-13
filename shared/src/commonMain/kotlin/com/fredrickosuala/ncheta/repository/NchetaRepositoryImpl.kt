package com.fredrickosuala.ncheta.repository

import com.fredrickosuala.ncheta.data.model.NchetaEntry
import com.fredrickosuala.ncheta.domain.subscription.SubscriptionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext

class NchetaRepositoryImpl(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
    private val authRepository: AuthRepository,
    private val subscriptionManager: SubscriptionManager
) : NchetaRepository {

    val currentUser = authRepository.getCurrentUser()

    private val isPremium = subscriptionManager.isPremium(currentUser?.uid.orEmpty())
        .stateIn(
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            started = SharingStarted.Eagerly,
            initialValue = false
        )

    override suspend fun insertEntry(entry: NchetaEntry) {
        localDataSource.insertEntry(entry)
        val currentUser = authRepository.getCurrentUser()
        if (currentUser != null && isPremium.value) {
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
            localDataSource.deleteEntryById(id)
        }
    }

    override suspend fun syncRemoteEntries() {
        val currentUser = authRepository.getCurrentUser()
        if (isPremium.value && currentUser != null) {
            try {
                val remoteEntries = remoteDataSource.getEntries(currentUser.uid)
                localDataSource.replaceAll(remoteEntries)
            } catch (e: Exception) {
                println("Error syncing remote entries: ${e.message}")
            }
        }
    }

}