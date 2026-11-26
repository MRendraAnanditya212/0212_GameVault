package com.example.gamevault.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gamevault.ui.auth.AuthViewModel
import com.example.gamevault.ui.auth.LoginScreen
import com.example.gamevault.ui.auth.RegisterScreen
import com.example.gamevault.ui.explore.ExploreGamesScreen
import com.example.gamevault.ui.explore.ExploreViewModel

@Composable
fun GameVaultNavGraph(
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val topLevelRoutes = gameVaultBottomNavItems.map { it.screen.route }.toSet()
    val showBottomBar = currentDestination?.route in topLevelRoutes

    val authViewModel: AuthViewModel = viewModel()
    val sessionState by authViewModel.sessionState.collectAsState()
    val authUiState by authViewModel.uiState.collectAsState()

    val startDestination = if (sessionState.isLoggedIn) Screen.Explore.route else Screen.Login.route

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp
                ) {
                    gameVaultBottomNavItems.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.route == item.screen.route
                        } == true

                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Explore.route) {
                val exploreViewModel: ExploreViewModel = viewModel(
                    factory = ExploreViewModel.factory()
                )
                ExploreGamesScreen(
                    viewModel = exploreViewModel,
                    onGameClick = { gameId ->
                        navController.navigate(Screen.GameDetail.createRoute(gameId))
                    }
                )
            }

            composable(Screen.Collections.route) {
                ModulePlaceholderScreen(
                    title = "Curated Collections",
                    subtitle = "Admin-curated game lists & RBAC controls scheduled for Week 12."
                )
            }

            composable(Screen.Wishlist.route) {
                ModulePlaceholderScreen(
                    title = "Gamer Wishlist & Backlog",
                    subtitle = "Personal priority backlog & local report export scheduled for Week 14."
                )
            }

            composable(Screen.Profile.route) {
                ModulePlaceholderScreen(
                    title = "Gamer Profile & Auth",
                    subtitle = "JWT authentication & role-based session management scheduled for Week 11."
                )
            }

            composable(
                route = Screen.GameDetail.route,
                arguments = listOf(
                    navArgument(Screen.GameDetail.ARG_GAME_ID) { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val gameId = backStackEntry.arguments?.getInt(Screen.GameDetail.ARG_GAME_ID) ?: -1
                GameDetailPlaceholderScreen(
                    gameId = gameId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Login.route) {
                LoginScreen(
                    uiState = authUiState,
                    onLoginClick = { email, password -> authViewModel.login(email, password) },
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                    onClearMessages = { authViewModel.clearMessages() }
                )
            }

            composable(Screen.Register.route) {
                RegisterScreen(
                    uiState = authUiState,
                    onRegisterClick = { nama, email, gamerTag, pass ->
                        authViewModel.register(nama, email, gamerTag, pass)
                    },
                    onNavigateBackToLogin = { navController.popBackStack() },
                    onClearMessages = { authViewModel.clearMessages() }
                )
            }
        }
    }
}

@Composable
internal fun ModulePlaceholderScreen(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Outlined.SportsEsports,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun GameDetailPlaceholderScreen(
    gameId: Int,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Game Detail #$gameId",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        ModulePlaceholderScreen(
            title = "Game ID: $gameId",
            subtitle = "Full game detail, responsive screenshot gallery, and community reviews arrive in Weeks 11 & 13.",
            modifier = Modifier.padding(innerPadding)
        )
    }
}
