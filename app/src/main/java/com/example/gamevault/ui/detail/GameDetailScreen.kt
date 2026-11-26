package com.example.gamevault.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.gamevault.data.model.GameDetail

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDetailScreen(
    gameId: Int,
    viewModel: GameDetailViewModel,
    onNavigateBack: () -> Unit,
    onAddToWishlistClick: (GameDetail) -> Unit,
    onWriteReviewClick: (GameDetail) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(gameId) {
        viewModel.loadGameDetail(gameId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.detail?.name ?: "Detail Game", maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.errorMessage != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(uiState.errorMessage!!, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = { viewModel.loadGameDetail(gameId) }) {
                        Text("Coba Lagi")
                    }
                }
            }

            uiState.detail != null -> {
                val game = uiState.detail!!
                val screenshots = uiState.screenshots
                val pagerState = rememberPagerState(pageCount = { maxOf(screenshots.size, 1) })

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Galeri Screenshot Responsif (HorizontalPager + Coil AsyncImage)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(230.dp)
                    ) {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize()
                        ) { page ->
                            val imageUrl = screenshots.getOrNull(page)
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = "Screenshot ${page + 1}",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        if (screenshots.size > 1) {
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(10.dp)
                                    .background(Color.Black.copy(alpha = 0.55f), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                repeat(screenshots.size) { index ->
                                    val isSelected = pagerState.currentPage == index
                                    Box(
                                        modifier = Modifier
                                            .size(if (isSelected) 8.dp else 6.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.6f))
                                    )
                                }
                            }
                        }
                    }

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = game.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Baris Rating & Skor Metacritic
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            AssistChip(
                                onClick = {},
                                label = { Text("${game.rating} / 5.0") },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Star,
                                        contentDescription = null,
                                        tint = Color(0xFFFFB300),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            )

                            game.metacriticScore?.let { score ->
                                val badgeColor = when {
                                    score >= 75 -> Color(0xFF2E7D32)
                                    score >= 50 -> Color(0xFFF9A825)
                                    else -> Color(0xFFC62828)
                                }
                                Surface(
                                    color = badgeColor,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "Metacritic: $score",
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            if (game.releasedDate != "TBA") {
                                Text(
                                    text = "Rilis: ${game.releasedDate}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Metadata Genre & Developer
                        val genreText = game.genres.joinToString(", ").ifBlank { "-" }
                        val devText = game.developers.joinToString(", ").ifBlank { "-" }
                        val platformText = game.platforms.joinToString(", ").ifBlank { "-" }

                        Text("Genre: $genreText", style = MaterialTheme.typography.bodyMedium)
                        Text("Platform: $platformText", style = MaterialTheme.typography.bodyMedium)
                        Text("Developer: $devText", style = MaterialTheme.typography.bodyMedium)

                        Spacer(modifier = Modifier.height(16.dp))

                        // Tombol Integrasi Wishlist & Ulasan (REQ-13)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { onAddToWishlistClick(game) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.BookmarkAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Simpan Wishlist")
                            }

                            OutlinedButton(
                                onClick = { onWriteReviewClick(game) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.RateReview, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Tulis Ulasan")
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "Deskripsi Game",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = game.description.ifBlank { "Tidak ada deskripsi tersedia." },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
