package com.example.allergytracker.ui.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.allergytracker.data.model.AllergyRecord
import com.example.allergytracker.data.model.HistoryItem
import com.example.allergytracker.data.repository.ProductRepository
import com.example.allergytracker.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _historyState = MutableLiveData<UiState<List<AllergyRecord>>>()
    val historyState: LiveData<UiState<List<AllergyRecord>>> = _historyState

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private var glutenFilter = false
    private var lactoseFilter = false
    private var nutsFilter = false

    private var cachedItems: List<HistoryItem> = emptyList()

    init {
        loadHistory()
    }

    fun loadHistory() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val records = repository.getAllergyRecords()
                if (records.isEmpty()) {
                    _historyState.value = UiState.Empty
                } else {
                    _historyState.value = UiState.Success(records)
                }
            } catch (e: Exception) {
                _historyState.value = UiState.Error("Ошибка загрузки истории: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setGlutenFilter(isChecked: Boolean) {
        glutenFilter = isChecked
    }

    fun setLactoseFilter(isChecked: Boolean) {
        lactoseFilter = isChecked
    }

    fun setNutsFilter(isChecked: Boolean) {
        nutsFilter = isChecked
    }

    fun applyFilters() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                applyFilters(cachedItems)
            } catch (e: Exception) {
                _error.value = "Error applying filters: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun applyFilters(items: List<HistoryItem>) {
        val filteredItems = items.filter { item ->
            when {
                glutenFilter && !item.containsGluten -> false
                lactoseFilter && !item.containsLactose -> false
                nutsFilter && !item.containsNuts -> false
                else -> true
            }
        }
        _historyState.value = UiState.Success(filteredItems.map { it.allergyRecord })
    }

    fun clearError() {
        _error.value = null
    }

    fun deleteRecord(record: AllergyRecord) {
        viewModelScope.launch {
            try {
                repository.deleteAllergyRecord(record)
                loadHistory()
            } catch (e: Exception) {
                _error.value = "Ошибка при удалении записи: ${e.message}"
            }
        }
    }

    fun addRecord(record: AllergyRecord) {
        viewModelScope.launch {
            try {
                repository.addAllergyRecord(record)
                loadHistory()
            } catch (e: Exception) {
                _error.value = "Ошибка при добавлении записи: ${e.message}"
            }
        }
    }
} 