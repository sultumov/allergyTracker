package com.example.allergytracker.data.mapper

import com.example.allergytracker.data.model.Allergy as DataAllergy
import com.example.allergytracker.domain.model.Allergy as DomainAllergy

object AllergyMapper {
    fun toDomain(data: DataAllergy): DomainAllergy = DomainAllergy(
        id = data.id,
        name = data.name,
        category = data.category,
        severity = data.severity,
        description = data.description,
        createdAt = data.createdAt,
        isActive = data.isActive
    )

    fun toData(domain: DomainAllergy): DataAllergy = DataAllergy(
        id = domain.id,
        name = domain.name,
        category = domain.category,
        severity = domain.severity,
        description = domain.description,
        createdAt = domain.createdAt,
        isActive = domain.isActive
    )
} 