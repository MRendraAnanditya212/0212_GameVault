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

class ExploreViewModel(
    private val repository: GameRepository = RawgGameRepository(),
    autoLoad: Boolean = true
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState: StateFlow<ExploreUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        if (autoLoad) {
            loadGames(page = 1)
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300L) // Debounce 300ms (REQ-9 & NFR-2)
            loadGames(page = 1)
        }
    }

    fun onSearchQueryChanged(query: String) = onSearchQueryChange(query)

    fun onSearchSubmit() {
        searchJob?.cancel()
        loadGames(page = 1)
    }

    fun onClearSearch() {
        _uiState.update { it.copy(searchQuery = "") }
        searchJob?.cancel()
        loadGames(page = 1)
    }

    fun onCategorySelected(category: ExploreCategory) {
        _uiState.update {
            it.copy(
                selectedCategory = category,
                selectedOrdering = category.ordering
            )
        }
        loadGames(page = 1)
    }

    fun onGenreSelected(genreSlug: String?) {
        val next = if (_uiState.value.selectedGenre == genreSlug) null else genreSlug
        _uiState.update { it.copy(selectedGenre = next) }
        loadGames(page = 1)
    }

    fun onPlatformSelected(platformId: String?) {
        val next = if (_uiState.value.selectedPlatform == platformId) null else platformId
        _uiState.update { it.copy(selectedPlatform = next) }
        loadGames(page = 1)
    }

    fun onOrderingSelected(ordering: String) {
        _uiState.update { it.copy(selectedOrdering = ordering) }
        loadGames(page = 1)
    }

    fun refresh() {
        loadGames(page = 1)
    }

    fun loadNextPage() {
        val current = _uiState.value
        if (current.isLoading || current.isLoadingMore || !current.hasNextPage) return
        loadGames(page = current.currentPage + 1)
    }

    fun loadGames(page: Int = 1) {
        val current = _uiState.value
        val isFirstPage = page == 1

        viewModelScope.launch {
            if (isFirstPage) {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            } else {
                _uiState.update { it.copy(isLoadingMore = true, errorMessage = null) }
            }

            val searchParam = current.searchQuery.trim().takeIf { it.isNotEmpty() }
            val orderingParam = current.selectedCategory.ordering

            val result = repository.getGames(
                page = page,
                pageSize = 20,
                search = searchParam,
                ordering = orderingParam,
                genres = current.selectedGenre,
                parentPlatforms = current.selectedPlatform
            )

            result.onSuccess { pageResult ->
                _uiState.update { state ->
                    val updatedGames = if (isFirstPage) {
                        pageResult.games
                    } else {
                        (state.games + pageResult.games).distinctBy { it.id }
                    }
                    state.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        games = updatedGames,
                        currentPage = page,
                        hasNextPage = pageResult.hasNextPage,
                        totalCount = pageResult.totalCount,
                        errorMessage = null
                    )
                }
            }.onFailure { error ->
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        errorMessage = error.message ?: "Gagal memuat game"
                    )
                }
            }
        }
    }

    companion object {
        fun factory(repository: GameRepository = RawgGameRepository()): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ExploreViewModel(repository) as T
                }
            }
    }
}
