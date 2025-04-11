package com.example.allergytracker.ui.reaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.allergytracker.databinding.FragmentReactionDetailsBinding
import com.example.allergytracker.domain.model.Reaction
import com.example.allergytracker.ui.allergy.AllergyViewModel
import com.example.allergytracker.ui.common.UiState
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class ReactionDetailsFragment : Fragment() {

    private var _binding: FragmentReactionDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ReactionViewModel by viewModels()
    private val allergyViewModel: AllergyViewModel by viewModels()
    private val args: ReactionDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReactionDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupToolbar()
        setupButtons()
        
        // Загружаем детали реакции
        loadReactionDetails()
        
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    private fun setupButtons() {
        binding.buttonEdit.setOnClickListener {
            navigateToEditReaction()
        }
        
        binding.buttonDelete.setOnClickListener {
            showDeleteDialog()
        }
        
        binding.buttonViewAllergy.setOnClickListener {
            navigateToAllergyDetails()
        }
    }
    
    private fun loadReactionDetails() {
        // Загружаем детали реакции
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getReactionById(args.reactionId).collect { reaction ->
                reaction?.let {
                    // Загружаем информацию об аллергии
                    allergyViewModel.loadAllergyById(it.allergyId)
                }
            }
        }
    }
    
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Наблюдаем за деталями реакции
                launch {
                    viewModel.reactionState.collectLatest { state ->
                        when (state) {
                            is UiState.Loading -> showLoading(true)
                            is UiState.Success -> {
                                showLoading(false)
                                state.data?.let { updateUI(it) }
                            }
                            is UiState.Error -> {
                                showLoading(false)
                                showError(state.message)
                            }
                        }
                    }
                }
                
                // Наблюдаем за деталями аллергии
                launch {
                    allergyViewModel.allergyState.collectLatest { state ->
                        when (state) {
                            is UiState.Success -> {
                                state.data?.let { allergy ->
                                    binding.textAllergyName.text = allergy.name
                                }
                            }
                            is UiState.Error -> {
                                binding.textAllergyName.text = "Неизвестная аллергия"
                            }
                            else -> {}
                        }
                    }
                }
            }
        }
    }
    
    private fun updateUI(reaction: Reaction) {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        
        binding.apply {
            // Основная информация
            textDate.text = dateFormat.format(reaction.date)
            textSeverity.text = reaction.severity
            
            // Симптомы (добавляем чипы для каждого симптома)
            chipGroupSymptoms.removeAllViews()
            reaction.symptoms.forEach { symptom ->
                val chip = Chip(requireContext()).apply {
                    text = symptom
                    isClickable = false
                }
                chipGroupSymptoms.addView(chip)
            }
            
            // Заметки
            textNotes.text = reaction.notes.takeIf { it.isNotEmpty() } ?: "Нет заметок"
            
            // Лекарство
            textMedication.text = reaction.medication ?: "Не указано"
            
            // Продолжительность
            textDuration.text = if (reaction.duration != null) {
                "${reaction.duration} минут"
            } else {
                "Не указана"
            }
            
            // Заголовок
            toolbar.title = "Реакция от ${dateFormat.format(reaction.date)}"
        }
    }
    
    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.contentLayout.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
    }
    
    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
    
    private fun navigateToEditReaction() {
        try {
            val action = ReactionDetailsFragmentDirections.actionReactionDetailsToEditReaction(args.reactionId)
            findNavController().navigate(action)
        } catch (e: Exception) {
            Timber.e(e, "Error navigating to edit reaction")
            showError("Ошибка при переходе к редактированию")
        }
    }
    
    private fun navigateToAllergyDetails() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                viewModel.getReactionById(args.reactionId).collect { reaction ->
                    reaction?.let {
                        val action = ReactionDetailsFragmentDirections.actionReactionDetailsToAllergyDetails(it.allergyId)
                        findNavController().navigate(action)
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error navigating to allergy details")
                showError("Ошибка при переходе к деталям аллергии")
            }
        }
    }
    
    private fun showDeleteDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Удаление реакции")
            .setMessage("Вы уверены, что хотите удалить эту запись о реакции? Это действие нельзя отменить.")
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Удалить") { _, _ ->
                deleteReaction()
            }
            .show()
    }
    
    private fun deleteReaction() {
        try {
            viewModel.deleteReaction(args.reactionId)
            Snackbar.make(binding.root, "Реакция успешно удалена", Snackbar.LENGTH_SHORT).show()
            findNavController().navigateUp()
        } catch (e: Exception) {
            Timber.e(e, "Error deleting reaction")
            showError("Ошибка при удалении реакции")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 