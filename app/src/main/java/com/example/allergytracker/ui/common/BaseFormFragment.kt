package com.example.allergytracker.ui.common

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

/**
 * Базовый класс для фрагментов с формами добавления и редактирования
 */
abstract class BaseFormFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupToolbar()
        setupFormControls()
        setupButtons()
        observeViewModel()
    }
    
    /**
     * Настройка тулбара
     */
    protected open fun setupToolbar() {
        // По умолчанию добавляем навигацию назад
        getToolbar()?.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    /**
     * Получение тулбара для настройки
     */
    protected abstract fun getToolbar(): View?
    
    /**
     * Настройка элементов формы
     */
    protected abstract fun setupFormControls()
    
    /**
     * Настройка кнопок (сохранить, отмена и т.д.)
     */
    protected abstract fun setupButtons()
    
    /**
     * Наблюдение за изменениями во ViewModel
     */
    protected abstract fun observeViewModel()
    
    /**
     * Проверка введенных данных
     */
    protected abstract fun validateInputs(): Boolean
    
    /**
     * Сохранение данных
     */
    protected abstract fun saveData()
    
    /**
     * Показывает индикатор загрузки
     */
    protected abstract fun showLoading(isLoading: Boolean)
    
    /**
     * Показывает сообщение об ошибке
     */
    protected fun showError(message: String) {
        Timber.e(message)
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
    }
    
    /**
     * Показывает сообщение об успехе и возвращается назад
     */
    protected fun showSuccessAndNavigateBack(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }
} 