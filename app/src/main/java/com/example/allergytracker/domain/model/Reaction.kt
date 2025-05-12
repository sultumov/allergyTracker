package com.example.allergytracker.domain.model

import java.util.Date

/**
 * Доменная модель реакции на аллерген
 */
data class Reaction(
    val id: String = "",
    val allergyId: String = "",
    val date: Date = Date(),
    val severity: String = "",
    val symptoms: List<String> = emptyList(),
    val notes: String = "",
    val medication: String? = null,
    val duration: Int? = null,
    val location: String? = null,
    val triggers: List<String> = emptyList()
) {
    companion object {
        const val COLLECTION_NAME = "reactions"
    }
} 