package com.example.allergytracker.data.repository

import com.example.allergytracker.data.AllergyDao
import com.example.allergytracker.data.model.AllergyRecord
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AllergyRepository @Inject constructor(
    private val allergyDao: AllergyDao
) {
    fun getAllergyRecords(): Flow<List<AllergyRecord>> = allergyDao.getAllRecords()

    suspend fun insertRecord(record: AllergyRecord) = allergyDao.insertRecord(record)

    suspend fun deleteRecord(record: AllergyRecord) = allergyDao.deleteRecord(record)

    suspend fun deleteRecord(id: Int) = allergyDao.deleteRecord(id)
} 