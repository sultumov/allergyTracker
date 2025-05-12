package com.example.allergytracker.data.repository

import com.example.allergytracker.data.mapper.SymptomMapper
import com.example.allergytracker.data.remote.datasource.FirebaseSymptomDataSource
import com.example.allergytracker.domain.model.Symptom
import com.example.allergytracker.domain.repository.SymptomRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

@Singleton
class SymptomRepositoryImpl @Inject constructor(
    private val dataSource: FirebaseSymptomDataSource
) : SymptomRepository {

    override fun getAllSymptoms(): Flow<List<Symptom>> {
        return dataSource.getAllSymptoms()
            .map { SymptomMapper.mapToDomainList(it) }
    }

    override fun getSymptomsByAllergyId(allergyId: Long): Flow<List<Symptom>> {
        return dataSource.getSymptomsByAllergyId(allergyId)
            .map { SymptomMapper.mapToDomainList(it) }
    }

    override fun getRecentSymptoms(days: Int): Flow<List<Symptom>> {
        return dataSource.getRecentSymptoms(days)
            .map { SymptomMapper.mapToDomainList(it) }
    }

    override suspend fun addSymptom(symptom: Symptom): Long {
        try {
            val dataSymptom = SymptomMapper.mapToData(symptom)
            return dataSource.addSymptom(dataSymptom)
        } catch (e: Exception) {
            Timber.e(e, "Error adding symptom")
            throw e
        }
    }

    override suspend fun updateSymptom(symptom: Symptom) {
        try {
            val dataSymptom = SymptomMapper.mapToData(symptom)
            dataSource.updateSymptom(dataSymptom)
        } catch (e: Exception) {
            Timber.e(e, "Error updating symptom")
            throw e
        }
    }

    override suspend fun deleteSymptom(id: Long) {
        try {
            dataSource.deleteSymptom(id)
        } catch (e: Exception) {
            Timber.e(e, "Error deleting symptom with id: $id")
            throw e
        }
    }
} 