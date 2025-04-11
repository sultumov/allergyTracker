package com.example.allergytracker.ui.allergy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.allergytracker.data.model.Reaction
import com.example.allergytracker.databinding.FragmentReactionDetailsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class ReactionDetailsFragment : Fragment() {
    private var _binding: FragmentReactionDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ReactionDetailsViewModel by viewModels()
    private val args: ReactionDetailsFragmentArgs by navArgs()
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

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
        setupClickListeners()
        observeViewModel()
        viewModel.loadReaction(args.reactionId)
    }

    private fun setupClickListeners() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.deleteButton.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.reaction.collect { reaction: Reaction? ->
                reaction?.let { updateUI(it) }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is ReactionDetailsUiState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is ReactionDetailsUiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                    }
                    is ReactionDetailsUiState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun updateUI(reaction: Reaction) {
        binding.textSeverity.text = reaction.severity
        binding.textSymptoms.text = reaction.symptoms
        binding.textNotes.text = reaction.notes ?: "Нет заметок"
        binding.textDate.text = dateFormat.format(reaction.date)
    }

    private fun showDeleteConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Удалить запись?")
            .setMessage("Вы уверены, что хотите удалить эту запись? Это действие нельзя отменить.")
            .setPositiveButton("Удалить") { _, _ ->
                viewModel.deleteReaction(args.reactionId)
                findNavController().navigateUp()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 