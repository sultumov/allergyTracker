package com.example.allergytracker.util

import androidx.navigation.NavController
import androidx.navigation.NavDirections
import timber.log.Timber

/**
 * Безопасная навигация с предотвращением дублирования навигационных действий
 */
fun NavController.safeNavigate(directions: NavDirections) {
    try {
        val action = currentDestination?.getAction(directions.actionId)
        if (action != null) {
            navigate(directions)
        } else {
            Timber.w("Navigation action not found: ${directions.actionId}")
        }
    } catch (e: Exception) {
        Timber.e(e, "Error during navigation to ${directions.actionId}")
    }
} 