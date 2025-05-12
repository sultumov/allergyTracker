package com.example.allergytracker.domain.usecase.symptom

import com.example.allergytracker.domain.model.Symptom
import com.example.allergytracker.domain.repository.SymptomRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecentSymptomsUseCase @Inject constructor(
    private val repository: SymptomRepository
) {
    operator fun invoke(days: Int = 7): Flow<List<Symptom>> = 
        repository.getRecentSymptoms(days)
} 