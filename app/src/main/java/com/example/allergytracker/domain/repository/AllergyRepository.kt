package com.example.allergytracker.domain.repository

import com.example.allergytracker.domain.model.Allergy
import kotlinx.coroutines.flow.Flow

interface AllergyRepository {
    fun getAllAllergies(): Flow<List<Allergy>>
    fun getActiveAllergies(): Flow<List<Allergy>>
    fun getAllergyById(id: String): Flow<Allergy?>
    suspend fun addAllergy(allergy: Allergy)
    suspend fun updateAllergy(allergy: Allergy)
    suspend fun deleteAllergy(id: String)
    suspend fun deactivateAllergy(id: String)
    suspend fun activateAllergy(id: String)
} 