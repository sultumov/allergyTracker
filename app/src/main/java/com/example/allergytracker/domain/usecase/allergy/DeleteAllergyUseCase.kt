package com.example.allergytracker.domain.usecase.allergy

import com.example.allergytracker.domain.repository.AllergyRepository
import javax.inject.Inject

class DeleteAllergyUseCase @Inject constructor(
    private val repository: AllergyRepository
) {
    suspend operator fun invoke(id: String) = repository.deleteAllergy(id)
} 