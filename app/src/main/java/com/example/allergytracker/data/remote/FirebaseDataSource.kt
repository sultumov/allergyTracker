package com.example.allergytracker.data.remote

import com.example.allergytracker.data.local.LocalCache
import com.example.allergytracker.data.model.Allergy
import com.example.allergytracker.data.model.AllergyRecord
import com.example.allergytracker.data.model.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseDataSource @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: FirebaseDatabase,
    private val localCache: LocalCache
) {
    init {
        // Enable offline persistence
        database.setPersistenceEnabled(true)
    }

    private fun getCurrentUserId(): String {
        return auth.currentUser?.uid ?: throw IllegalStateException("User not authenticated")
    }

    // Allergy operations
    suspend fun saveAllergy(allergy: Allergy) {
        val userId = getCurrentUserId()
        database.getReference("users/$userId/allergies/${allergy.id}")
            .setValue(allergy)
            .await()
        // Update local cache
        val currentAllergies = localCache.getAllergies().toMutableList()
        val index = currentAllergies.indexOfFirst { it.id == allergy.id }
        if (index != -1) {
            currentAllergies[index] = allergy
        } else {
            currentAllergies.add(allergy)
        }
        localCache.saveAllergies(currentAllergies)
    }

    fun getAllergies(): Flow<List<Allergy>> = callbackFlow {
        val userId = getCurrentUserId()
        
        // First emit cached data
        trySend(localCache.getAllergies())

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val allergies = snapshot.children.mapNotNull { it.getValue(Allergy::class.java) }
                localCache.saveAllergies(allergies)
                trySend(allergies)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        database.getReference("users/$userId/allergies")
            .addValueEventListener(listener)

        awaitClose {
            database.getReference("users/$userId/allergies")
                .removeEventListener(listener)
        }
    }

    suspend fun deleteAllergy(allergyId: String) {
        val userId = getCurrentUserId()
        database.getReference("users/$userId/allergies/$allergyId")
            .removeValue()
            .await()
        // Update local cache
        val currentAllergies = localCache.getAllergies().toMutableList()
        currentAllergies.removeAll { it.id == allergyId }
        localCache.saveAllergies(currentAllergies)
    }

    // Allergy Record operations
    suspend fun saveAllergyRecord(record: AllergyRecord) {
        val userId = getCurrentUserId()
        database.getReference("users/$userId/records/${record.id}")
            .setValue(record)
            .await()
        // Update local cache
        val currentRecords = localCache.getAllergyRecords().toMutableList()
        val index = currentRecords.indexOfFirst { it.id == record.id }
        if (index != -1) {
            currentRecords[index] = record
        } else {
            currentRecords.add(record)
        }
        localCache.saveAllergyRecords(currentRecords)
    }

    fun getAllergyRecords(): Flow<List<AllergyRecord>> = callbackFlow {
        val userId = getCurrentUserId()
        
        // First emit cached data
        trySend(localCache.getAllergyRecords())

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val records = snapshot.children.mapNotNull { it.getValue(AllergyRecord::class.java) }
                localCache.saveAllergyRecords(records)
                trySend(records)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        database.getReference("users/$userId/records")
            .addValueEventListener(listener)

        awaitClose {
            database.getReference("users/$userId/records")
                .removeEventListener(listener)
        }
    }

    // Product operations
    suspend fun saveProduct(product: Product) {
        database.getReference("products/${product.barcode}")
            .setValue(product)
            .await()
        // Update local cache
        localCache.saveProduct(product)
    }

    suspend fun getProductByBarcode(barcode: String): Product? {
        // First check local cache
        val cachedProduct = localCache.getProductByBarcode(barcode)
        if (cachedProduct != null) {
            return cachedProduct
        }

        // If not in cache, fetch from Firebase
        return try {
            val snapshot = database.getReference("products/$barcode")
                .get()
                .await()
            val product = snapshot.getValue(Product::class.java)
            if (product != null) {
                localCache.saveProduct(product)
            }
            product
        } catch (e: Exception) {
            null
        }
    }

    fun getAllProducts(): Flow<List<Product>> = callbackFlow {
        // First emit cached data
        trySend(localCache.getAllProducts())

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val products = snapshot.children.mapNotNull { it.getValue(Product::class.java) }
                products.forEach { localCache.saveProduct(it) }
                trySend(products)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        database.getReference("products")
            .addValueEventListener(listener)

        awaitClose {
            database.getReference("products")
                .removeEventListener(listener)
        }
    }

    // Sync operations
    suspend fun syncData() {
        val userId = getCurrentUserId()
        val lastSyncTime = localCache.getLastSyncTime()
        
        // Sync allergies
        val allergiesSnapshot = database.getReference("users/$userId/allergies")
            .orderByChild("lastModified")
            .startAt(lastSyncTime)
            .get()
            .await()
        
        val allergies = allergiesSnapshot.children.mapNotNull { it.getValue(Allergy::class.java) }
        localCache.saveAllergies(allergies)

        // Sync records
        val recordsSnapshot = database.getReference("users/$userId/records")
            .orderByChild("lastModified")
            .startAt(lastSyncTime)
            .get()
            .await()
        
        val records = recordsSnapshot.children.mapNotNull { it.getValue(AllergyRecord::class.java) }
        localCache.saveAllergyRecords(records)

        // Update last sync time
        localCache.saveLastSyncTime(System.currentTimeMillis())
    }

    // History operations
    suspend fun saveHistoryItem(item: HistoryItem) {
        val userId = getCurrentUserId()
        database.getReference("users/$userId/history/${item.id}")
            .setValue(item)
            .await()
        // Update local cache
        val currentHistory = localCache.getHistory().toMutableList()
        currentHistory.add(item)
        localCache.saveHistory(currentHistory)
    }

    fun getHistory(): Flow<List<HistoryItem>> = callbackFlow {
        val userId = getCurrentUserId()
        
        // First emit cached data
        trySend(localCache.getHistory())

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children.mapNotNull { it.getValue(HistoryItem::class.java) }
                localCache.saveHistory(items)
                trySend(items.sortedByDescending { it.scanDate })
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        database.getReference("users/$userId/history")
            .addValueEventListener(listener)

        awaitClose {
            database.getReference("users/$userId/history")
                .removeEventListener(listener)
        }
    }

    suspend fun clearHistory() {
        val userId = getCurrentUserId()
        database.getReference("users/$userId/history")
            .removeValue()
            .await()
        // Clear local cache
        localCache.saveHistory(emptyList())
    }
} 