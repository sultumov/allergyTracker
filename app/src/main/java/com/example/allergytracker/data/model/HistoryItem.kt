package com.example.allergytracker.data.model

import java.util.Date

/**
 * Модель элемента истории сканирования продуктов
 */
data class HistoryItem(
    val id: String = "",
    val userId: String = "",
    val productBarcode: String = "",
    val productName: String = "",
    val scanDate: Long = System.currentTimeMillis(),
    val foundAllergens: List<String> = emptyList(),
    val notes: String = ""
) {
    // Пустой конструктор для Firebase
    constructor() : this(
        id = "",
        userId = "",
        productBarcode = "",
        productName = "",
        scanDate = 0L,
        foundAllergens = emptyList(),
        notes = ""
    )
    
    companion object {
        const val COLLECTION_NAME = "history"
    }
} 