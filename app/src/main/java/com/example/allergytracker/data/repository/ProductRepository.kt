package com.example.allergytracker.data.repository

import com.example.allergytracker.data.model.HistoryItem
import com.example.allergytracker.data.model.Product
import com.example.allergytracker.data.remote.FirebaseDataSource
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val firebaseDataSource: FirebaseDataSource
) {
    suspend fun getProductByBarcode(barcode: String): Product? {
        return try {
            firebaseDataSource.getProductByBarcode(barcode)
        } catch (e: Exception) {
            throw Exception("Не удалось получить информацию о продукте: ${e.message}")
        }
    }

    suspend fun saveProduct(product: Product) {
        try {
            firebaseDataSource.saveProduct(product)
        } catch (e: Exception) {
            throw Exception("Не удалось сохранить продукт: ${e.message}")
        }
    }

    suspend fun addToHistory(product: Product) {
        try {
            val historyItem = HistoryItem(
                id = System.currentTimeMillis().toString(),
                name = product.name,
                date = Date(),
                allergens = buildList {
                    if (product.containsGluten) add("Глютен")
                    if (product.containsLactose) add("Лактоза")
                    if (product.containsNuts) add("Орехи")
                }
            )
            firebaseDataSource.saveHistoryItem(historyItem)
        } catch (e: Exception) {
            throw Exception("Не удалось добавить запись в историю: ${e.message}")
        }
    }

    fun getHistory(): Flow<List<HistoryItem>> {
        return try {
            firebaseDataSource.getHistory()
        } catch (e: Exception) {
            throw Exception("Не удалось получить историю: ${e.message}")
        }
    }

    suspend fun clearHistory() {
        try {
            firebaseDataSource.clearHistory()
        } catch (e: Exception) {
            throw Exception("Не удалось очистить историю: ${e.message}")
        }
    }
} 