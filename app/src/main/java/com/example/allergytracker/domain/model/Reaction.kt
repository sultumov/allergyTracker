package com.example.allergytracker.domain.model

import java.util.Date

data class Reaction(
    val id: String,
    val allergyId: String,
    val date: Date,
    val severity: String,
    val symptoms: List<String>,
    val notes: String,
    val medication: String? = null,
    val duration: Int? = null // in minutes
) {
    companion object {
        const val COLLECTION_NAME = "reactions"
    }
} 