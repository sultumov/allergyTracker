package com.example.allergytracker.ui.allergy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.allergytracker.databinding.FragmentAllergyDetailsBinding
import com.example.allergytracker.domain.model.Allergy
import com.example.allergytracker.ui.common.BaseDetailFragment
import com.example.allergytracker.ui.common.UiState
import com.example.allergytracker.ui.reaction.ReactionAdapter
import com.example.allergytracker.ui.reaction.ReactionViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class AllergyDetailsFragment : BaseDetailFragment() {
    private var _binding: FragmentAllergyDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AllergyViewModel by viewModels()
    private val reactionViewModel: ReactionViewModel by viewModels()
    private val args: AllergyDetailsFragmentArgs by navArgs()
    private lateinit var reactionAdapter: ReactionAdapter
    
    private var currentAllergy: Allergy? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllergyDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    override fun loadData() {
        viewModel.loadAllergyById(args.allergyId)
        reactionViewModel.loadReactionsByAllergyId(args.allergyId)
        
        setupReactionsList()
    }

    private fun setupReactionsList() {
        reactionAdapter = ReactionAdapter(
            onItemClick = { reaction ->
                navigateToReactionDetails(reaction.id)
            },
            onLongClick = { reaction ->
                showDeleteReactionDialog(reaction.id)
                true
            }
        )
        binding.recyclerViewReactions.apply {
            adapter = reactionAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        
        binding.fabAddReaction.setOnClickListener {
            navigateToAddReaction()
        }
    }

    override fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.allergyState.collectLatest { state ->
                        when (state) {
                            is UiState.Loading -> showLoading(true)
                            is UiState.Success -> {
                                showLoading(false)
                                state.data?.let { 
                                    currentAllergy = it
                                    updateUI(it) 
                                }
                            }
                            is UiState.Error -> {
                                showLoading(false)
                                showError(state.message)
                            }
                        }
                    }
                }
                launch {
                    reactionViewModel.reactionsState.collectLatest { state ->
                        when (state) {
                            is UiState.Loading -> {
                                binding.progressBarReactions.visibility = View.VISIBLE
                            }
                            is UiState.Success -> {
                                binding.progressBarReactions.visibility = View.GONE
                                reactionAdapter.submitList(state.data)
                                binding.textNoReactions.visibility =
                                    if (state.data.isEmpty()) View.VISIBLE else View.GONE
                            }
                            is UiState.Error -> {
                                binding.progressBarReactions.visibility = View.GONE
                                showError("Ошибка загрузки реакций: ${state.message}")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun updateUI(allergy: Allergy) {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        binding.apply {
            textAllergyName.text = allergy.name
            textCategory.text = allergy.category
            textSeverity.text = allergy.severity
            textDescription.text = allergy.description.takeIf { it.isNotEmpty() } ?: "Нет описания"
            textDate.text = dateFormat.format(allergy.createdAt)
            chipActiveStatus.isChecked = allergy.isActive
            chipActiveStatus.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked != allergy.isActive) {
                    toggleAllergyActiveStatus(allergy, isChecked)
                }
            }
            toolbar.title = allergy.name
        }
    }

    override fun getLoadingView(): View? = binding.progressBar
    
    override fun getContentView(): View? = binding.contentLayout

    private fun navigateToReactionDetails(reactionId: String) {
        try {
            val action = AllergyDetailsFragmentDirections.actionAllergyDetailsToReactionDetails(reactionId)
            findNavController().navigate(action)
        } catch (e: Exception) {
            Timber.e(e, "Error navigating to reaction details")
            showError("Ошибка при переходе к деталям реакции")
        }
    }
    
    private fun navigateToAddReaction() {
        try {
            val action = AllergyDetailsFragmentDirections.actionAllergyDetailsToAddReaction(args.allergyId)
            findNavController().navigate(action)
        } catch (e: Exception) {
            Timber.e(e, "Error navigating to add reaction")
            showError("Ошибка при переходе к добавлению реакции")
        }
    }

    override fun navigateToEdit() {
        try {
            val action = AllergyDetailsFragmentDirections.actionAllergyDetailsToEditAllergy(args.allergyId)
            findNavController().navigate(action)
        } catch (e: Exception) {
            Timber.e(e, "Error navigating to edit allergy")
            showError("Ошибка при переходе к редактированию")
        }
    }

    override fun deleteItem() {
        try {
            viewModel.deleteAllergy(args.allergyId)
            showSuccess("Аллергия успешно удалена")
            findNavController().navigateUp()
        } catch (e: Exception) {
            Timber.e(e, "Error deleting allergy")
            showError("Ошибка при удалении аллергии")
        }
    }
    
    override fun getDeleteDialogTitle(): String = "Удаление аллергии"
    
    override fun getDeleteDialogMessage(): String = 
        "Вы уверены, что хотите удалить эту аллергию и все связанные с ней реакции? Это действие нельзя отменить."

    private fun showDeleteReactionDialog(reactionId: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Удаление реакции")
            .setMessage("Вы уверены, что хотите удалить эту запись о реакции? Это действие нельзя отменить.")
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Удалить") { _, _ ->
                deleteReaction(reactionId)
            }
            .show()
    }

    private fun deleteReaction(reactionId: String) {
        try {
            reactionViewModel.deleteReaction(reactionId)
            showSuccess("Реакция успешно удалена")
        } catch (e: Exception) {
            Timber.e(e, "Error deleting reaction")
            showError("Ошибка при удалении реакции")
        }
    }

    private fun toggleAllergyActiveStatus(allergy: Allergy, isActive: Boolean) {
        try {
            val updatedAllergy = allergy.copy(isActive = isActive)
            viewModel.updateAllergy(updatedAllergy)
            
            val statusText = if (isActive) "активна" else "неактивна"
            showSuccess("Аллергия теперь $statusText")
        } catch (e: Exception) {
            Timber.e(e, "Error updating allergy status")
            showError("Ошибка при обновлении статуса аллергии")
            // Возвращаем состояние чипа к исходному
            binding.chipActiveStatus.isChecked = !isActive
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 