package com.example.allergytracker.domain.usecase.symptom

import com.example.allergytracker.domain.model.Symptom
import com.example.allergytracker.domain.repository.SymptomRepository
import javax.inject.Inject

class UpdateSymptomUseCase @Inject constructor(
    private val repository: SymptomRepository
) {
    suspend operator fun invoke(symptom: Symptom) = repository.updateSymptom(symptom)
} 