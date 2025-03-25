package com.example.allergytracker.data
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

import com.example.allergytracker.data.AllergyRecord;

import java.util.List;

@Dao
interface AllergyDao {
    @Insert
    suspend fun insertRecord(record: AllergyRecord)

    @Query("SELECT * FROM allergy_records ORDER BY id DESC")
    suspend fun getAllRecords(): List<AllergyRecord>

    @Query("DELETE FROM allergy_records WHERE id = :id")
    suspend fun deleteRecord(id: Int)
}