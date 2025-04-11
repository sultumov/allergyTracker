package com.example.allergytracker.domain.usecase.allergy

import com.example.allergytracker.domain.model.Allergy
import com.example.allergytracker.domain.repository.AllergyRepository
import javax.inject.Inject

class UpdateAllergyUseCase @Inject constructor(
    private val repository: AllergyRepository
) {
    suspend operator fun invoke(allergy: Allergy) = repository.updateAllergy(allergy)
} 