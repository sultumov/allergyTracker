package com.example.allergytracker.data.model

/**
 * Модель продукта с данными об аллергенах
 */
data class Product(
    val barcode: String = "",
    val name: String = "",
    val manufacturer: String = "",
    val allergens: List<String> = emptyList(),
    val ingredients: List<String> = emptyList(),
    val imageUrl: String? = null,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    // Пустой конструктор для Firebase
    constructor() : this(
        barcode = "",
        name = "",
        manufacturer = "",
        allergens = emptyList(),
        ingredients = emptyList(),
        imageUrl = null,
        lastUpdated = 0L
    )
    
    companion object {
        const val COLLECTION_NAME = "products"
    }
} 