package com.example.allergytracker.util

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Менеджер для работы с SharedPreferences
 */
@Singleton
class PreferenceManager @Inject constructor(private val context: Context) {

    private val prefs: SharedPreferences by lazy { 
        PreferenceManager.getDefaultSharedPreferences(context) 
    }
    private val gson = Gson()

    /**
     * Константы ключей
     */
    object Keys {
        const val SHOW_ACTIVE_ALLERGIES_ONLY = "show_active_allergies_only"
        const val DEFAULT_FILTER_PERIOD = "default_filter_period"
        const val LAST_SEARCH_QUERY = "last_search_query"
        const val DARK_MODE = "dark_mode"
        const val NOTIFICATIONS_ENABLED = "notifications_enabled"
        const val EXPORT_SETTINGS = "export_settings"
        const val LAST_SYNC_TIME = "last_sync_time"
    }

    /**
     * Получение строкового значения
     */
    fun getString(key: String, defaultValue: String = ""): String {
        return prefs.getString(key, defaultValue) ?: defaultValue
    }

    /**
     * Сохранение строкового значения
     */
    fun putString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    /**
     * Получение целочисленного значения
     */
    fun getInt(key: String, defaultValue: Int = 0): Int {
        return prefs.getInt(key, defaultValue)
    }

    /**
     * Сохранение целочисленного значения
     */
    fun putInt(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    /**
     * Получение булевого значения
     */
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return prefs.getBoolean(key, defaultValue)
    }

    /**
     * Сохранение булевого значения
     */
    fun putBoolean(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    /**
     * Получение значения с плавающей точкой
     */
    fun getFloat(key: String, defaultValue: Float = 0f): Float {
        return prefs.getFloat(key, defaultValue)
    }

    /**
     * Сохранение значения с плавающей точкой
     */
    fun putFloat(key: String, value: Float) {
        prefs.edit().putFloat(key, value).apply()
    }

    /**
     * Получение значения типа Long
     */
    fun getLong(key: String, defaultValue: Long = 0L): Long {
        return prefs.getLong(key, defaultValue)
    }

    /**
     * Сохранение значения типа Long
     */
    fun putLong(key: String, value: Long) {
        prefs.edit().putLong(key, value).apply()
    }

    /**
     * Сохранение объекта в JSON
     */
    inline fun <reified T> putObject(key: String, value: T) {
        try {
            val json = gson.toJson(value)
            putString(key, json)
        } catch (e: Exception) {
            Timber.e(e, "Error saving object with key: $key")
        }
    }

    /**
     * Получение объекта из JSON
     */
    inline fun <reified T> getObject(key: String, defaultValue: T? = null): T? {
        return try {
            val json = getString(key)
            if (json.isEmpty()) defaultValue
            else gson.fromJson(json, T::class.java)
        } catch (e: Exception) {
            Timber.e(e, "Error getting object with key: $key")
            defaultValue
        }
    }

    /**
     * Проверка наличия ключа
     */
    fun contains(key: String): Boolean {
        return prefs.contains(key)
    }

    /**
     * Удаление значения по ключу
     */
    fun remove(key: String) {
        prefs.edit().remove(key).apply()
    }

    /**
     * Очистка всех настроек
     */
    fun clear() {
        prefs.edit().clear().apply()
    }
} 