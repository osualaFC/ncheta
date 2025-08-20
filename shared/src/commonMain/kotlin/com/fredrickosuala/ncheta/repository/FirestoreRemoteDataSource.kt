package com.fredrickosuala.ncheta.repository

import com.fredrickosuala.ncheta.data.model.NchetaEntry
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore

class FirestoreRemoteDataSource : RemoteDataSource {

    private val db = Firebase.firestore

    override suspend fun saveEntry(userId: String, entry: NchetaEntry) {
        db.collection("users").document(userId)
            .collection("entries").document(entry.id)
            .set(entry)
    }

    override suspend fun getEntries(userId: String): List<NchetaEntry> {
        return db.collection("users").document(userId)
            .collection("entries").get().documents
            .map { it.data() }
    }
}