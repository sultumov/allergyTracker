package com.example.allergytracker.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.allergytracker.data.model.Allergy
import com.example.allergytracker.data.model.AllergyRecord
import com.example.allergytracker.data.model.Product
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalCache @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("allergy_tracker_cache", Context.MODE_PRIVATE)

    // Allergy cache
    fun saveAllergies(allergies: List<Allergy>) {
        val json = gson.toJson(allergies)
        prefs.edit().putString(KEY_ALLERGIES, json).apply()
    }

    fun getAllergies(): List<Allergy> {
        val json = prefs.getString(KEY_ALLERGIES, null) ?: return emptyList()
        val type = object : TypeToken<List<Allergy>>() {}.type
        return gson.fromJson(json, type)
    }

    // Allergy Record cache
    fun saveAllergyRecords(records: List<AllergyRecord>) {
        val json = gson.toJson(records)
        prefs.edit().putString(KEY_RECORDS, json).apply()
    }

    fun getAllergyRecords(): List<AllergyRecord> {
        val json = prefs.getString(KEY_RECORDS, null) ?: return emptyList()
        val type = object : TypeToken<List<AllergyRecord>>() {}.type
        return gson.fromJson(json, type)
    }

    // Product cache
    fun saveProduct(product: Product) {
        val products = getAllProducts().toMutableList()
        val existingIndex = products.indexOfFirst { it.barcode == product.barcode }
        if (existingIndex != -1) {
            products[existingIndex] = product
        } else {
            products.add(product)
        }
        saveProducts(products)
    }

    fun getProductByBarcode(barcode: String): Product? {
        return getAllProducts().find { it.barcode == barcode }
    }

    fun getAllProducts(): List<Product> {
        val json = prefs.getString(KEY_PRODUCTS, null) ?: return emptyList()
        val type = object : TypeToken<List<Product>>() {}.type
        return gson.fromJson(json, type)
    }

    private fun saveProducts(products: List<Product>) {
        val json = gson.toJson(products)
        prefs.edit().putString(KEY_PRODUCTS, json).apply()
    }

    // Last sync time
    fun saveLastSyncTime(time: Long) {
        prefs.edit().putLong(KEY_LAST_SYNC, time).apply()
    }

    fun getLastSyncTime(): Long {
        return prefs.getLong(KEY_LAST_SYNC, 0)
    }

    // History operations
    fun saveHistory(history: List<HistoryItem>) {
        val json = gson.toJson(history)
        prefs.edit().putString("history", json).apply()
    }

    fun getHistory(): List<HistoryItem> {
        val json = prefs.getString("history", "[]")
        return gson.fromJson(json, Array<HistoryItem>::class.java).toList()
    }

    companion object {
        private const val KEY_ALLERGIES = "allergies"
        private const val KEY_RECORDS = "records"
        private const val KEY_PRODUCTS = "products"
        private const val KEY_LAST_SYNC = "last_sync"
    }
} 