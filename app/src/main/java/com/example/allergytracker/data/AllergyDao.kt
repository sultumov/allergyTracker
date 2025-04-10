package com.example.allergytracker.data

import androidx.room.*
import com.example.allergytracker.data.model.AllergyRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface AllergyDao {
    @Query("SELECT * FROM allergy_records ORDER BY date DESC")
    fun getAllRecords(): Flow<List<AllergyRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: AllergyRecord)

    @Delete
    suspend fun deleteRecord(record: AllergyRecord)

    @Query("DELETE FROM allergy_records WHERE id = :id")
    suspend fun deleteRecord(id: Int)
}