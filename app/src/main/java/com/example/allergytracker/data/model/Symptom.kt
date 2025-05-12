package com.example.allergytracker.data.model

import com.google.firebase.firestore.Exclude
import java.util.Date

/**
 * Модель для отслеживания симптомов
 */
data class Symptom(
    var id: Long = 0,
    val allergyId: Long = 0, // ID связанной аллергии (может быть 0, если симптом не связан с конкретной аллергией)
    val name: String = "", // Название симптома (зуд, кашель, чихание и т.д.)
    val severity: Int = 0, // Тяжесть от 1 до 10
    val notes: String = "", // Дополнительные заметки
    val timestamp: Date = Date(), // Время появления симптома
    val location: String = "", // Место, где проявился симптом
    val triggers: List<String> = emptyList(), // Возможные триггеры
    val medicationTaken: String = "", // Принятые лекарства
    val isActive: Boolean = true // Активен ли симптом в настоящее время
) {
    companion object {
        const val COLLECTION_NAME = "symptoms"
    }

    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "allergyId" to allergyId,
            "name" to name,
            "severity" to severity,
            "notes" to notes,
            "timestamp" to timestamp,
            "location" to location,
            "triggers" to triggers,
            "medicationTaken" to medicationTaken,
            "isActive" to isActive
        )
    }
} 