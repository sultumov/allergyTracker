package com.example.allergytracker.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.allergytracker.data.model.Allergy
import com.example.allergytracker.data.model.AllergyRecord
import com.example.allergytracker.data.model.HistoryItem
import com.example.allergytracker.data.model.Product
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Класс для локального кэширования данных на устройстве
 */
@Singleton
class LocalCache @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
) {
    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // Методы для работы с аллергиями
    fun saveAllergies(allergies: List<Allergy>) {
        try {
            val json = gson.toJson(allergies)
            sharedPreferences.edit().putString(KEY_ALLERGIES, json).apply()
        } catch (e: Exception) {
            Timber.e(e, "Error saving allergies to cache")
        }
    }

    fun getAllergies(): List<Allergy> {
        return try {
            val json = sharedPreferences.getString(KEY_ALLERGIES, null)
            if (json.isNullOrEmpty()) {
                emptyList()
            } else {
                val type = object : TypeToken<List<Allergy>>() {}.type
                gson.fromJson(json, type)
            }
        } catch (e: Exception) {
            Timber.e(e, "Error getting allergies from cache")
            emptyList()
        }
    }

    // Методы для работы с записями аллергий
    fun saveAllergyRecords(records: List<AllergyRecord>) {
        try {
            val json = gson.toJson(records)
            sharedPreferences.edit().putString(KEY_RECORDS, json).apply()
        } catch (e: Exception) {
            Timber.e(e, "Error saving allergy records to cache")
        }
    }

    fun getAllergyRecords(): List<AllergyRecord> {
        return try {
            val json = sharedPreferences.getString(KEY_RECORDS, null)
            if (json.isNullOrEmpty()) {
                emptyList()
            } else {
                val type = object : TypeToken<List<AllergyRecord>>() {}.type
                gson.fromJson(json, type)
            }
        } catch (e: Exception) {
            Timber.e(e, "Error getting allergy records from cache")
            emptyList()
        }
    }

    // Методы для работы с продуктами
    fun saveProduct(product: Product) {
        try {
            val products = getAllProducts().toMutableList()
            val index = products.indexOfFirst { it.barcode == product.barcode }
            if (index >= 0) {
                products[index] = product
            } else {
                products.add(product)
            }
            val json = gson.toJson(products)
            sharedPreferences.edit().putString(KEY_PRODUCTS, json).apply()
        } catch (e: Exception) {
            Timber.e(e, "Error saving product to cache")
        }
    }

    fun getProductByBarcode(barcode: String): Product? {
        return try {
            getAllProducts().find { it.barcode == barcode }
        } catch (e: Exception) {
            Timber.e(e, "Error getting product by barcode from cache")
            null
        }
    }

    fun getAllProducts(): List<Product> {
        return try {
            val json = sharedPreferences.getString(KEY_PRODUCTS, null)
            if (json.isNullOrEmpty()) {
                emptyList()
            } else {
                val type = object : TypeToken<List<Product>>() {}.type
                gson.fromJson(json, type)
            }
        } catch (e: Exception) {
            Timber.e(e, "Error getting products from cache")
            emptyList()
        }
    }

    // Методы для работы с историей
    fun saveHistory(items: List<HistoryItem>) {
        try {
            val json = gson.toJson(items)
            sharedPreferences.edit().putString(KEY_HISTORY, json).apply()
        } catch (e: Exception) {
            Timber.e(e, "Error saving history to cache")
        }
    }

    fun getHistory(): List<HistoryItem> {
        return try {
            val json = sharedPreferences.getString(KEY_HISTORY, null)
            if (json.isNullOrEmpty()) {
                emptyList()
            } else {
                val type = object : TypeToken<List<HistoryItem>>() {}.type
                gson.fromJson(json, type)
            }
        } catch (e: Exception) {
            Timber.e(e, "Error getting history from cache")
            emptyList()
        }
    }

    // Методы для синхронизации
    fun getLastSyncTime(): Long {
        return sharedPreferences.getLong(KEY_LAST_SYNC, 0L)
    }

    fun saveLastSyncTime(time: Long) {
        sharedPreferences.edit().putLong(KEY_LAST_SYNC, time).apply()
    }

    companion object {
        private const val PREFS_NAME = "allergy_tracker_cache"
        private const val KEY_ALLERGIES = "allergies"
        private const val KEY_RECORDS = "allergy_records"
        private const val KEY_PRODUCTS = "products"
        private const val KEY_HISTORY = "history"
        private const val KEY_LAST_SYNC = "last_sync_time"
    }
} 