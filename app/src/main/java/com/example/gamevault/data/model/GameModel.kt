package com.example.gamevault.data.model

data class GameItem(
    val id: Int,
    val slug: String,
    val name: String,
    val releasedDate: String,
    val coverImageUrl: String?,
    val rating: Double,
    val ratingsCount: Int,
    val metacriticScore: Int?,
    val playtimeHours: Int,
    val genres: List<String>,
    val platforms: List<String>,
    val previewScreenshots: List<String>
)

data class GameDetail(
    val id: Int,
    val slug: String,
    val name: String,
    val description: String,
    val releasedDate: String,
    val coverImageUrl: String?,
    val additionalImageUrl: String?,
    val website: String?,
    val rating: Double,
    val ratingsCount: Int,
    val metacriticScore: Int?,
    val playtimeHours: Int,
    val genres: List<String>,
    val platforms: List<String>,
    val developers: List<String>,
    val publishers: List<String>,
    val esrbRating: String?
)

data class GameScreenshot(
    val id: Int,
    val imageUrl: String,
    val width: Int?,
    val height: Int?
)

data class GamePageResult(
    val games: List<GameItem>,
    val totalCount: Int,
    val hasNextPage: Boolean
)

fun GameDto.toDomain(): GameItem = GameItem(
    id = id,
    slug = slug,
    name = name.ifBlank { "Untitled Game" },
    releasedDate = released?.takeIf { it.isNotBlank() } ?: "TBA",
    coverImageUrl = backgroundImage?.takeIf { it.isNotBlank() },
    rating = rating,
    ratingsCount = ratingsCount,
    metacriticScore = metacritic,
    playtimeHours = playtime,
    genres = genres.orEmpty().mapNotNull { it.name.takeIf(String::isNotBlank) },
    platforms = parentPlatforms.orEmpty().mapNotNull { it.platform?.name?.takeIf(String::isNotBlank) },
    previewScreenshots = shortScreenshots.orEmpty().mapNotNull { it.image.takeIf(String::isNotBlank) }
)

fun GameDetailDto.toDomain(): GameDetail = GameDetail(
    id = id,
    slug = slug,
    name = name.ifBlank { "Untitled Game" },
    description = descriptionRaw?.trim().orEmpty().ifBlank { "No description available." },
    releasedDate = released?.takeIf { it.isNotBlank() } ?: "TBA",
    coverImageUrl = backgroundImage?.takeIf { it.isNotBlank() },
    additionalImageUrl = backgroundImageAdditional?.takeIf { it.isNotBlank() },
    website = website?.takeIf { it.isNotBlank() },
    rating = rating,
    ratingsCount = ratingsCount,
    metacriticScore = metacritic,
    playtimeHours = playtime,
    genres = genres.orEmpty().mapNotNull { it.name.takeIf(String::isNotBlank) },
    platforms = parentPlatforms.orEmpty().mapNotNull { it.platform?.name?.takeIf(String::isNotBlank) },
    developers = developers.orEmpty().mapNotNull { it.name.takeIf(String::isNotBlank) },
    publishers = publishers.orEmpty().mapNotNull { it.name.takeIf(String::isNotBlank) },
    esrbRating = esrbRating?.name?.takeIf { it.isNotBlank() }
)

fun ScreenshotDto.toDomain(): GameScreenshot = GameScreenshot(
    id = id,
    imageUrl = image,
    width = width,
    height = height
)