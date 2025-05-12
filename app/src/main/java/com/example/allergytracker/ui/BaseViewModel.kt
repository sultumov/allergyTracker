package com.example.allergytracker.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Базовый класс ViewModel с общими функциями обработки ошибок и запуска корутин
 */
abstract class BaseViewModel : ViewModel() {

    /**
     * Создает обработчик исключений для корутин
     * @param onError дополнительная функция для обработки ошибки, по умолчанию null
     * @return CoroutineExceptionHandler
     */
    protected fun createExceptionHandler(onError: ((Throwable) -> Unit)? = null): CoroutineExceptionHandler {
        return CoroutineExceptionHandler { _, throwable ->
            Timber.e(throwable, "Error in ViewModel")
            onError?.invoke(throwable)
        }
    }

    /**
     * Выполняет корутину безопасно с обработкой ошибок
     * @param scope корутин скоуп для запуска
     * @param onError функция обработки ошибки
     * @param block блок кода для выполнения
     * @return Job задача корутина
     */
    protected fun launchSafe(
        scope: CoroutineScope,
        onError: ((Throwable) -> Unit)? = null,
        block: suspend CoroutineScope.() -> Unit
    ): Job {
        return scope.launch(createExceptionHandler(onError)) {
            try {
                block()
            } catch (e: Exception) {
                Timber.e(e, "Error in coroutine")
                onError?.invoke(e)
            }
        }
    }
} 