package com.example.allergytracker.data.remote

import java.util.concurrent.TimeUnit

data class CacheSettings(
    val productCacheTimeout: Long = TimeUnit.HOURS.toMillis(1),
    val criticalProductCacheTimeout: Long = TimeUnit.MINUTES.toMillis(15),
    val maxCacheSize: Int = 1000,
    val enableOfflineMode: Boolean = true
) {
    companion object {
        val DEFAULT = CacheSettings()
        val STRICT = CacheSettings(
            productCacheTimeout = TimeUnit.MINUTES.toMillis(30),
            criticalProductCacheTimeout = TimeUnit.MINUTES.toMillis(5),
            maxCacheSize = 500,
            enableOfflineMode = true
        )
        val LENIENT = CacheSettings(
            productCacheTimeout = TimeUnit.DAYS.toMillis(1),
            criticalProductCacheTimeout = TimeUnit.HOURS.toMillis(1),
            maxCacheSize = 2000,
            enableOfflineMode = true
        )
    }
} 