package com.example.allergytracker.data
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "allergy_records")
data class AllergyRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val symptoms: String,
    val triggers: String,
    val medication: String? = null
)