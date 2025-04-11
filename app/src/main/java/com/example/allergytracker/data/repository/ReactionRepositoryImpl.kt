package com.example.allergytracker.data.repository

import com.example.allergytracker.data.mapper.ReactionMapper
import com.example.allergytracker.data.remote.datasource.FirebaseReactionDataSource
import com.example.allergytracker.domain.model.Reaction
import com.example.allergytracker.domain.repository.ReactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReactionRepositoryImpl @Inject constructor(
    private val dataSource: FirebaseReactionDataSource,
    private val mapper: ReactionMapper
) : ReactionRepository {

    override fun getAllReactions(): Flow<List<Reaction>> =
        dataSource.getAllReactions().map { reactions ->
            reactions.map { mapper.toDomain(it) }
        }

    override fun getReactionsByAllergyId(allergyId: String): Flow<List<Reaction>> =
        dataSource.getReactionsByAllergyId(allergyId).map { reactions ->
            reactions.map { mapper.toDomain(it) }
        }

    override fun getReactionById(id: String): Flow<Reaction?> =
        dataSource.getReactionById(id).map { reaction ->
            reaction?.let { mapper.toDomain(it) }
        }

    override fun getRecentReactions(limit: Int): Flow<List<Reaction>> =
        dataSource.getRecentReactions(limit).map { reactions ->
            reactions.map { mapper.toDomain(it) }
        }

    override suspend fun addReaction(reaction: Reaction) {
        dataSource.insertReaction(mapper.toData(reaction))
    }

    override suspend fun updateReaction(reaction: Reaction) {
        // TODO: Implement update
    }

    override suspend fun deleteReaction(id: String) {
        dataSource.deleteReaction(id)
    }
} 