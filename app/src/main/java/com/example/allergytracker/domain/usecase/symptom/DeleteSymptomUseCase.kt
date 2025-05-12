package com.example.allergytracker.domain.usecase.symptom

import com.example.allergytracker.domain.repository.SymptomRepository
import javax.inject.Inject

class DeleteSymptomUseCase @Inject constructor(
    private val repository: SymptomRepository
) {
    suspend operator fun invoke(id: Long) = repository.deleteSymptom(id)
} 