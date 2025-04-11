package com.example.allergytracker.ui.reaction

import androidx.lifecycle.viewModelScope
import com.example.allergytracker.domain.model.Reaction
import com.example.allergytracker.domain.usecase.reaction.*
import com.example.allergytracker.ui.common.BaseViewModel
import com.example.allergytracker.ui.common.UiState
import com.example.allergytracker.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ReactionViewModel @Inject constructor(
    private val getReactionsUseCase: GetReactionsUseCase,
    private val getReactionsByAllergyIdUseCase: GetReactionsByAllergyIdUseCase,
    private val getRecentReactionsUseCase: GetRecentReactionsUseCase,
    private val addReactionUseCase: AddReactionUseCase,
    private val deleteReactionUseCase: DeleteReactionUseCase
) : BaseViewModel() {

    private val _reactionsState = MutableStateFlow<UiState<List<Reaction>>>(UiState.Loading())
    val reactionsState: StateFlow<UiState<List<Reaction>>> = _reactionsState

    private val _reactionState = MutableStateFlow<UiState<Reaction?>>(UiState.Loading())
    val reactionState: StateFlow<UiState<Reaction?>> = _reactionState

    private val _filterPeriod = MutableStateFlow(FilterPeriod.ALL)
    val filterPeriod: StateFlow<FilterPeriod> = _filterPeriod

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _currentAllergyId = MutableStateFlow<String?>(null)
    val currentAllergyId: StateFlow<String?> = _currentAllergyId

    init {
        loadReactions()
    }

    fun loadReactions() {
        viewModelScope.launch {
            _reactionsState.value = UiState.Loading()
            try {
                val reactionsFlow = when {
                    _currentAllergyId.value != null -> getReactionsByAllergyIdUseCase(_currentAllergyId.value!!)
                    _filterPeriod.value == FilterPeriod.RECENT -> getRecentReactionsUseCase()
                    else -> getReactionsUseCase()
                }

                reactionsFlow
                    .catch { e ->
                        Timber.e(e, "Error loading reactions")
                        _reactionsState.value = UiState.Error(e.message ?: "Unknown error")
                    }
                    .map { reactions ->
                        applyFilters(reactions)
                    }
                    .collect { filteredReactions ->
                        _reactionsState.value = UiState.Success(filteredReactions)
                    }
            } catch (e: Exception) {
                Timber.e(e, "Error setting up reactions flow")
                _reactionsState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun loadReactionsByAllergyId(allergyId: String) {
        _currentAllergyId.value = allergyId
        loadReactions()
    }

    fun clearAllergyFilter() {
        _currentAllergyId.value = null
        loadReactions()
    }

    fun addReaction(reaction: Reaction) {
        launchOperation(
            operation = { addReactionUseCase(reaction) },
            onSuccess = { loadReactions() }
        )
    }

    fun deleteReaction(id: String) {
        launchOperation(
            operation = { deleteReactionUseCase(id) },
            onSuccess = { loadReactions() }
        )
    }

    fun setFilterPeriod(period: FilterPeriod) {
        _filterPeriod.value = period
        loadReactions()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        loadReactions()
    }

    private fun applyFilters(reactions: List<Reaction>): List<Reaction> {
        val filtered = when (_filterPeriod.value) {
            FilterPeriod.ALL -> reactions
            FilterPeriod.TODAY -> reactions.filter { DateUtils.isToday(it.date) }
            FilterPeriod.WEEK -> reactions.filter { DateUtils.isThisWeek(it.date) }
            FilterPeriod.MONTH -> reactions.filter { DateUtils.isThisMonth(it.date) }
            FilterPeriod.RECENT -> reactions
        }

        val query = _searchQuery.value.trim().lowercase()
        return if (query.isEmpty()) {
            filtered
        } else {
            filtered.filter { reaction ->
                reaction.severity.lowercase().contains(query) ||
                        reaction.symptoms.any { it.lowercase().contains(query) } ||
                        reaction.notes.lowercase().contains(query)
            }
        }
    }

    enum class FilterPeriod {
        ALL, TODAY, WEEK, MONTH, RECENT
    }
} 