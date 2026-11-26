package com.example.gamevault.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamevault.data.model.GameDetail
import com.example.gamevault.data.repository.GameRepository
import com.example.gamevault.data.repository.RawgGameRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GameDetailUiState(
    val isLoading: Boolean = true,
    val detail: GameDetail? = null,
    val screenshots: List<String> = emptyList(),
    val errorMessage: String? = null
)

class GameDetailViewModel(
    private val repository: GameRepository = RawgGameRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameDetailUiState())
    val uiState: StateFlow<GameDetailUiState> = _uiState.asStateFlow()

    fun loadGameDetail(gameId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val detailDeferred = async { repository.getGameDetail(gameId) }
                val screenshotsDeferred = async { repository.getGameScreenshots(gameId) }

                val detailResult = detailDeferred.await()
                val screenshotsResult = screenshotsDeferred.await()

                detailResult.onSuccess { gameDetail ->
                    val galleryUrls = buildList {
                        gameDetail.coverImageUrl?.let { add(it) }
                        gameDetail.additionalImageUrl?.let { add(it) }
                        screenshotsResult.getOrNull()?.let { list ->
                            addAll(list.map { it.imageUrl })
                        }
                    }.distinct()

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            detail = gameDetail,
                            screenshots = galleryUrls
                        )
                    }
                }.onFailure { err ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = err.message ?: "Detail game tidak ditemukan")
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Gagal memuat detail: ${e.localizedMessage}")
                }
            }
        }
    }
}
