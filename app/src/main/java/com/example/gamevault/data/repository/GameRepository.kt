package com.example.gamevault.data.repository

import com.example.gamevault.BuildConfig
import com.example.gamevault.core.network.RawgApiService
import com.example.gamevault.core.network.RetrofitClient
import com.example.gamevault.data.model.GameDetail
import com.example.gamevault.data.model.GamePageResult
import com.example.gamevault.data.model.GameScreenshot
import com.example.gamevault.data.model.toDomain
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException

interface GameRepository {
    suspend fun getGames(
        page: Int = 1,
        pageSize: Int = 20,
        search: String? = null,
        ordering: String? = null,
        genres: String? = null,
        parentPlatforms: String? = null,
        dates: String? = null
    ): Result<GamePageResult>

    suspend fun getGameDetail(gameId: Int): Result<GameDetail>

    suspend fun getGameScreenshots(gameId: Int): Result<List<GameScreenshot>>
}

class RawgGameRepository(
    private val apiService: RawgApiService = RetrofitClient.rawgApiService,
    private val apiKeyProvider: () -> String = { BuildConfig.RAWG_API_KEY }
) : GameRepository {

    override suspend fun getGames(
        page: Int,
        pageSize: Int,
        search: String?,
        ordering: String?,
        genres: String?,
        parentPlatforms: String?,
        dates: String?
    ): Result<GamePageResult> = safeApiCall {
        val response = apiService.getGames(
            apiKey = apiKeyProvider(),
            page = page,
            pageSize = pageSize,
            search = search?.trim()?.takeIf { it.isNotEmpty() },
            ordering = ordering,
            genres = genres,
            parentPlatforms = parentPlatforms,
            dates = dates
        )
        GamePageResult(
            games = response.results.map { it.toDomain() },
            totalCount = response.count,
            hasNextPage = response.next != null
        )
    }

    override suspend fun getGameDetail(gameId: Int): Result<GameDetail> = safeApiCall {
        apiService.getGameDetail(gameId, apiKeyProvider()).toDomain()
    }

    override suspend fun getGameScreenshots(gameId: Int): Result<List<GameScreenshot>> = safeApiCall {
        apiService.getGameScreenshots(gameId, apiKeyProvider()).results.map { it.toDomain() }
    }

    private suspend fun <T> safeApiCall(block: suspend () -> T): Result<T> {
        if (apiKeyProvider().isBlank()) {
            return Result.failure(
                IllegalStateException(
                    "RAWG API key belum dikonfigurasi. Tambahkan RAWG_API_KEY=<key_anda> di local.properties lalu sync Gradle."
                )
            )
        }

        return try {
            Result.success(block())
        } catch (ce: CancellationException) {
            throw ce
        } catch (e: HttpException) {
            val message = when (e.code()) {
                401, 403 -> "RAWG API key tidak valid atau akses ditolak (${e.code()})."
                404 -> "Data game tidak ditemukan pada RAWG API."
                429 -> "Batas permintaan RAWG API tercapai. Coba lagi beberapa saat."
                in 500..599 -> "Server RAWG API sedang mengalami gangguan (${e.code()})."
                else -> "Permintaan ke RAWG API gagal (HTTP ${e.code()})."
            }
            Result.failure(IllegalStateException(message, e))
        } catch (e: IOException) {
            Result.failure(
                IOException("Gagal terhubung ke jaringan. Periksa koneksi internet Anda.", e)
            )
        } catch (e: Exception) {
            Result.failure(
                IllegalStateException(e.message ?: "Terjadi kesalahan saat memuat katalog game.", e)
            )
        }
    }
}
