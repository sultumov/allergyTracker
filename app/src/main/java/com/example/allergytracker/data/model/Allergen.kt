package com.example.allergytracker.data.model

data class Allergen(
    val id: String,
    val name: String,
    val description: String,
    val commonSources: List<String>,
    val symptoms: List<String>,
    val severityLevels: List<String>,
    val treatmentOptions: List<String>,
    val preventionTips: List<String>
) 