package com.example.allergytracker.ui.util

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LifecycleCoroutineScope
import com.example.allergytracker.data.remote.FirestoreConnectionChecker
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * Утилитный класс для работы с Firebase
 */
object FirebaseUtils {
    /**
     * Проверяет статус Firebase и Firestore
     */
    fun checkAndDisplayFirebaseStatus(context: Context, lifecycleScope: LifecycleCoroutineScope) {
        lifecycleScope.launch {
            val isFirebaseInitialized = try {
                FirebaseApp.getInstance() != null
            } catch (e: Exception) {
                Timber.e(e, "Firebase not initialized")
                false
            }
            
            if (!isFirebaseInitialized) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Ошибка: Firebase не инициализирован",
                        Toast.LENGTH_LONG
                    ).show()
                }
                return@launch
            }
            
            val isFirestoreConnected = try {
                withContext(Dispatchers.IO) {
                    FirestoreConnectionChecker.checkConnection()
                }
            } catch (e: Exception) {
                Timber.e(e, "Error checking Firestore connection")
                false
            }
            
            withContext(Dispatchers.Main) {
                if (isFirestoreConnected) {
                    Toast.makeText(
                        context,
                        "Firebase и Firestore успешно подключены",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        context,
                        "Ошибка подключения к Firestore. Проверьте интернет-соединение и настройки Firebase",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
} 