package com.example.allergytracker.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.allergytracker.data.model.AllergyRecord
import com.example.allergytracker.ui.state.UiState
import com.example.allergytracker.viewmodel.AllergyViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

class MainActivityTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var viewModel: AllergyViewModel

    private lateinit var activity: MainActivity

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        activity = MainActivity()
        activity.viewModel = viewModel
    }

    @Test
    fun `when search query changes, viewModel should be updated`() {
        val query = "test query"
        activity.setupSearch()
        activity.binding.searchBar.setQuery(query, true)
        verify(viewModel).setSearchQuery(query)
    }

    @Test
    fun `when filter changes, viewModel should be updated`() {
        activity.setupFilters()
        activity.binding.filterChipGroup.check(R.id.chipToday)
        verify(viewModel).setFilterType(AllergyViewModel.FilterType.TODAY)
    }

    @Test
    fun `when records are loaded, adapter should be updated`() {
        val records = listOf(
            AllergyRecord(
                date = "2024-04-10 12:00",
                symptoms = "Test symptoms",
                triggers = "Test triggers"
            )
        )
        whenever(viewModel.recordsState).thenReturn(MutableLiveData(UiState.Success(records)))
        activity.observeViewModel()
        verify(activity.adapter).submitList(records)
    }
} 