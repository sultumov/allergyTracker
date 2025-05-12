package com.example.allergytracker

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class AllergyTrackerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        
        try {
            // Инициализация Firebase
            FirebaseApp.initializeApp(this)
            
            // Включение офлайн-режима для Firebase Realtime Database
            // ВАЖНО: Должно вызываться до первого обращения к Firebase
            FirebaseDatabase.getInstance().setPersistenceEnabled(true)
            
            // Инициализация логирования
            if (BuildConfig.DEBUG) {
                Timber.plant(Timber.DebugTree())
            }
        } catch (e: Exception) {
            // В release-сборке просто логируем ошибку, приложение продолжит работу
            Timber.e(e, "Error initializing Firebase or Timber")
        }
    }
} 