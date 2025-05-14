package com.example.allergytracker.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume

/**
 * Утилитный класс для проверки соединения с Firestore
 */
object FirestoreConnectionChecker {
    /**
     * Проверяет соединение с Firestore
     * @return true если соединение успешно установлено, false в противном случае
     */
    suspend fun checkConnection(): Boolean = suspendCancellableCoroutine { continuation ->
        try {
            val db = FirebaseFirestore.getInstance()
            
            // Устанавливаем настройки для проверки соединения
            val settings = FirebaseFirestoreSettings.Builder()
                .setHost("firestore.googleapis.com")
                .setSslEnabled(true)
                .build()
            db.firestoreSettings = settings
            
            // Пробуем получить доступ к коллекции симптомов
            val task = db.collection("symptoms")
                .limit(1)
                .get()
                
            // Добавляем обработчики для проверки успешности
            task.addOnSuccessListener {
                Timber.d("Firestore connection successful")
                if (continuation.isActive) continuation.resume(true)
            }
            
            task.addOnFailureListener { exception ->
                Timber.e(exception, "Firestore connection failed")
                if (continuation.isActive) continuation.resume(false)
            }
            
            // Добавляем таймаут для операции
            task.addOnCanceledListener {
                Timber.w("Firestore connection canceled")
                if (continuation.isActive) continuation.resume(false)
            }
            
        } catch (e: Exception) {
            Timber.e(e, "Error checking Firestore connection")
            if (continuation.isActive) continuation.resume(false)
        }
    }
} 