package com.example.gamevault

import com.example.gamevault.data.model.GameDetail
import com.example.gamevault.data.model.GameDto
import com.example.gamevault.data.model.GameItem
import com.example.gamevault.data.model.GamePageResult
import com.example.gamevault.data.model.GameScreenshot
import com.example.gamevault.data.model.GenreDto
import com.example.gamevault.data.model.PlatformDto
import com.example.gamevault.data.model.PlatformWrapperDto
import com.example.gamevault.data.model.toDomain
import com.example.gamevault.data.repository.GameRepository
import com.example.gamevault.ui.explore.ExploreCategory
import com.example.gamevault.ui.explore.ExploreViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ExploreViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `GameDto toDomain maps fields and applies fallbacks`() {
        val dto = GameDto(
            id = 3498,
            slug = "grand-theft-auto-v",
            name = "Grand Theft Auto V",
            released = null,
            backgroundImage = "https://example.com/gtav.jpg",
            rating = 4.47,
            ratingsCount = 6500,
            metacritic = 92,
            playtime = 74,
            genres = listOf(GenreDto(1, "Action", "action")),
            parentPlatforms = listOf(PlatformWrapperDto(PlatformDto(1, "PC", "pc")))
        )

        val domain = dto.toDomain()

        assertEquals(3498, domain.id)
        assertEquals("Grand Theft Auto V", domain.name)
        assertEquals("TBA", domain.releasedDate)
        assertEquals(92, domain.metacriticScore)
        assertEquals(listOf("Action"), domain.genres)
        assertEquals(listOf("PC"), domain.platforms)
    }

    @Test
    fun `initial load populates games and pagination state`() = runTest(testDispatcher) {
        val fakeRepo = FakeGameRepository(
            pages = mapOf(
                1 to Result.success(
                    GamePageResult(
                        games = listOf(sampleGame(1, "Elden Ring"), sampleGame(2, "Hades")),
                        totalCount = 40,
                        hasNextPage = true
                    )
                )
            )
        )

        val viewModel = ExploreViewModel(repository = fakeRepo)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
        assertEquals(2, state.games.size)
        assertEquals("Elden Ring", state.games.first().name)
        assertTrue(state.hasNextPage)
        assertEquals(ExploreCategory.POPULAR.ordering, fakeRepo.lastOrdering)
    }

    @Test
    fun `loadNextPage appends deduplicated items`() = runTest(testDispatcher) {
        val fakeRepo = FakeGameRepository(
            pages = mapOf(
                1 to Result.success(
                    GamePageResult(
                        games = listOf(sampleGame(1, "Elden Ring"), sampleGame(2, "Hades")),
                        totalCount = 3,
                        hasNextPage = true
                    )
                ),
                2 to Result.success(
                    GamePageResult(
                        games = listOf(sampleGame(2, "Hades"), sampleGame(3, "Celeste")),
                        totalCount = 3,
                        hasNextPage = false
                    )
                )
            )
        )

        val viewModel = ExploreViewModel(repository = fakeRepo)
        advanceUntilIdle()

        viewModel.loadNextPage()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(listOf(1, 2, 3), state.games.map { it.id })
        assertEquals(2, state.currentPage)
        assertFalse(state.hasNextPage)
    }

    @Test
    fun `debounced search queries repository with trimmed keyword`() = runTest(testDispatcher) {
        val fakeRepo = FakeGameRepository(
            pages = mapOf(
                1 to Result.success(
                    GamePageResult(
                        games = listOf(sampleGame(10, "The Witcher 3")),
                        totalCount = 1,
                        hasNextPage = false
                    )
                )
            )
        )

        val viewModel = ExploreViewModel(repository = fakeRepo, autoLoad = false)
        viewModel.onSearchQueryChange("  witcher  ")
        advanceTimeBy(450)
        advanceUntilIdle()

        assertEquals("witcher", fakeRepo.lastSearch)
        assertEquals(1, viewModel.uiState.value.games.size)
    }

    @Test
    fun `error state surfaces message and clears on refresh`() = runTest(testDispatcher) {
        val fakeRepo = FakeGameRepository(
            pages = mapOf(
                1 to Result.failure(IllegalStateException("Network unavailable"))
            )
        )

        val viewModel = ExploreViewModel(repository = fakeRepo)
        advanceUntilIdle()

        assertEquals("Network unavailable", viewModel.uiState.value.errorMessage)
        assertTrue(viewModel.uiState.value.games.isEmpty())

        fakeRepo.pages = mapOf(
            1 to Result.success(
                GamePageResult(
                    games = listOf(sampleGame(99, "Cyberpunk 2077")),
                    totalCount = 1,
                    hasNextPage = false
                )
            )
        )

        viewModel.refresh()
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.errorMessage)
        assertEquals(1, viewModel.uiState.value.games.size)
    }

    private fun sampleGame(id: Int, name: String) = GameItem(
        id = id,
        slug = name.lowercase().replace(" ", "-"),
        name = name,
        releasedDate = "2024-01-01",
        coverImageUrl = "https://example.com/$id.jpg",
        rating = 4.5,
        ratingsCount = 1000,
        metacriticScore = 90,
        playtimeHours = 30,
        genres = listOf("RPG"),
        platforms = listOf("PC"),
        previewScreenshots = emptyList()
    )

    private class FakeGameRepository(
        var pages: Map<Int, Result<GamePageResult>>
    ) : GameRepository {
        var lastSearch: String? = null
        var lastOrdering: String? = null

        override suspend fun getGames(
            page: Int,
            pageSize: Int,
            search: String?,
            ordering: String?,
            genres: String?,
            parentPlatforms: String?,
            dates: String?
        ): Result<GamePageResult> {
            lastSearch = search
            lastOrdering = ordering
            return pages[page] ?: Result.success(GamePageResult(emptyList(), 0, false))
        }

        override suspend fun getGameDetail(gameId: Int): Result<GameDetail> =
            Result.failure(UnsupportedOperationException("Not used in Explore tests"))

        override suspend fun getGameScreenshots(gameId: Int): Result<List<GameScreenshot>> =
            Result.success(emptyList())
    }
}
