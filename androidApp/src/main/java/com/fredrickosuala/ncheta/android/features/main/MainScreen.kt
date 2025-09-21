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
import com.fredrickosuala.ncheta.android.navigation.Route
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
            hasCompletedOnboarding == null -> Route.Loading.path
            !hasCompletedOnboarding!! -> Route.Onboarding.path
            !isReadyToUseApp -> Route.Settings.create(true)
            else -> Route.Create.path
        }
    }

    if (startDestination == Route.Loading.path) {
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
            val bottomBarRoutes = setOf(Route.Create.path, Route.Entries.path)
            val shouldShowTopBar = currentDestination?.route in bottomBarRoutes

            if (shouldShowTopBar) {
                TopAppBar(
                    title = {
                        val title = if (currentDestination?.route == Route.Create.path) "Nchèta" else "My Entries"
                        AppHeader(title, showBackArrow = false) { }
                    },
                    actions = {
                        IconButton(onClick = { navController.navigate(Route.Settings.create(false)) }) {
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
            val shouldShowBottomBar = listOf(Route.Create.path, Route.Entries.path)
                .any { it == currentDestination?.route }

            if (shouldShowBottomBar) {
                Column {
                    HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline)
                    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                        val navItems = listOf(Route.Create, Route.Entries)
                        navItems.forEach { screen ->
                            NavigationBarItem(
                                icon = { Icon(BottomNavItem.fromRoute(screen.path)!!.icon, contentDescription = BottomNavItem.fromRoute(screen.path)!!.title) },
                                label = { Text(BottomNavItem.fromRoute(screen.path)!!.title) },
                                selected = currentDestination?.hierarchy?.any { it.route == screen.path } == true,
                                onClick = {
                                    navController.navigate(screen.path) {
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
            composable(Route.Onboarding.path) {
                OnboardingScreen(
                    onOnboardingComplete = {
                        mainViewModel.setOnboardingComplete()
                        navController.navigate(Route.Settings.create(true)) {
                            popUpTo(Route.Onboarding.path) { inclusive = true }
                        }
                    }
                )
            }

            composable(
                route = Route.Settings.path,
                arguments = listOf(navArgument("isFirstTime") { type = NavType.BoolType })
            ) { backStackEntry ->
                val isFirstTime = backStackEntry.arguments?.getBoolean("isFirstTime") ?: false
                SettingsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onKeySaved = {
                        navController.navigate(Route.Create.path) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    },
                    onNavigateToPaywall = { navController.navigate(Route.Paywall.path) },
                    onNavigateToAuth = { navController.navigate(Route.Auth.path) },
                    isFirstTimeSetup = isFirstTime
                )
            }

            composable(Route.Paywall.path) {
                PaywallScreen(
                    onPurchaseSuccess = { navController.popBackStack() },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Route.Create.path) {
                InputScreen(
                    onSaved = {
                        navController.navigate(Route.Entries.path) {
                            popUpTo(Route.Create.path) { inclusive = true }
                        }
                    },
                    onNavigateToAuth = { navController.navigate(Route.Auth.path) },
                    onNavigateToPayWall = { navController.navigate(Route.Paywall.path) }
                )
            }

            composable(Route.Entries.path) {
                EntryListScreen(
                    onEntryClick = { entryId -> navController.navigate(Route.Practice.create(entryId)) },
                )
            }

            composable(Route.Auth.path) {
                AuthScreen(onAuthSuccess = { navController.popBackStack() })
            }

            composable(
                route = Route.Practice.path,
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
