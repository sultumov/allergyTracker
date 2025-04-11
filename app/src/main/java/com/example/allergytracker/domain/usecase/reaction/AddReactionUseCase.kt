package com.example.allergytracker.domain.usecase.reaction

import com.example.allergytracker.domain.model.Reaction
import com.example.allergytracker.domain.repository.ReactionRepository
import javax.inject.Inject

class AddReactionUseCase @Inject constructor(
    private val repository: ReactionRepository
) {
    suspend operator fun invoke(reaction: Reaction) = repository.addReaction(reaction)
} 