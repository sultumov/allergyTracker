package com.example.allergytracker.data.model

/**
 * Модель записи о реакции на аллергию
 */
data class AllergyRecord(
    val id: String = "",
    val allergyId: String = "",
    val userId: String = "",
    val date: Long = System.currentTimeMillis(),
    val severity: Int = 0,
    val symptoms: List<String> = emptyList(),
    val notes: String = "",
    val medications: List<String> = emptyList(),
    val location: String? = null,
    val triggers: List<String> = emptyList(),
    val lastModified: Long = System.currentTimeMillis()
) {
    // Пустой конструктор для Firebase
    constructor() : this(
        id = "",
        allergyId = "",
        userId = "",
        date = 0L,
        severity = 0,
        symptoms = emptyList(),
        notes = "",
        medications = emptyList(),
        location = null,
        triggers = emptyList(),
        lastModified = 0L
    )
    
    companion object {
        const val COLLECTION_NAME = "records"
    }
} 