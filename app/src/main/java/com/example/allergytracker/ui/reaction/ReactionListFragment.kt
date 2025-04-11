package com.example.allergytracker.ui.reaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.allergytracker.R
import com.example.allergytracker.databinding.FragmentReactionListBinding
import com.example.allergytracker.domain.model.Reaction
import com.example.allergytracker.ui.common.UiState
import com.example.allergytracker.ui.reaction.ReactionViewModel.FilterPeriod
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class ReactionListFragment : Fragment() {

    private var _binding: FragmentReactionListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ReactionViewModel by viewModels()
    private lateinit var reactionAdapter: ReactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReactionListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearchView()
        setupFilterChips()
        setupFab()
        observeViewModelState()
    }

    private fun setupRecyclerView() {
        reactionAdapter = ReactionAdapter(
            onItemClick = { reaction ->
                navigateToReactionDetails(reaction)
            },
            onLongClick = { reaction ->
                showDeleteDialog(reaction)
            }
        )

        binding.recyclerViewReactions.apply {
            adapter = reactionAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.setSearchQuery(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { viewModel.setSearchQuery(it) }
                return true
            }
        })
    }

    private fun setupFilterChips() {
        binding.chipGroupFilter.setOnCheckedStateChangeListener { _, checkedIds ->
            val period = when {
                checkedIds.contains(R.id.chipToday) -> FilterPeriod.TODAY
                checkedIds.contains(R.id.chipWeek) -> FilterPeriod.WEEK
                checkedIds.contains(R.id.chipMonth) -> FilterPeriod.MONTH
                else -> FilterPeriod.ALL
            }
            viewModel.setFilterPeriod(period)
        }
    }

    private fun setupFab() {
        binding.fabAddReaction.setOnClickListener {
            navigateToAddReaction()
        }
    }

    private fun observeViewModelState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Наблюдаем за состоянием списка реакций
                launch {
                    viewModel.reactionsState.collectLatest { state ->
                        when (state) {
                            is UiState.Loading -> showLoading(true)
                            is UiState.Success -> {
                                showLoading(false)
                                showReactions(state.data)
                            }
                            is UiState.Error -> {
                                showLoading(false)
                                showError(state.message)
                            }
                        }
                    }
                }

                // Наблюдаем за периодом фильтрации
                launch {
                    viewModel.filterPeriod.collectLatest { period ->
                        updateFilterChips(period)
                    }
                }

                // Наблюдаем за строкой поиска
                launch {
                    viewModel.searchQuery.collectLatest { query ->
                        if (binding.searchView.query.toString() != query) {
                            binding.searchView.setQuery(query, false)
                        }
                    }
                }

                // Наблюдаем за ID аллергии (если фильтруем по определенной аллергии)
                launch {
                    viewModel.currentAllergyId.collectLatest { allergyId ->
                        updateAllergyFilter(allergyId)
                    }
                }
            }
        }
    }

    private fun updateFilterChips(period: FilterPeriod) {
        val chipId = when (period) {
            FilterPeriod.TODAY -> R.id.chipToday
            FilterPeriod.WEEK -> R.id.chipWeek
            FilterPeriod.MONTH -> R.id.chipMonth
            else -> R.id.chipAll
        }
        binding.chipGroupFilter.check(chipId)
    }

    private fun updateAllergyFilter(allergyId: String?) {
        binding.chipAllergyFilter.apply {
            visibility = if (allergyId != null) View.VISIBLE else View.GONE
            setOnCloseIconClickListener {
                viewModel.clearAllergyFilter()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.recyclerViewReactions.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showReactions(reactions: List<Reaction>) {
        reactionAdapter.submitList(reactions)
        binding.textEmptyList.visibility = if (reactions.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    private fun navigateToReactionDetails(reaction: Reaction) {
        try {
            val action = ReactionListFragmentDirections.actionReactionListToReactionDetails(reaction.id)
            findNavController().navigate(action)
        } catch (e: Exception) {
            Timber.e(e, "Error navigating to reaction details with id: ${reaction.id}")
            showError("Ошибка при открытии деталей реакции")
        }
    }

    private fun navigateToAddReaction() {
        try {
            val action = ReactionListFragmentDirections.actionReactionListToAddReaction()
            findNavController().navigate(action)
        } catch (e: Exception) {
            Timber.e(e, "Error navigating to add reaction screen")
            showError("Ошибка при открытии экрана добавления реакции")
        }
    }

    private fun showDeleteDialog(reaction: Reaction) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Удаление записи")
            .setMessage("Вы уверены, что хотите удалить эту запись о реакции? Это действие нельзя отменить.")
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Удалить") { _, _ ->
                deleteReaction(reaction)
            }
            .show()
    }

    private fun deleteReaction(reaction: Reaction) {
        try {
            viewModel.deleteReaction(reaction.id)
            Snackbar.make(
                binding.root,
                "Запись успешно удалена",
                Snackbar.LENGTH_LONG
            ).show()
        } catch (e: Exception) {
            Timber.e(e, "Error deleting reaction: ${reaction.id}")
            showError("Ошибка при удалении записи о реакции")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 