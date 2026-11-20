package com.example.gamevault.data.model

import com.google.gson.annotations.SerializedName

data class RawgListResponse<T>(
    @SerializedName("count") val count: Int = 0,
    @SerializedName("next") val next: String? = null,
    @SerializedName("previous") val previous: String? = null,
    @SerializedName("results") val results: List<T> = emptyList()
)

data class GameDto(
    @SerializedName("id") val id: Int,
    @SerializedName("slug") val slug: String = "",
    @SerializedName("name") val name: String = "",
    @SerializedName("released") val released: String? = null,
    @SerializedName("background_image") val backgroundImage: String? = null,
    @SerializedName("rating") val rating: Double = 0.0,
    @SerializedName("rating_top") val ratingTop: Int = 5,
    @SerializedName("ratings_count") val ratingsCount: Int = 0,
    @SerializedName("metacritic") val metacritic: Int? = null,
    @SerializedName("playtime") val playtime: Int = 0,
    @SerializedName("genres") val genres: List<GenreDto>? = null,
    @SerializedName("parent_platforms") val parentPlatforms: List<PlatformWrapperDto>? = null,
    @SerializedName("short_screenshots") val shortScreenshots: List<ScreenshotDto>? = null
)

data class GameDetailDto(
    @SerializedName("id") val id: Int,
    @SerializedName("slug") val slug: String = "",
    @SerializedName("name") val name: String = "",
    @SerializedName("description_raw") val descriptionRaw: String? = null,
    @SerializedName("released") val released: String? = null,
    @SerializedName("background_image") val backgroundImage: String? = null,
    @SerializedName("background_image_additional") val backgroundImageAdditional: String? = null,
    @SerializedName("website") val website: String? = null,
    @SerializedName("rating") val rating: Double = 0.0,
    @SerializedName("ratings_count") val ratingsCount: Int = 0,
    @SerializedName("metacritic") val metacritic: Int? = null,
    @SerializedName("playtime") val playtime: Int = 0,
    @SerializedName("genres") val genres: List<GenreDto>? = null,
    @SerializedName("parent_platforms") val parentPlatforms: List<PlatformWrapperDto>? = null,
    @SerializedName("developers") val developers: List<DeveloperDto>? = null,
    @SerializedName("publishers") val publishers: List<PublisherDto>? = null,
    @SerializedName("esrb_rating") val esrbRating: EsrbRatingDto? = null
)

data class GenreDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String = "",
    @SerializedName("slug") val slug: String = ""
)

data class PlatformWrapperDto(
    @SerializedName("platform") val platform: PlatformDto? = null
)

data class PlatformDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String = "",
    @SerializedName("slug") val slug: String = ""
)

data class ScreenshotDto(
    @SerializedName("id") val id: Int,
    @SerializedName("image") val image: String = "",
    @SerializedName("width") val width: Int? = null,
    @SerializedName("height") val height: Int? = null
)

data class DeveloperDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String = ""
)

data class PublisherDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String = ""
)

data class EsrbRatingDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String = ""
)