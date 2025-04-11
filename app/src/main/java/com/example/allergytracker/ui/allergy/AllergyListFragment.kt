package com.example.allergytracker.ui.allergy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.allergytracker.R
import com.example.allergytracker.databinding.FragmentAllergyListBinding
import com.example.allergytracker.domain.model.Allergy
import com.example.allergytracker.ui.common.BaseListFragment
import com.example.allergytracker.ui.common.UiState
import com.example.allergytracker.ui.navigation.NavigationManager
import com.example.allergytracker.util.AnimationUtils
import com.example.allergytracker.util.PreferenceManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class AllergyListFragment : BaseListFragment() {

    private var _binding: FragmentAllergyListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AllergyViewModel by viewModels()
    private lateinit var allergyAdapter: AllergyAdapter
    
    @Inject
    lateinit var preferenceManager: PreferenceManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllergyListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun setupToolbar() {
        // Если нужна специальная настройка тулбара
    }

    override fun setupRecyclerView() {
        allergyAdapter = AllergyAdapter(
            onItemClick = { allergy ->
                NavigationManager.navigateToAllergyDetails(this, allergy.id)
            },
            onLongClick = { allergy ->
                showDeleteDialog(allergy)
                true
            }
        )

        binding.recyclerViewAllergies.apply {
            adapter = allergyAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun setupFab() {
        binding.fabAddAllergy.setOnClickListener {
            NavigationManager.navigateToAddAllergy(this)
        }
    }
    
    override fun setupFilters() {
        // Загружаем состояние фильтра из настроек
        val savedShowOnlyActive = preferenceManager.getBoolean(
            PreferenceManager.Keys.SHOW_ACTIVE_ALLERGIES_ONLY, 
            true
        )
        binding.chipActive.isChecked = savedShowOnlyActive
        viewModel.setShowOnlyActive(savedShowOnlyActive)
        
        binding.chipGroupActiveStatus.setOnCheckedStateChangeListener { _, checkedIds ->
            val showOnlyActive = checkedIds.contains(R.id.chipActive)
            viewModel.setShowOnlyActive(showOnlyActive)
            // Сохраняем состояние фильтра в настройки
            preferenceManager.putBoolean(
                PreferenceManager.Keys.SHOW_ACTIVE_ALLERGIES_ONLY, 
                showOnlyActive
            )
        }
    }
    
    override fun setupSearch() {
        // Загружаем последний поисковый запрос из настроек
        val lastQuery = preferenceManager.getString(PreferenceManager.Keys.LAST_SEARCH_QUERY)
        if (lastQuery.isNotEmpty()) {
            binding.searchView.setQuery(lastQuery, false)
            allergyAdapter.filter.filter(lastQuery)
        }
        
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                saveSearchQuery(query.orEmpty())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                allergyAdapter.filter.filter(newText)
                saveSearchQuery(newText.orEmpty())
                return true
            }
        })
    }
    
    private fun saveSearchQuery(query: String) {
        preferenceManager.putString(PreferenceManager.Keys.LAST_SEARCH_QUERY, query)
    }
    
    override fun performSearch(query: String) {
        allergyAdapter.filter.filter(query)
        saveSearchQuery(query)
    }
    
    override fun getRecyclerView(): RecyclerView? = binding.recyclerViewAllergies
    
    override fun getFab(): FloatingActionButton? = binding.fabAddAllergy
    
    override fun getSearchView(): SearchView? = binding.searchView
    
    override fun getLoadingView(): View? = binding.progressBar
    
    override fun getEmptyView(): View? = binding.textEmptyList

    override fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Наблюдаем за состоянием списка аллергий
                launch {
                    viewModel.allergiesState.collectLatest { state ->
                        when (state) {
                            is UiState.Loading -> showLoading(true)
                            is UiState.Success -> {
                                AnimationUtils.crossFade(binding.recyclerViewAllergies, binding.progressBar)
                                showAllergies(state.data)
                            }
                            is UiState.Error -> {
                                showLoading(false)
                                showError(state.message)
                            }
                            else -> showLoading(false)
                        }
                    }
                }

                // Наблюдаем за статусом фильтра "только активные"
                launch {
                    viewModel.showOnlyActive.collectLatest { showOnlyActive ->
                        if (binding.chipActive.isChecked != showOnlyActive) {
                            binding.chipActive.isChecked = showOnlyActive
                        }
                    }
                }
            }
        }
    }

    private fun showAllergies(allergies: List<Allergy>) {
        allergyAdapter.submitList(allergies)
        allergyAdapter.showEmptyView(binding.textEmptyList, binding.recyclerViewAllergies)
    }

    private fun showDeleteDialog(allergy: Allergy) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Удаление аллергии")
            .setMessage("Вы уверены, что хотите удалить '${allergy.name}'? Это действие нельзя отменить.")
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Удалить") { _, _ ->
                deleteAllergy(allergy)
            }
            .show()
    }

    private fun deleteAllergy(allergy: Allergy) {
        try {
            viewModel.deleteAllergy(allergy.id)
            showSuccess("Аллергия '${allergy.name}' удалена")
        } catch (e: Exception) {
            Timber.e(e, "Error deleting allergy: ${allergy.id}")
            showError("Ошибка при удалении аллергии")
        }
    }
    
    private fun showSuccess(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 