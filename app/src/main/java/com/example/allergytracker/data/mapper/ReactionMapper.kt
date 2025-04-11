package com.example.allergytracker.data.mapper

import com.example.allergytracker.data.model.Reaction as DataReaction
import com.example.allergytracker.domain.model.Reaction as DomainReaction

object ReactionMapper {
    fun toDomain(data: DataReaction): DomainReaction = DomainReaction(
        id = data.id,
        allergyId = data.allergyId,
        date = data.date,
        severity = data.severity,
        symptoms = data.symptoms,
        notes = data.notes,
        medication = data.medication,
        duration = data.duration
    )

    fun toData(domain: DomainReaction): DataReaction = DataReaction(
        id = domain.id,
        allergyId = domain.allergyId,
        date = domain.date,
        severity = domain.severity,
        symptoms = domain.symptoms,
        notes = domain.notes,
        medication = domain.medication,
        duration = domain.duration
    )
} 