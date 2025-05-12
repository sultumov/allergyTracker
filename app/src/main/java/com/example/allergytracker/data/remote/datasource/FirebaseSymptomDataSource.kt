package com.example.allergytracker.data.remote.datasource

import com.example.allergytracker.data.model.Symptom
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import java.util.Date

@Singleton
class FirebaseSymptomDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val symptomsCollection = firestore.collection(Symptom.COLLECTION_NAME)

    fun getAllSymptoms(): Flow<List<Symptom>> = callbackFlow {
        val subscription = symptomsCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Timber.e(error, "Error getting all symptoms")
                    close(error)
                    return@addSnapshotListener
                }

                val symptoms = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject(Symptom::class.java)?.apply {
                            this.id = doc.id.toLongOrNull() ?: 0
                        }
                    } catch (e: Exception) {
                        Timber.e(e, "Error parsing symptom document")
                        null
                    }
                } ?: emptyList()
                trySend(symptoms)
            }
        awaitClose { subscription.remove() }
    }

    fun getSymptomsByAllergyId(allergyId: Long): Flow<List<Symptom>> = callbackFlow {
        val subscription = symptomsCollection
            .whereEqualTo("allergyId", allergyId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Timber.e(error, "Error getting symptoms for allergy: $allergyId")
                    close(error)
                    return@addSnapshotListener
                }

                val symptoms = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject(Symptom::class.java)?.apply {
                            this.id = doc.id.toLongOrNull() ?: 0
                        }
                    } catch (e: Exception) {
                        Timber.e(e, "Error parsing symptom document")
                        null
                    }
                } ?: emptyList()
                trySend(symptoms)
            }
        awaitClose { subscription.remove() }
    }

    fun getRecentSymptoms(days: Int): Flow<List<Symptom>> = callbackFlow {
        val calendar = java.util.Calendar.getInstance()
        calendar.add(java.util.Calendar.DAY_OF_YEAR, -days)
        val dateLimit = calendar.time

        val subscription = symptomsCollection
            .whereGreaterThan("timestamp", dateLimit)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Timber.e(error, "Error getting recent symptoms")
                    close(error)
                    return@addSnapshotListener
                }

                val symptoms = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject(Symptom::class.java)?.apply {
                            this.id = doc.id.toLongOrNull() ?: 0
                        }
                    } catch (e: Exception) {
                        Timber.e(e, "Error parsing symptom document")
                        null
                    }
                } ?: emptyList()
                trySend(symptoms)
            }
        awaitClose { subscription.remove() }
    }

    suspend fun addSymptom(symptom: Symptom): Long {
        try {
            val id = System.currentTimeMillis()
            val symptomWithId = symptom.copy(id = id)
            symptomsCollection.document(id.toString())
                .set(symptomWithId)
                .await()
            return id
        } catch (e: Exception) {
            Timber.e(e, "Error adding symptom: $symptom")
            throw e
        }
    }

    suspend fun updateSymptom(symptom: Symptom) {
        try {
            symptomsCollection.document(symptom.id.toString())
                .set(symptom)
                .await()
        } catch (e: Exception) {
            Timber.e(e, "Error updating symptom: $symptom")
            throw e
        }
    }

    suspend fun deleteSymptom(id: Long) {
        try {
            symptomsCollection.document(id.toString())
                .delete()
                .await()
        } catch (e: Exception) {
            Timber.e(e, "Error deleting symptom: $id")
            throw e
        }
    }
} 