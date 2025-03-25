package com.example.allergytracker.data
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [AllergyRecord::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun allergyDao(): AllergyDao // Получение DAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "allergy_database" // Название базы данных
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}