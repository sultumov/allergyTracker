package com.example.allergytracker.domain.usecase.reaction

import com.example.allergytracker.domain.model.Reaction
import com.example.allergytracker.domain.repository.ReactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetReactionsUseCase @Inject constructor(
    private val repository: ReactionRepository
) {
    operator fun invoke(): Flow<List<Reaction>> = repository.getAllReactions()
} 