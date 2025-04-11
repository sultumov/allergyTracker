package com.example.allergytracker.data.model

data class Recipe(
    val id: String,
    val title: String,
    val description: String,
    val ingredients: List<String>,
    val instructions: List<String>,
    val cookingTime: Int, // in minutes
    val servings: Int,
    val isAllergenFree: Boolean,
    val allergens: List<String> = emptyList()
) 