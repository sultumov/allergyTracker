package com.example.allergytracker.data.model

import com.google.firebase.firestore.Exclude
import java.util.Date

data class Reaction(
    @get:Exclude
    var id: String = "",
    val allergyId: String = "",
    val date: Date = Date(),
    val severity: String = "",
    val symptoms: List<String> = emptyList(),
    val notes: String = "",
    val medication: String? = null,
    val duration: Int? = null // in minutes
) {
    companion object {
        const val COLLECTION_NAME = "reactions"
        
        enum class Severity {
            MILD, MODERATE, SEVERE;

            override fun toString(): String {
                return when (this) {
                    MILD -> "Легкая"
                    MODERATE -> "Умеренная"
                    SEVERE -> "Тяжелая"
                }
            }
        }
    }

    // Пустой конструктор для Firestore
    constructor() : this("", "", Date(), "", emptyList(), "", null, null)
} 