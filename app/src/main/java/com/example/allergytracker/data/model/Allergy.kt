package com.example.allergytracker.data.model

data class Allergy(
    val id: String = System.currentTimeMillis().toString(),
    val name: String,
    val description: String = "",
    val severity: Severity = Severity.MEDIUM,
    val category: Category = Category.OTHER,
    val lastModified: Long = System.currentTimeMillis()
) {


    // Пустой конструктор для Firebase
    constructor() : this("", "", "", Severity.MEDIUM, Category.OTHER, 0)

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