package com.example.allergytracker.ui.symptom

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.allergytracker.domain.model.Symptom
import com.example.allergytracker.domain.usecase.symptom.*
import com.example.allergytracker.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SymptomViewModel @Inject constructor(
    private val getAllSymptomsUseCase: GetAllSymptomsUseCase,
    private val getSymptomsByAllergyIdUseCase: GetSymptomsByAllergyIdUseCase,
    private val getRecentSymptomsUseCase: GetRecentSymptomsUseCase,
    private val addSymptomUseCase: AddSymptomUseCase,
    private val updateSymptomUseCase: UpdateSymptomUseCase,
    private val deleteSymptomUseCase: DeleteSymptomUseCase
) : ViewModel() {

    // Список всех симптомов
    private val _allSymptoms = MutableLiveData<UiState<List<Symptom>>>()
    val allSymptoms: LiveData<UiState<List<Symptom>>> = _allSymptoms

    // Список симптомов для конкретной аллергии
    private val _allergySymptoms = MutableLiveData<UiState<List<Symptom>>>()
    val allergySymptoms: LiveData<UiState<List<Symptom>>> = _allergySymptoms

    // Недавние симптомы
    private val _recentSymptoms = MutableLiveData<UiState<List<Symptom>>>()
    val recentSymptoms: LiveData<UiState<List<Symptom>>> = _recentSymptoms

    // Состояние операции сохранения/обновления
    private val _operationState = MutableLiveData<UiState<Boolean>>()
    val operationState: LiveData<UiState<Boolean>> = _operationState

    // Обработчик исключений
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Timber.e(throwable, "Error in SymptomViewModel")
        _allSymptoms.postValue(UiState.Error("Произошла ошибка: ${throwable.localizedMessage}"))
    }

    // Загрузить все симптомы
    fun loadAllSymptoms() {
        _allSymptoms.value = UiState.Loading
        viewModelScope.launch(exceptionHandler) {
            getAllSymptomsUseCase()
                .flowOn(Dispatchers.IO)
                .catch { e ->
                    Timber.e(e, "Error loading all symptoms")
                    _allSymptoms.value = UiState.Error("Ошибка загрузки данных: ${e.localizedMessage}")
                }
                .collect { symptoms ->
                    _allSymptoms.value = UiState.Success(symptoms)
                }
        }
    }

    // Загрузить симптомы для конкретной аллергии
    fun loadSymptomsByAllergyId(allergyId: Long) {
        _allergySymptoms.value = UiState.Loading
        viewModelScope.launch(exceptionHandler) {
            getSymptomsByAllergyIdUseCase(allergyId)
                .flowOn(Dispatchers.IO)
                .catch { e ->
                    Timber.e(e, "Error loading symptoms for allergy $allergyId")
                    _allergySymptoms.value = UiState.Error("Ошибка загрузки данных: ${e.localizedMessage}")
                }
                .collect { symptoms ->
                    _allergySymptoms.value = UiState.Success(symptoms)
                }
        }
    }

    // Загрузить недавние симптомы
    fun loadRecentSymptoms(days: Int = 7) {
        _recentSymptoms.value = UiState.Loading
        viewModelScope.launch(exceptionHandler) {
            getRecentSymptomsUseCase(days)
                .flowOn(Dispatchers.IO)
                .catch { e ->
                    Timber.e(e, "Error loading recent symptoms")
                    _recentSymptoms.value = UiState.Error("Ошибка загрузки данных: ${e.localizedMessage}")
                }
                .collect { symptoms ->
                    _recentSymptoms.value = UiState.Success(symptoms)
                }
        }
    }

    // Добавить новый симптом
    fun addSymptom(
        allergyId: Long,
        name: String,
        severity: Int,
        notes: String,
        location: String = "",
        triggers: List<String> = emptyList(),
        medicationTaken: String = ""
    ) {
        _operationState.value = UiState.Loading
        viewModelScope.launch(exceptionHandler) {
            try {
                val symptom = Symptom(
                    id = 0, // ID будет назначен в репозитории
                    allergyId = allergyId,
                    name = name,
                    severity = severity,
                    notes = notes,
                    timestamp = Date(),
                    location = location,
                    triggers = triggers,
                    medicationTaken = medicationTaken,
                    isActive = true
                )

                withContext(Dispatchers.IO) {
                    addSymptomUseCase(symptom)
                }

                _operationState.value = UiState.Success(true)
                
                // Обновляем соответствующие списки
                loadRecentSymptoms()
                if (allergyId > 0) {
                    loadSymptomsByAllergyId(allergyId)
                }
                loadAllSymptoms()
            } catch (e: Exception) {
                Timber.e(e, "Error adding symptom")
                _operationState.value = UiState.Error("Ошибка сохранения: ${e.localizedMessage}")
            }
        }
    }

    // Обновить существующий симптом
    fun updateSymptom(symptom: Symptom) {
        _operationState.value = UiState.Loading
        viewModelScope.launch(exceptionHandler) {
            try {
                withContext(Dispatchers.IO) {
                    updateSymptomUseCase(symptom)
                }

                _operationState.value = UiState.Success(true)
                
                // Обновляем соответствующие списки
                loadRecentSymptoms()
                if (symptom.allergyId > 0) {
                    loadSymptomsByAllergyId(symptom.allergyId)
                }
                loadAllSymptoms()
            } catch (e: Exception) {
                Timber.e(e, "Error updating symptom")
                _operationState.value = UiState.Error("Ошибка обновления: ${e.localizedMessage}")
            }
        }
    }

    // Удалить симптом
    fun deleteSymptom(symptom: Symptom) {
        viewModelScope.launch(exceptionHandler) {
            try {
                withContext(Dispatchers.IO) {
                    deleteSymptomUseCase(symptom.id)
                }
                
                // Обновляем соответствующие списки
                loadRecentSymptoms()
                if (symptom.allergyId > 0) {
                    loadSymptomsByAllergyId(symptom.allergyId)
                }
                loadAllSymptoms()
            } catch (e: Exception) {
                Timber.e(e, "Error deleting symptom")
                _operationState.value = UiState.Error("Ошибка удаления: ${e.localizedMessage}")
            }
        }
    }
} 