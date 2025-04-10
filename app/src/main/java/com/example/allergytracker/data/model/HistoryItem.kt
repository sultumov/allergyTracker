package com.example.allergytracker.data.model

import java.util.Date

data class HistoryItem(
    val id: String,
    val productName: String,
    val scanDate: Date,
    val containsGluten: Boolean,
    val containsLactose: Boolean,
    val containsNuts: Boolean
) 