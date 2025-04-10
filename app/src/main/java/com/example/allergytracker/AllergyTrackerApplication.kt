package com.example.allergytracker

import android.app.Application
import com.google.firebase.FirebaseApp

class AllergyTrackerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
} 