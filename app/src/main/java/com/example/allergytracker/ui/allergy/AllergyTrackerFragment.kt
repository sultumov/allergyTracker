package com.example.allergytracker.ui.allergy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.allergytracker.R
import com.example.allergytracker.databinding.FragmentAllergyTrackerBinding
import com.example.allergytracker.ui.adapter.ReactionAdapter
import com.example.allergytracker.ui.viewmodel.AllergyTrackerViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AllergyTrackerFragment : Fragment() {
    private var _binding: FragmentAllergyTrackerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AllergyTrackerViewModel by viewModels()
    private lateinit var allergyAdapter: AllergyAdapter
    private lateinit var reactionAdapter: ReactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllergyTrackerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViews()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupRecyclerViews() {
        allergyAdapter = AllergyAdapter(
            onItemClick = { allergy ->
                try {
                    findNavController().navigate(
                        AllergyTrackerFragmentDirections.actionAllergyTrackerToAllergyDetails(allergy.id)
                    )
                } catch (e: Exception) {
                    showError("Ошибка навигации: ${e.message}")
                }
            },
            onItemLongClick = { allergy ->
                // Здесь можно добавить контекстное меню для редактирования/удаления
                showError("Долгое нажатие на аллергию: ${allergy.name}")
                true
            }
        )

        reactionAdapter = ReactionAdapter { reaction ->
            try {
                findNavController().navigate(
                    AllergyTrackerFragmentDirections.actionAllergyTrackerToReactionDetails(reaction.id)
                )
            } catch (e: Exception) {
                showError("Ошибка навигации: ${e.message}")
            }
        }

        binding.rvAllergies.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = allergyAdapter
        }

        binding.rvReactions.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = reactionAdapter
        }
    }

    private fun setupClickListeners() {
        binding.fabAddAllergy.setOnClickListener {
            try {
                findNavController().navigate(R.id.action_allergyTracker_to_addAllergy)
            } catch (e: Exception) {
                showError("Ошибка навигации: ${e.message}")
            }
        }

        binding.fabAddReaction.setOnClickListener {
            try {
                findNavController().navigate(R.id.action_allergyTracker_to_addReaction)
            } catch (e: Exception) {
                showError("Ошибка навигации: ${e.message}")
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collectLatest { isLoading ->
                updateLoadingState(isLoading)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allergies.collectLatest { allergies ->
                allergyAdapter.submitList(allergies)
                updateEmptyState(allergies.isEmpty())
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.reactions.collectLatest { reactions ->
                reactionAdapter.submitList(reactions)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.error.collectLatest { error ->
                error?.let {
                    showError(it)
                    viewModel.clearError()
                }
            }
        }
    }

    private fun updateLoadingState(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.rvAllergies.visibility = if (isLoading) View.GONE else View.VISIBLE
        binding.rvReactions.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        binding.tvEmptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        allergyAdapter.submitList(emptyList())
        reactionAdapter.submitList(emptyList())
    }
} 