package com.example.allergytracker.domain.model

import java.util.Date

data class Allergy(
    val id: String,
    val name: String,
    val category: String,
    val severity: String,
    val description: String,
    val createdAt: Date = Date(),
    val isActive: Boolean = true
) {
    companion object {
        const val COLLECTION_NAME = "allergies"
    }
} 