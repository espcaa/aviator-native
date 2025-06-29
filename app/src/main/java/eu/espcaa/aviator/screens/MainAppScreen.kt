package eu.espcaa.aviator.screens

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import eu.espcaa.aviator.screens.main.MapScreen
import eu.espcaa.aviator.screens.main.FlightLogScreen
import eu.espcaa.aviator.screens.main.PassportScreen
import eu.espcaa.aviator.screens.main.SettingsScreen
import eu.espcaa.aviator.R


sealed class Screen(val route: String, val icon: Int, val titleResId: Int) {
    object Map : Screen("map",eu.espcaa.aviator.R.drawable.outline_globe_24, R.string.map_screen_title)
    object FlightLog : Screen("flight log", eu.espcaa.aviator.R.drawable.outline_flight_24, R.string.flight_log_screen_title)
    object Passport : Screen("passport",eu.espcaa.aviator.R.drawable.outline_confirmation_number_24, R.string.passport_screen_title)
    object Settings : Screen("settings", -1, -1)
}

data class FlightLogEntry(
    val id: String,
    val origin: String,
    val destination: String,
    val airline: String,
    val airliner : String,
    val date: String

)



@Composable
fun MainAppScreen(onLogout: () -> Unit) {
    val navController = rememberNavController()
    val bottomNavScreens = listOf(Screen.Map, Screen.FlightLog, Screen.Passport)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute != Screen.Settings.route) {
                NavigationBar {
                    bottomNavScreens.forEach { screen ->
                        val selected = currentRoute == screen.route
                        NavigationBarItem(
                            icon = { Icon(painter = painterResource(id = screen.icon), contentDescription = null) },
                            label = { Text(stringResource(screen.titleResId)) },
                            selected = selected,
                            onClick = {
                                if (!selected) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Map.route,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            composable(
                route = Screen.Map.route,
                enterTransition = { fadeIn( animationSpec = tween(100)) },
                exitTransition = { fadeOut( animationSpec = tween(100)) },
                popEnterTransition = { fadeIn( animationSpec = tween(100)) },
                popExitTransition = { fadeOut( animationSpec = tween(100)) }

            ) {
                MapScreen(navController)
            }

            composable(
                route = Screen.FlightLog.route,
                enterTransition = { fadeIn( animationSpec = tween(100)) },
                exitTransition = { fadeOut( animationSpec = tween(100)) },
                popEnterTransition = { fadeIn( animationSpec = tween(100)) },
                popExitTransition = { fadeOut( animationSpec = tween(100)) }
            ) {
                FlightLogScreen()
            }

            composable(
                route = Screen.Passport.route,
                enterTransition = { fadeIn( animationSpec = tween(100)) },
                exitTransition = { fadeOut( animationSpec = tween(100)) },
                popEnterTransition = { fadeIn( animationSpec = tween(100)) },
                popExitTransition = { fadeOut( animationSpec = tween(100)) }

                ) {
                PassportScreen()
            }

            composable(
                route = Screen.Settings.route,
                enterTransition = { slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }, animationSpec = tween(300)) },
                exitTransition = { slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth }, animationSpec = tween(300)) },
                popEnterTransition = { slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }, animationSpec = tween(300)) },
                popExitTransition = { slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth }, animationSpec = tween(300)) }

            ) {
                SettingsScreen(
                    navController,
                    onLogout = onLogout
                )
            }
        }
    }
}