package com.fredrickosuala.ncheta.android.features.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fredrickosuala.ncheta.android.InAppUpdateManager
import com.fredrickosuala.ncheta.android.features.auth.AuthScreen
import com.fredrickosuala.ncheta.android.features.entrylist.EntryListScreen
import com.fredrickosuala.ncheta.android.features.input.InputScreen
import com.fredrickosuala.ncheta.android.features.onboarding.OnboardingScreen
import com.fredrickosuala.ncheta.android.features.paywall.PaywallScreen
import com.fredrickosuala.ncheta.android.features.practice.PracticeScreen
import com.fredrickosuala.ncheta.android.features.settings.SettingsScreen
import com.fredrickosuala.ncheta.android.navigation.AppHeader
import com.fredrickosuala.ncheta.android.navigation.BottomNavItem
import com.fredrickosuala.ncheta.features.main.MainViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    mainViewModel: MainViewModel = koinViewModel()
) {

    val hasCompletedOnboarding by mainViewModel.hasCompletedOnboarding.collectAsState()
    val isReadyToUseApp by mainViewModel.isReadyToUseApp.collectAsState()
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    val startDestination = remember(hasCompletedOnboarding, isReadyToUseApp) {
        when {
            hasCompletedOnboarding == null -> "loading"
            !hasCompletedOnboarding!! -> "onboarding"
            !isReadyToUseApp -> "settings"
            else -> BottomNavItem.Create.route
        }
    }

    if (startDestination == "loading") {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    //InApp update
    InAppUpdateManager(snackbarHostState = snackbarHostState)

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            val bottomBarRoutes =
                listOf(BottomNavItem.Create.route, BottomNavItem.Entries.route).toSet()
            val shouldShowTopBar = currentDestination?.route in bottomBarRoutes

            if (shouldShowTopBar) {
                TopAppBar(
                    title = {
                        val title = if (currentDestination?.route.orEmpty() == BottomNavItem.Create.route) "Nchèta" else "My Entries"
                        AppHeader(title, showBackArrow = false) { }
                    },
                    actions = {
                        IconButton(onClick = { navController.navigate("settings?isFirstTime=false") }) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings"
                            )
                        }
                    }
                )
            }
        },
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            val shouldShowBottomBar = listOf(
                BottomNavItem.Create.route,
                BottomNavItem.Entries.route
            ).any { it == currentDestination?.route }

            if (shouldShowBottomBar) {
                Column {
                    HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline)
                    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                        val navItems = listOf(BottomNavItem.Create, BottomNavItem.Entries)
                        navItems.forEach { screen ->
                            NavigationBarItem(
                                icon = { Icon(screen.icon, contentDescription = screen.title) },
                                label = { Text(screen.title) },
                                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                onClick = {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                                )
                            )
                        }
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
            composable("onboarding") {
                OnboardingScreen(onOnboardingComplete = {
                    mainViewModel.setOnboardingComplete()
                    navController.navigate("settings?isFirstTime=true") {
                        popUpTo("onboarding") {
                            inclusive = true
                        }
                    }
                })
            }
            composable(
                route = "settings?isFirstTime={isFirstTime}",
                arguments = listOf(navArgument("isFirstTime") {
                    type = NavType.BoolType
                    defaultValue = false
                })
            ) { backStackEntry ->
                val isFirstTime = backStackEntry.arguments?.getBoolean("isFirstTime") ?: false
                SettingsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onKeySaved = {
                        navController.navigate(BottomNavItem.Create.route) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    },
                    onNavigateToPaywall = { navController.navigate("paywall") },
                    onNavigateToAuth = { navController.navigate("auth") },
                    isFirstTimeSetup = isFirstTime
                )
            }
            composable("paywall") {
                PaywallScreen(
                    onPurchaseSuccess = {
                        navController.popBackStack()
                    },
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
            composable(BottomNavItem.Create.route) {
                InputScreen(
                    onSaved = {
                        navController.navigate(BottomNavItem.Entries.route) {
                            popUpTo(BottomNavItem.Create.route) { inclusive = true }
                        }
                    },
                    onNavigateToAuth = { navController.navigate("auth") },
                    onNavigateToPayWall = { navController.navigate("paywall") }
                )
            }
            composable(BottomNavItem.Entries.route) {
                EntryListScreen(
                    onEntryClick = { entryId -> navController.navigate("practice/$entryId") },
                )
            }
            composable("auth") {
                AuthScreen(onAuthSuccess = { navController.popBackStack() })
            }
            composable(
                route = "practice/{entryId}",
                arguments = listOf(navArgument("entryId") { type = NavType.StringType })
            ) { backStackEntry ->
                val entryId = backStackEntry.arguments?.getString("entryId")
                if (entryId != null) {
                    PracticeScreen(
                        entryId = entryId,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}