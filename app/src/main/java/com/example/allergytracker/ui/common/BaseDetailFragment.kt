package com.example.allergytracker.ui.common

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.allergytracker.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

/**
 * Базовый класс для фрагментов с деталями объектов
 */
abstract class BaseDetailFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupToolbar()
        loadData()
        observeViewModel()
    }
    
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_detail, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }
            R.id.action_edit -> {
                navigateToEdit()
                true
            }
            R.id.action_delete -> {
                showDeleteConfirmationDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    /**
     * Настройка тулбара
     */
    protected abstract fun setupToolbar()
    
    /**
     * Загрузка данных объекта
     */
    protected abstract fun loadData()
    
    /**
     * Наблюдение за изменениями во ViewModel
     */
    protected abstract fun observeViewModel()
    
    /**
     * Навигация к экрану редактирования
     */
    protected abstract fun navigateToEdit()
    
    /**
     * Удаление объекта
     */
    protected abstract fun deleteItem()
    
    /**
     * Показывает диалог подтверждения удаления
     */
    protected fun showDeleteConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getDeleteDialogTitle())
            .setMessage(getDeleteDialogMessage())
            .setNegativeButton("Отмена", null)
            .setPositiveButton("Удалить") { _, _ -> deleteItem() }
            .show()
    }
    
    /**
     * Заголовок диалога удаления
     */
    protected abstract fun getDeleteDialogTitle(): String
    
    /**
     * Сообщение диалога удаления
     */
    protected abstract fun getDeleteDialogMessage(): String
    
    /**
     * Показывает индикатор загрузки
     */
    protected fun showLoading(isLoading: Boolean) {
        getLoadingView()?.isVisible = isLoading
        getContentView()?.isVisible = !isLoading
    }
    
    /**
     * Получение View индикатора загрузки
     */
    protected abstract fun getLoadingView(): View?
    
    /**
     * Получение основного контейнера с контентом
     */
    protected abstract fun getContentView(): View?
    
    /**
     * Показывает сообщение об ошибке
     */
    protected fun showError(message: String) {
        view?.let {
            Snackbar.make(it, message, Snackbar.LENGTH_LONG).show()
        }
    }
    
    /**
     * Показывает сообщение об успехе
     */
    protected fun showSuccess(message: String) {
        view?.let {
            Snackbar.make(it, message, Snackbar.LENGTH_SHORT).show()
        }
    }
} 