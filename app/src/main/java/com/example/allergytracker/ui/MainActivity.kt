package com.example.allergytracker.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.allergytracker.R
import com.example.allergytracker.databinding.ActivityMainBinding
import com.example.allergytracker.ui.adapter.AllergyRecordAdapter
import com.example.allergytracker.ui.state.UiState
import com.example.allergytracker.viewmodel.AllergyViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: AllergyViewModel
    private lateinit var adapter: AllergyRecordAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[AllergyViewModel::class.java]
        adapter = AllergyRecordAdapter { record ->
            viewModel.deleteRecord(record)
            showUndoSnackbar(record)
        }

        setupRecyclerView()
        setupSearch()
        setupFilters()
        observeViewModel()

        binding.btnAddRecord.setOnClickListener {
            startActivity(Intent(this, AddAllergyActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    private fun setupRecyclerView() {
        binding.rvRecords.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
            layoutAnimation = AnimationUtils.loadLayoutAnimation(
                this@MainActivity,
                R.anim.item_animation
            )
        }
    }

    private fun setupSearch() {
        binding.searchBar.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.setSearchQuery(newText ?: "")
                return true
            }
        })
    }

    private fun setupFilters() {
        binding.filterChipGroup.setOnCheckedChangeListener { group, checkedId ->
            val filterType = when (checkedId) {
                R.id.chipAll -> AllergyViewModel.FilterType.ALL
                R.id.chipToday -> AllergyViewModel.FilterType.TODAY
                R.id.chipWeek -> AllergyViewModel.FilterType.WEEK
                R.id.chipMonth -> AllergyViewModel.FilterType.MONTH
                else -> AllergyViewModel.FilterType.ALL
            }
            viewModel.setFilterType(filterType)
        }
    }

    private fun observeViewModel() {
        viewModel.recordsState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.rvRecords.visibility = View.GONE
                    binding.textEmpty.visibility = View.GONE
                }
                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.rvRecords.visibility = View.VISIBLE
                    binding.textEmpty.visibility = View.GONE
                    adapter.submitList(state.data)
                    binding.rvRecords.scheduleLayoutAnimation()
                }
                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.rvRecords.visibility = View.GONE
                    binding.textEmpty.visibility = View.VISIBLE
                    binding.textEmpty.text = state.message
                    Toast.makeText(this, state.message, Toast.LENGTH_LONG).show()
                }
                is UiState.Empty -> {
                    binding.progressBar.visibility = View.GONE
                    binding.rvRecords.visibility = View.GONE
                    binding.textEmpty.visibility = View.VISIBLE
                    binding.textEmpty.text = getString(R.string.no_records)
                }
            }
        }
    }

    private fun showUndoSnackbar(record: AllergyRecord) {
        Snackbar.make(
            binding.rvRecords,
            "Запись удалена",
            Snackbar.LENGTH_LONG
        ).setAction("Отменить") {
            viewModel.addRecord(record)
        }.show()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}