package com.example.allergytracker.ui.history

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.allergytracker.R
import com.example.allergytracker.data.model.AllergyRecord
import com.example.allergytracker.databinding.ActivityHistoryBinding
import com.example.allergytracker.ui.adapter.AllergyRecordAdapter
import com.example.allergytracker.ui.state.UiState
import com.example.allergytracker.ui.history.HistoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.google.android.material.snackbar.Snackbar

@AndroidEntryPoint
class AllergyHistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private val viewModel: HistoryViewModel by viewModels()
    private lateinit var adapter: AllergyRecordAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = AllergyRecordAdapter(
            onDeleteClick = { record ->
                viewModel.deleteRecord(record)
                showUndoSnackbar(record)
            }
        )

        binding.historyRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@AllergyHistoryActivity)
            adapter = this@AllergyHistoryActivity.adapter
            setHasFixedSize(true)
        }
    }

    private fun observeViewModel() {
        viewModel.historyState.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    showLoadingState()
                }
                is UiState.Success<List<AllergyRecord>> -> {
                    showSuccessState(state.data)
                }
                is UiState.Error -> {
                    showErrorState(state.message)
                }
                is UiState.Empty -> {
                    showEmptyState()
                }
            }
        }
    }

    private fun showLoadingState() {
        binding.apply {
            historyRecyclerView.visibility = View.GONE
            emptyHistoryText.visibility = View.GONE
        }
    }

    private fun showSuccessState(records: List<AllergyRecord>) {
        binding.apply {
            historyRecyclerView.visibility = View.VISIBLE
            emptyHistoryText.visibility = View.GONE
        }
        adapter.submitList(records)
    }

    private fun showErrorState(message: String) {
        binding.apply {
            historyRecyclerView.visibility = View.GONE
            emptyHistoryText.visibility = View.VISIBLE
            emptyHistoryText.text = message
        }
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    private fun showEmptyState() {
        binding.apply {
            historyRecyclerView.visibility = View.GONE
            emptyHistoryText.visibility = View.VISIBLE
            emptyHistoryText.text = getString(R.string.no_history)
        }
    }

    private fun showUndoSnackbar(record: AllergyRecord) {
        Snackbar.make(
            binding.historyRecyclerView,
            getString(R.string.record_deleted),
            Snackbar.LENGTH_LONG
        ).setAction(getString(R.string.undo)) {
            viewModel.addRecord(record)
        }.show()
    }
} 