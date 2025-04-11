package com.example.allergytracker.domain.repository

import com.example.allergytracker.domain.model.Reaction
import kotlinx.coroutines.flow.Flow

interface ReactionRepository {
    fun getAllReactions(): Flow<List<Reaction>>
    fun getReactionsByAllergyId(allergyId: String): Flow<List<Reaction>>
    fun getReactionById(id: String): Flow<Reaction?>
    fun getRecentReactions(limit: Int = 10): Flow<List<Reaction>>
    suspend fun addReaction(reaction: Reaction)
    suspend fun updateReaction(reaction: Reaction)
    suspend fun deleteReaction(id: String)
} 