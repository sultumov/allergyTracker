package com.example.allergytracker.data.repository

import com.example.allergytracker.data.model.Allergy
import com.example.allergytracker.data.remote.FirebaseDataSource
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AllergyRepository @Inject constructor(
    private val firebaseDataSource: FirebaseDataSource
) {
    fun getAllergies(): Flow<List<Allergy>> = firebaseDataSource.getAllergies()

    fun getAllergyById(id: String): Flow<Allergy?> = firebaseDataSource.getAllergyById(id)
    
    suspend fun addAllergy(allergy: Allergy): DocumentReference = firebaseDataSource.addAllergy(allergy)
    
    suspend fun updateAllergy(allergy: Allergy): Unit = firebaseDataSource.updateAllergy(allergy)
    
    suspend fun deleteAllergy(id: String): Unit = firebaseDataSource.deleteAllergy(id)
} 