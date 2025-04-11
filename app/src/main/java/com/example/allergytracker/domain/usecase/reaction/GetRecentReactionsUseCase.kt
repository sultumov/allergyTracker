package com.example.allergytracker.domain.usecase.reaction

import com.example.allergytracker.domain.model.Reaction
import com.example.allergytracker.domain.repository.ReactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecentReactionsUseCase @Inject constructor(
    private val repository: ReactionRepository
) {
    operator fun invoke(limit: Int = 10): Flow<List<Reaction>> = 
        repository.getRecentReactions(limit)
} 