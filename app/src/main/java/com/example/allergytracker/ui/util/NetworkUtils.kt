package com.example.allergytracker.ui.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import timber.log.Timber

/**
 * Утилитный класс для проверки сетевого подключения
 */
object NetworkUtils {

    /**
     * Проверяет доступность сети интернет
     * @param context Контекст приложения
     * @return true если сетевое подключение доступно, false в противном случае
     */
    fun isNetworkAvailable(context: Context): Boolean {
        try {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val network = connectivityManager.activeNetwork ?: return false
                val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
                
                return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                       capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            } else {
                @Suppress("DEPRECATION")
                val networkInfo = connectivityManager.activeNetworkInfo
                @Suppress("DEPRECATION")
                return networkInfo != null && networkInfo.isConnected
            }
        } catch (e: Exception) {
            Timber.e(e, "Error checking network availability")
            return false
        }
    }

    /**
     * Выполняет проверку сети перед выполнением сетевой операции
     * @param context Контекст приложения
     * @param onNetworkAvailable колбэк, выполняемый при доступности сети
     * @param onNetworkUnavailable колбэк, выполняемый при недоступности сети (по умолчанию null)
     */
    inline fun withNetworkCheck(
        context: Context,
        onNetworkAvailable: () -> Unit,
        onNetworkUnavailable: (() -> Unit)? = null
    ) {
        if (isNetworkAvailable(context)) {
            onNetworkAvailable()
        } else {
            onNetworkUnavailable?.invoke()
        }
    }
} 