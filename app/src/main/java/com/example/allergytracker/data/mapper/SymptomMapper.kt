package com.example.allergytracker.data.mapper

import com.example.allergytracker.data.model.Symptom as DataSymptom
import com.example.allergytracker.domain.model.Symptom as DomainSymptom

/**
 * Маппер для конвертации между моделями данных и доменными моделями
 */
object SymptomMapper {
    /**
     * Преобразование из модели данных в доменную модель
     */
    fun mapToDomain(dataSymptom: DataSymptom): DomainSymptom {
        return DomainSymptom(
            id = dataSymptom.id,
            allergyId = dataSymptom.allergyId,
            name = dataSymptom.name,
            severity = dataSymptom.severity,
            notes = dataSymptom.notes,
            timestamp = dataSymptom.timestamp,
            location = dataSymptom.location,
            triggers = dataSymptom.triggers,
            medicationTaken = dataSymptom.medicationTaken,
            isActive = dataSymptom.isActive
        )
    }

    /**
     * Преобразование из доменной модели в модель данных
     */
    fun mapToData(domainSymptom: DomainSymptom): DataSymptom {
        return DataSymptom(
            id = domainSymptom.id,
            allergyId = domainSymptom.allergyId,
            name = domainSymptom.name,
            severity = domainSymptom.severity,
            notes = domainSymptom.notes,
            timestamp = domainSymptom.timestamp,
            location = domainSymptom.location,
            triggers = domainSymptom.triggers,
            medicationTaken = domainSymptom.medicationTaken,
            isActive = domainSymptom.isActive
        )
    }

    /**
     * Преобразование списка моделей данных в список доменных моделей
     */
    fun mapToDomainList(dataSymptoms: List<DataSymptom>): List<DomainSymptom> {
        return dataSymptoms.map { mapToDomain(it) }
    }

    /**
     * Преобразование списка доменных моделей в список моделей данных
     */
    fun mapToDataList(domainSymptoms: List<DomainSymptom>): List<DataSymptom> {
        return domainSymptoms.map { mapToData(it) }
    }
} 