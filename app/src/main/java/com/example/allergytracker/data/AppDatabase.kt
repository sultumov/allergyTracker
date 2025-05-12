package com.example.allergytracker.data
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.allergytracker.data.model.AllergyRecord

@Database(entities = [AllergyRecord::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun allergyDao(): AllergyDao // Получение DAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Здесь можно добавить миграции при обновлении версии базы данных
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "allergy_database" // Название базы данных
                )
                .addMigrations(MIGRATION_1_2)
                .fallbackToDestructiveMigration() // Временное решение для разработки
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}