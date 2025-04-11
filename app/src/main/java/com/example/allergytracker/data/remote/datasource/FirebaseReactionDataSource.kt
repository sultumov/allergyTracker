package com.example.allergytracker.data.remote.datasource

import com.example.allergytracker.data.model.Reaction
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
class FirebaseReactionDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val reactionsCollection = firestore.collection(Reaction.COLLECTION_NAME)

    fun getReactionById(id: String): Flow<Reaction?> = callbackFlow {
        val subscription = reactionsCollection
            .document(id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Timber.e(error, "Error getting reaction by id: $id")
                    close(error)
                    return@addSnapshotListener
                }

                val reaction = try {
                    snapshot?.toObject(Reaction::class.java)?.apply {
                        this.id = snapshot.id
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Error parsing reaction document")
                    null
                }
                trySend(reaction)
            }
        awaitClose { subscription.remove() }
    }

    suspend fun deleteReaction(id: String) {
        try {
            reactionsCollection.document(id)
                .delete()
                .await()
        } catch (e: Exception) {
            Timber.e(e, "Error deleting reaction: $id")
            throw e
        }
    }

    suspend fun insertReaction(reaction: Reaction) {
        try {
            val document = reactionsCollection.document()
            val reactionWithId = reaction.copy(id = document.id)
            document.set(reactionWithId).await()
        } catch (e: Exception) {
            Timber.e(e, "Error inserting reaction: $reaction")
            throw e
        }
    }

    fun getAllReactions(): Flow<List<Reaction>> = callbackFlow {
        val subscription = reactionsCollection
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Timber.e(error, "Error getting all reactions")
                    close(error)
                    return@addSnapshotListener
                }

                val reactions = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject(Reaction::class.java)?.apply {
                            this.id = doc.id
                        }
                    } catch (e: Exception) {
                        Timber.e(e, "Error parsing reaction document")
                        null
                    }
                } ?: emptyList()
                trySend(reactions)
            }
        awaitClose { subscription.remove() }
    }

    fun getReactionsByAllergyId(allergyId: String): Flow<List<Reaction>> = callbackFlow {
        val subscription = reactionsCollection
            .whereEqualTo("allergyId", allergyId)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Timber.e(error, "Error getting reactions for allergy: $allergyId")
                    close(error)
                    return@addSnapshotListener
                }

                val reactions = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject(Reaction::class.java)?.apply {
                            this.id = doc.id
                        }
                    } catch (e: Exception) {
                        Timber.e(e, "Error parsing reaction document")
                        null
                    }
                } ?: emptyList()
                trySend(reactions)
            }
        awaitClose { subscription.remove() }
    }

    fun getRecentReactions(limit: Int = 10): Flow<List<Reaction>> = callbackFlow {
        val subscription = reactionsCollection
            .orderBy("date", Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Timber.e(error, "Error getting recent reactions")
                    close(error)
                    return@addSnapshotListener
                }

                val reactions = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject(Reaction::class.java)?.apply {
                            this.id = doc.id
                        }
                    } catch (e: Exception) {
                        Timber.e(e, "Error parsing reaction document")
                        null
                    }
                } ?: emptyList()
                trySend(reactions)
            }
        awaitClose { subscription.remove() }
    }
} 