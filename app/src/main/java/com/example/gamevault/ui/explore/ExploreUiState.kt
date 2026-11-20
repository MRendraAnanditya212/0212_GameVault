package com.example.gamevault.ui.explore

import com.example.gamevault.data.model.GameItem

enum class ExploreCategory(
    val label: String,
    val ordering: String
) {
    POPULAR(label = "Popular", ordering = "-added"),
    TOP_RATED(label = "Top Rated", ordering = "-metacritic"),
    LATEST(label = "Latest", ordering = "-released")
}

data class ExploreUiState(
    val games: List<GameItem> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: ExploreCategory = ExploreCategory.POPULAR,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val errorMessage: String? = null,
    val currentPage: Int = 1,
    val hasNextPage: Boolean = false,
    val totalCount: Int = 0
) {
    val isEmpty: Boolean
        get() = !isLoading && errorMessage == null && games.isEmpty()
}