package com.example.gamevault.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CollectionsBookmark
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    data object Explore : Screen("explore")
    data object Collections : Screen("collections")
    data object Wishlist : Screen("wishlist")
    data object Profile : Screen("profile")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object ExportHistory : Screen("export_history")

    data object GameDetail : Screen("game_detail/{gameId}") {
        const val ARG_GAME_ID = "gameId"
        fun createRoute(gameId: Int): String = "game_detail/$gameId"
    }
}

data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val gameVaultBottomNavItems = listOf(
    BottomNavItem(
        screen = Screen.Explore,
        label = "Explore",
        selectedIcon = Icons.Filled.Explore,
        unselectedIcon = Icons.Outlined.Explore
    ),
    BottomNavItem(
        screen = Screen.Collections,
        label = "Collections",
        selectedIcon = Icons.Filled.CollectionsBookmark,
        unselectedIcon = Icons.Outlined.CollectionsBookmark
    ),
    BottomNavItem(
        screen = Screen.Wishlist,
        label = "Wishlist",
        selectedIcon = Icons.Filled.Bookmark,
        unselectedIcon = Icons.Outlined.BookmarkBorder
    ),
    BottomNavItem(
        screen = Screen.Profile,
        label = "Profile",
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person
    )
)
