package com.example.allergytracker.ui.recipe

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.allergytracker.databinding.FragmentRecipeGeneratorBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecipeGeneratorFragment : Fragment() {
    private var _binding: FragmentRecipeGeneratorBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RecipeGeneratorViewModel by viewModels()
    private val recipeAdapter = RecipeAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeGeneratorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        binding.recipesList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recipeAdapter
        }
    }

    private fun setupClickListeners() {
        binding.generateButton.setOnClickListener {
            val ingredients = binding.ingredientsInput.editText?.text?.toString()?.trim()
            if (ingredients.isNullOrEmpty()) {
                Snackbar.make(binding.root, "Введите ингредиенты", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.generateRecipes(ingredients)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is RecipeGeneratorUiState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.recipesList.visibility = View.GONE
                    }
                    is RecipeGeneratorUiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.recipesList.visibility = View.VISIBLE
                        recipeAdapter.submitList(state.recipes)
                    }
                    is RecipeGeneratorUiState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
} 