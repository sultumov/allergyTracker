package com.example.allergytracker.data.model

data class Product(
    val barcode: String,
    val name: String,
    val containsGluten: Boolean,
    val containsLactose: Boolean,
    val containsNuts: Boolean,
    val lastModified: Long = System.currentTimeMillis()
) {
    // Пустой конструктор для Firebase
    constructor() : this("", "", false, false, false, 0)
} 