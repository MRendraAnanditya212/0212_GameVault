package com.example.gamevault.ui.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gamevault.data.repository.GameRepository
import com.example.gamevault.data.repository.RawgGameRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class ExploreViewModel(
    private val repository: GameRepository = RawgGameRepository(),
    autoLoad: Boolean = true
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState: StateFlow<ExploreUiState> = _uiState.asStateFlow()

    private var fetchJob: Job? = null
    private var searchDebounceJob: Job? = null

    init {
        if (autoLoad) {
            loadGames(reset = true)
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchDebounceJob?.cancel()
        searchDebounceJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MS)
            loadGames(reset = true)
        }
    }

    fun onSearchSubmit() {
        searchDebounceJob?.cancel()
        loadGames(reset = true)
    }

    fun onClearSearch() {
        if (_uiState.value.searchQuery.isEmpty()) return
        searchDebounceJob?.cancel()
        _uiState.update { it.copy(searchQuery = "") }
        loadGames(reset = true)
    }

    fun onCategorySelected(category: ExploreCategory) {
        if (_uiState.value.selectedCategory == category && _uiState.value.errorMessage == null) return
        searchDebounceJob?.cancel()
        _uiState.update { it.copy(selectedCategory = category) }
        loadGames(reset = true)
    }

    fun refresh() {
        searchDebounceJob?.cancel()
        loadGames(reset = true)
    }

    fun loadNextPage() {
        val state = _uiState.value
        if (state.isLoading || state.isLoadingMore || !state.hasNextPage) return
        loadGames(reset = false)
    }

    private fun loadGames(reset: Boolean) {
        val currentState = _uiState.value
        val targetPage = if (reset) 1 else currentState.currentPage + 1

        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            _uiState.update {
                if (reset) {
                    it.copy(
                        isLoading = true,
                        isLoadingMore = false,
                        errorMessage = null
                    )
                } else {
                    it.copy(
                        isLoadingMore = true,
                        errorMessage = null
                    )
                }
            }

            val trimmedQuery = currentState.searchQuery.trim()
            val dateRange = if (currentState.selectedCategory == ExploreCategory.LATEST && trimmedQuery.isEmpty()) {
                val today = LocalDate.now()
                val startDate = today.minusMonths(18)
                "$startDate,$today"
            } else {
                null
            }

            val result = repository.getGames(
                page = targetPage,
                pageSize = PAGE_SIZE,
                search = trimmedQuery.takeIf { it.isNotEmpty() },
                ordering = if (trimmedQuery.isEmpty()) currentState.selectedCategory.ordering else null,
                dates = dateRange
            )

            result.fold(
                onSuccess = { pageResult ->
                    _uiState.update { state ->
                        val mergedGames = if (reset) {
                            pageResult.games
                        } else {
                            (state.games + pageResult.games).distinctBy { it.id }
                        }
                        state.copy(
                            games = mergedGames,
                            isLoading = false,
                            isLoadingMore = false,
                            errorMessage = null,
                            currentPage = targetPage,
                            hasNextPage = pageResult.hasNextPage,
                            totalCount = pageResult.totalCount
                        )
                    }
                },
                onFailure = { throwable ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            errorMessage = throwable.message ?: "Gagal memuat data katalog game."
                        )
                    }
                }
            )
        }
    }

    companion object {
        private const val PAGE_SIZE = 20
        private const val SEARCH_DEBOUNCE_MS = 400L

        fun factory(
            repository: GameRepository = RawgGameRepository()
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(ExploreViewModel::class.java)) {
                    return ExploreViewModel(repository = repository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}