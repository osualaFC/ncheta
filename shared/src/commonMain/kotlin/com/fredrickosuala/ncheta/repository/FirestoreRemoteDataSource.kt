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

    override suspend fun deleteEntry(userId: String, entryId: String) {
        db.collection("users").document(userId)
            .collection("entries").document(entryId).delete()
    }

    override suspend fun saveEntries(userId: String, entries: List<NchetaEntry>) {
        val entriesCollection = db.collection("users").document(userId).collection("entries")
        val batch = db.batch()

        val existingDocs = entriesCollection.get().documents
        existingDocs.forEach { doc ->
            batch.delete(doc.reference)
        }

        entries.forEach { entry ->
            val newDocRef = entriesCollection.document(entry.id)
            batch.set(newDocRef, entry)
        }

        batch.commit()
    }
}