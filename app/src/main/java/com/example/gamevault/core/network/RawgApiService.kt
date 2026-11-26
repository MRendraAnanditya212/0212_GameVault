package com.example.gamevault.core.network

import com.example.gamevault.data.model.GameDetailDto
import com.example.gamevault.data.model.RawgGamesResponse
import com.example.gamevault.data.model.ScreenshotResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RawgApiService {
    @GET("games")
    suspend fun getGames(
        @Query("key") apiKey: String,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20,
        @Query("search") search: String? = null,
        @Query("genres") genres: String? = null,
        @Query("parent_platforms") parentPlatforms: String? = null,
        @Query("ordering") ordering: String? = "-added",
        @Query("dates") dates: String? = null
    ): RawgGamesResponse

    @GET("games/{id}")
    suspend fun getGameDetail(
        @Path("id") gameId: Int,
        @Query("key") apiKey: String
    ): GameDetailDto

    @GET("games/{id}/screenshots")
    suspend fun getGameScreenshots(
        @Path("id") gameId: Int,
        @Query("key") apiKey: String
    ): ScreenshotResponse
}
