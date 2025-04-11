package com.example.allergytracker.ui.common

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

/**
 * Базовый класс для фрагментов, отображающих список элементов
 */
abstract class BaseListFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupToolbar()
        setupRecyclerView()
        setupFab()
        setupSearch()
        setupFilters()
        observeViewModel()
    }

    /**
     * Настройка тулбара
     */
    protected abstract fun setupToolbar()

    /**
     * Настройка RecyclerView и адаптера
     */
    protected abstract fun setupRecyclerView()

    /**
     * Настройка кнопки добавления (FAB)
     */
    protected abstract fun setupFab()
    
    /**
     * Настройка поиска
     */
    protected open fun setupSearch() {
        getSearchView()?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                performSearch(newText.orEmpty())
                return true
            }
        })
    }
    
    /**
     * Настройка фильтров (если есть)
     */
    protected open fun setupFilters() {
        // По умолчанию пусто, переопределяется в потомках при необходимости
    }
    
    /**
     * Наблюдение за изменениями во ViewModel
     */
    protected abstract fun observeViewModel()
    
    /**
     * Получение RecyclerView для настройки
     */
    protected abstract fun getRecyclerView(): RecyclerView?
    
    /**
     * Получение FAB для настройки
     */
    protected abstract fun getFab(): FloatingActionButton?
    
    /**
     * Получение SearchView для настройки
     */
    protected abstract fun getSearchView(): SearchView?
    
    /**
     * Выполнение поиска
     */
    protected abstract fun performSearch(query: String)
    
    /**
     * Показывает индикатор загрузки
     */
    protected open fun showLoading(isLoading: Boolean) {
        getLoadingView()?.isVisible = isLoading
        getRecyclerView()?.isVisible = !isLoading
    }
    
    /**
     * Получение индикатора загрузки
     */
    protected abstract fun getLoadingView(): View?
    
    /**
     * Показывает пустой список
     */
    protected open fun showEmptyState(isEmpty: Boolean) {
        getEmptyView()?.isVisible = isEmpty
        getRecyclerView()?.isVisible = !isEmpty
    }
    
    /**
     * Получение View для пустого состояния
     */
    protected abstract fun getEmptyView(): View?
    
    /**
     * Показывает сообщение об ошибке
     */
    protected fun showError(message: String) {
        view?.let {
            Snackbar.make(it, message, Snackbar.LENGTH_LONG).show()
        }
    }
} 