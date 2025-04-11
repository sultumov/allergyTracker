package com.example.allergytracker.data.remote.datasource

import com.example.allergytracker.data.model.Allergy
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAllergyDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val allergiesCollection = firestore.collection(Allergy.COLLECTION_NAME)

    fun getAllAllergies(): Flow<List<Allergy>> = callbackFlow {
        val subscription = allergiesCollection
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Timber.e(error, "Error getting all allergies")
                    close(error)
                    return@addSnapshotListener
                }

                val allergies = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject(Allergy::class.java)?.apply {
                            this.id = doc.id
                        }
                    } catch (e: Exception) {
                        Timber.e(e, "Error parsing allergy document")
                        null
                    }
                } ?: emptyList()
                trySend(allergies)
            }
        awaitClose { subscription.remove() }
    }

    fun getActiveAllergies(): Flow<List<Allergy>> = callbackFlow {
        val subscription = allergiesCollection
            .whereEqualTo("isActive", true)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Timber.e(error, "Error getting active allergies")
                    close(error)
                    return@addSnapshotListener
                }

                val allergies = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject(Allergy::class.java)?.apply {
                            this.id = doc.id
                        }
                    } catch (e: Exception) {
                        Timber.e(e, "Error parsing allergy document")
                        null
                    }
                } ?: emptyList()
                trySend(allergies)
            }
        awaitClose { subscription.remove() }
    }

    fun getAllergyById(id: String): Flow<Allergy?> = callbackFlow {
        val subscription = allergiesCollection
            .document(id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Timber.e(error, "Error getting allergy by id: $id")
                    close(error)
                    return@addSnapshotListener
                }

                val allergy = try {
                    snapshot?.toObject(Allergy::class.java)?.apply {
                        this.id = snapshot.id
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Error parsing allergy document")
                    null
                }
                trySend(allergy)
            }
        awaitClose { subscription.remove() }
    }

    suspend fun addAllergy(allergy: Allergy) {
        try {
            val document = allergiesCollection.document()
            val allergyWithId = allergy.copy(id = document.id)
            document.set(allergyWithId).await()
        } catch (e: Exception) {
            Timber.e(e, "Error adding allergy: $allergy")
            throw e
        }
    }

    suspend fun updateAllergy(allergy: Allergy) {
        try {
            allergiesCollection.document(allergy.id)
                .set(allergy)
                .await()
        } catch (e: Exception) {
            Timber.e(e, "Error updating allergy: $allergy")
            throw e
        }
    }

    suspend fun deleteAllergy(id: String) {
        try {
            allergiesCollection.document(id)
                .delete()
                .await()
        } catch (e: Exception) {
            Timber.e(e, "Error deleting allergy: $id")
            throw e
        }
    }

    suspend fun deactivateAllergy(id: String) {
        try {
            allergiesCollection.document(id)
                .update("isActive", false)
                .await()
        } catch (e: Exception) {
            Timber.e(e, "Error deactivating allergy: $id")
            throw e
        }
    }

    suspend fun activateAllergy(id: String) {
        try {
            allergiesCollection.document(id)
                .update("isActive", true)
                .await()
        } catch (e: Exception) {
            Timber.e(e, "Error activating allergy: $id")
            throw e
        }
    }
} 