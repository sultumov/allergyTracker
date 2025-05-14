package com.example.allergytracker.ui.allergy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.allergytracker.domain.model.Reaction
import com.example.allergytracker.domain.repository.ReactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReactionDetailsViewModel @Inject constructor(
    private val reactionRepository: ReactionRepository
) : ViewModel() {

    private val _reaction = MutableStateFlow<Reaction?>(null)
    val reaction: StateFlow<Reaction?> = _reaction.asStateFlow()

    private val _uiState = MutableStateFlow<ReactionDetailsUiState>(ReactionDetailsUiState.Success)
    val uiState: StateFlow<ReactionDetailsUiState> = _uiState.asStateFlow()

    fun loadReaction(reactionId: String) {
        viewModelScope.launch {
            try {
                _uiState.value = ReactionDetailsUiState.Loading
                val reaction = reactionRepository.getReactionById(reactionId).first()
                _reaction.value = reaction
                _uiState.value = ReactionDetailsUiState.Success
            } catch (e: Exception) {
                _uiState.value = ReactionDetailsUiState.Error(e.message ?: "Ошибка загрузки данных")
            }
        }
    }

    fun deleteReaction(reactionId: String) {
        viewModelScope.launch {
            try {
                _uiState.value = ReactionDetailsUiState.Loading
                reactionRepository.deleteReaction(reactionId)
                _uiState.value = ReactionDetailsUiState.Success
            } catch (e: Exception) {
                _uiState.value = ReactionDetailsUiState.Error(e.message ?: "Ошибка удаления")
            }
        }
    }
}

sealed class ReactionDetailsUiState {
    object Success : ReactionDetailsUiState()
    object Loading : ReactionDetailsUiState()
    data class Error(val message: String) : ReactionDetailsUiState()
} 