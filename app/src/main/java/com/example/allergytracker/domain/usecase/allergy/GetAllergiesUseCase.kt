package com.example.allergytracker.domain.usecase.allergy

import com.example.allergytracker.domain.model.Allergy
import com.example.allergytracker.domain.repository.AllergyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllergiesUseCase @Inject constructor(
    private val repository: AllergyRepository
) {
    operator fun invoke(): Flow<List<Allergy>> = repository.getAllAllergies()
} 