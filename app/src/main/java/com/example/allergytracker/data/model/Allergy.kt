package com.example.allergytracker.data.model

import com.google.firebase.firestore.Exclude
import java.util.Date

data class Allergy(
    @get:Exclude
    var id: String = "",
    val name: String = "",
    val category: String = "",
    val severity: String = "",
    val description: String = "",
    val createdAt: Date = Date(),
    val isActive: Boolean = true
) {
    companion object {
        const val COLLECTION_NAME = "allergies"
        
        enum class Severity {
            LOW, MEDIUM, HIGH;

            override fun toString(): String {
                return when (this) {
                    LOW -> "Низкая"
                    MEDIUM -> "Средняя"
                    HIGH -> "Высокая"
                }
            }
        }

        enum class Category {
            FOOD, MEDICATION, ENVIRONMENTAL, OTHER;

            override fun toString(): String {
                return when (this) {
                    FOOD -> "Пищевая"
                    MEDICATION -> "Лекарственная"
                    ENVIRONMENTAL -> "Экологическая"
                    OTHER -> "Другая"
                }
            }
        }
    }

    // Пустой конструктор для Firestore
    constructor() : this("", "", "", "", "", Date(), true)
} 