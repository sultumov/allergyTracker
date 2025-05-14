package com.example.allergytracker.domain.model

import java.util.Date

/**
 * Доменная модель симптома
 */
data class Symptom(
    val id: Long,
    val allergyId: Long,
    val name: String,
    val severity: Int,
    val notes: String,
    val timestamp: Date,
    val location: String,
    val triggers: List<String>,
    val medicationTaken: String,
    val isActive: Boolean
) 