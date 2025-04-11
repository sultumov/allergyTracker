package com.example.allergytracker.domain.usecase.reaction

import com.example.allergytracker.domain.repository.ReactionRepository
import javax.inject.Inject

class DeleteReactionUseCase @Inject constructor(
    private val repository: ReactionRepository
) {
    suspend operator fun invoke(id: String) = repository.deleteReaction(id)
} 