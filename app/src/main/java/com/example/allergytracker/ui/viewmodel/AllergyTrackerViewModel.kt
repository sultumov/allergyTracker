package com.example.allergytracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.allergytracker.data.model.Allergy
import com.example.allergytracker.data.model.AllergyRecord
import com.example.allergytracker.data.repository.AllergyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllergyTrackerViewModel @Inject constructor(
    private val allergyRepository: AllergyRepository
) : ViewModel() {

    private val _allergies = MutableStateFlow<List<Allergy>>(emptyList())
    val allergies: StateFlow<List<Allergy>> = _allergies.asStateFlow()

    private val _reactions = MutableStateFlow<List<AllergyRecord>>(emptyList())
    val reactions: StateFlow<List<AllergyRecord>> = _reactions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                // Загружаем аллергии
                allergyRepository.getAllergies().collect { allergies ->
                    _allergies.value = allergies
                }

                // Загружаем записи о реакциях
                allergyRepository.getAllergyRecords().collect { records ->
                    _reactions.value = records
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Произошла ошибка при загрузке данных"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveAllergy(allergy: Allergy) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                allergyRepository.saveAllergy(allergy)
            } catch (e: Exception) {
                _error.value = e.message ?: "Произошла ошибка при сохранении аллергии"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteAllergy(allergyId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                allergyRepository.deleteAllergy(allergyId)
            } catch (e: Exception) {
                _error.value = e.message ?: "Произошла ошибка при удалении аллергии"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveAllergyRecord(record: AllergyRecord) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                allergyRepository.saveAllergyRecord(record)
            } catch (e: Exception) {
                _error.value = e.message ?: "Произошла ошибка при сохранении записи"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
} 