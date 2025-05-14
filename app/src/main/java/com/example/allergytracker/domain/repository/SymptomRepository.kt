package com.example.allergytracker.domain.repository

import com.example.allergytracker.domain.model.Symptom
import kotlinx.coroutines.flow.Flow

/**
 * Репозиторий для работы с симптомами
 */
interface SymptomRepository {
    /**
     * Получить все симптомы
     */
    fun getAllSymptoms(): Flow<List<Symptom>>
    
    /**
     * Получить симптомы, связанные с конкретной аллергией
     */
    fun getSymptomsByAllergyId(allergyId: Long): Flow<List<Symptom>>
    
    /**
     * Получить недавние симптомы за указанное количество дней
     */
    fun getRecentSymptoms(days: Int): Flow<List<Symptom>>
    
    /**
     * Добавить новый симптом
     * @return ID добавленного симптома
     */
    suspend fun addSymptom(symptom: Symptom): Long
    
    /**
     * Обновить существующий симптом
     */
    suspend fun updateSymptom(symptom: Symptom)
    
    /**
     * Удалить симптом по ID
     */
    suspend fun deleteSymptom(id: Long)
} 