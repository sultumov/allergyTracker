package com.example.allergytracker.data.repository

import com.example.allergytracker.data.mapper.AllergyMapper
import com.example.allergytracker.data.remote.datasource.FirebaseAllergyDataSource
import com.example.allergytracker.domain.model.Allergy
import com.example.allergytracker.domain.repository.AllergyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AllergyRepositoryImpl @Inject constructor(
    private val dataSource: FirebaseAllergyDataSource,
    private val mapper: AllergyMapper
) : AllergyRepository {

    override fun getAllAllergies(): Flow<List<Allergy>> =
        dataSource.getAllAllergies().map { allergies ->
            allergies.map { mapper.toDomain(it) }
        }

    override fun getActiveAllergies(): Flow<List<Allergy>> =
        dataSource.getActiveAllergies().map { allergies ->
            allergies.map { mapper.toDomain(it) }
        }

    override fun getAllergyById(id: String): Flow<Allergy?> =
        dataSource.getAllergyById(id).map { allergy ->
            allergy?.let { mapper.toDomain(it) }
        }

    override suspend fun addAllergy(allergy: Allergy) {
        dataSource.addAllergy(mapper.toData(allergy))
    }

    override suspend fun updateAllergy(allergy: Allergy) {
        dataSource.updateAllergy(mapper.toData(allergy))
    }

    override suspend fun deleteAllergy(id: String) {
        dataSource.deleteAllergy(id)
    }

    override suspend fun deactivateAllergy(id: String) {
        dataSource.deactivateAllergy(id)
    }

    override suspend fun activateAllergy(id: String) {
        dataSource.activateAllergy(id)
    }
} 