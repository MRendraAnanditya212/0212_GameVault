package com.example.gamevault.core.network

import com.example.gamevault.data.model.GameDetailDto
import com.example.gamevault.data.model.GameDto
import com.example.gamevault.data.model.GenreDto
import com.example.gamevault.data.model.RawgListResponse
import com.example.gamevault.data.model.ScreenshotDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RawgApiService {

    @GET("games")
    suspend fun getGames(
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20,
        @Query("search") search: String? = null,
        @Query("ordering") ordering: String? = null,
        @Query("genres") genres: String? = null,
        @Query("parent_platforms") parentPlatforms: String? = null,
        @Query("dates") dates: String? = null
    ): RawgListResponse<GameDto>

    @GET("games/{id}")
    suspend fun getGameDetail(
        @Path("id") gameId: Int
    ): GameDetailDto

    @GET("games/{id}/screenshots")
    suspend fun getGameScreenshots(
        @Path("id") gameId: Int,
        @Query("page") page: Int = 1,
        @Query("page_size") pageSize: Int = 20
    ): RawgListResponse<ScreenshotDto>

    @GET("genres")
    suspend fun getGenres(
        @Query("page_size") pageSize: Int = 20
    ): RawgListResponse<GenreDto>
}