package com.example.allergytracker.domain.usecase.reaction

import com.example.allergytracker.domain.model.Reaction
import com.example.allergytracker.domain.repository.ReactionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetReactionsByAllergyIdUseCase @Inject constructor(
    private val repository: ReactionRepository
) {
    operator fun invoke(allergyId: String): Flow<List<Reaction>> = 
        repository.getReactionsByAllergyId(allergyId)
} 