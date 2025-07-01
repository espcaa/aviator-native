package eu.espcaa.aviator.screens

import FlightLogScreen
import android.util.Log
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import eu.espcaa.aviator.ApiClient
import eu.espcaa.aviator.AuthState
import eu.espcaa.aviator.R
import eu.espcaa.aviator.screens.main.MapScreen
import eu.espcaa.aviator.screens.main.PassportScreen
import eu.espcaa.aviator.screens.main.SearchScreen
import eu.espcaa.aviator.screens.main.SettingsScreen
import eu.espcaa.aviator.scripts.DeleteFlightRequest
import eu.espcaa.aviator.scripts.GetFlightsRequest
import kotlinx.coroutines.launch

sealed class Screen(val route: String, val icon: Int, val titleResId: Int) {
    object Map :
            Screen("map", eu.espcaa.aviator.R.drawable.outline_globe_24, R.string.map_screen_title)
    object FlightLog :
            Screen(
                    "flight_log",
                    eu.espcaa.aviator.R.drawable.outline_flight_24,
                    R.string.flight_log_screen_title
            )
    object Passport :
            Screen(
                    "passport",
                    eu.espcaa.aviator.R.drawable.outline_confirmation_number_24,
                    R.string.passport_screen_title
            )
    object Settings : Screen("settings", -1, -1)
    object Search : Screen("search", -1, -1)
}

data class Flight(
        val airlineCode: String,
        val departureCode: String,
        val arrivalCode: String,
        val departureAirportCoords: Pair<Double, Double>,
        val arrivalAirportCoords: Pair<Double, Double>,
        val departureDate: String,
        val duration: String,
        val flightId: String,
)

@Composable
fun MainAppScreen(onLogout: () -> Unit, authState: AuthState) {
    val navController = rememberNavController()
    val bottomNavScreens = listOf(Screen.Map, Screen.FlightLog, Screen.Passport)
    val flightViewModel: FlightViewModel = viewModel()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    if (authState is AuthState.Authenticated) {
        LaunchedEffect(authState.sessionToken) {
            flightViewModel.loadFlights(authState.sessionToken)
        }
    }

    Scaffold(
            bottomBar = {
                if (currentRoute != Screen.Settings.route || currentRoute != Screen.Search.route) {
                    NavigationBar {
                        bottomNavScreens.forEach { screen ->
                            val selected = currentRoute == screen.route
                            NavigationBarItem(
                                    icon = {
                                        Icon(
                                                painter = painterResource(id = screen.icon),
                                                contentDescription = null
                                        )
                                    },
                                    label = { Text(stringResource(screen.titleResId)) },
                                    selected = selected,
                                    onClick = {
                                        if (!selected) {
                                            navController.navigate(screen.route) {
                                                popUpTo(
                                                        navController.graph.findStartDestination()
                                                                .id
                                                ) { saveState = true }
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
                modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
        ) {
            composable(
                    route = Screen.Map.route,
                    enterTransition = { fadeIn(animationSpec = tween(100)) },
                    exitTransition = { fadeOut(animationSpec = tween(100)) },
                    popEnterTransition = { fadeIn(animationSpec = tween(100)) },
                    popExitTransition = { fadeOut(animationSpec = tween(100)) }
            ) { MapScreen(navController, flightViewModel) }

            composable(
                    route = Screen.FlightLog.route + "?showBottomSheet={showBottomSheet}",
                    enterTransition = { fadeIn(animationSpec = tween(100)) },
                    exitTransition = { fadeOut(animationSpec = tween(100)) },
                    popEnterTransition = { fadeIn(animationSpec = tween(100)) },
                    popExitTransition = { fadeOut(animationSpec = tween(100)) },
                    arguments =
                            listOf(
                                    navArgument("showBottomSheet") {
                                        type = NavType.BoolType
                                        defaultValue = false
                                    }
                            ),
            ) {
                FlightLogScreen(
                        authState = authState,
                        flightViewModel = flightViewModel,
                        navController = navController,
                        showBottomSheet = it.arguments?.getBoolean("showBottomSheet") ?: false,
                )
            }

            composable(
                    route = Screen.Passport.route,
                    enterTransition = { fadeIn(animationSpec = tween(100)) },
                    exitTransition = { fadeOut(animationSpec = tween(100)) },
                    popEnterTransition = { fadeIn(animationSpec = tween(100)) },
                    popExitTransition = { fadeOut(animationSpec = tween(100)) }
            ) { PassportScreen() }

            composable(
                    route = Screen.Settings.route,
                    enterTransition = {
                        slideInHorizontally(
                                initialOffsetX = { fullWidth -> fullWidth },
                                animationSpec = tween(300)
                        )
                    },
                    exitTransition = {
                        slideOutHorizontally(
                                targetOffsetX = { fullWidth -> fullWidth },
                                animationSpec = tween(300)
                        )
                    },
                    popEnterTransition = {
                        slideInHorizontally(
                                initialOffsetX = { fullWidth -> fullWidth },
                                animationSpec = tween(300)
                        )
                    },
                    popExitTransition = {
                        slideOutHorizontally(
                                targetOffsetX = { fullWidth -> fullWidth },
                                animationSpec = tween(300)
                        )
                    }
            ) { SettingsScreen(navController, onLogout = onLogout) }

            composable(
                    route = Screen.Search.route + "?type={type}&fieldId={fieldId}",
                    arguments =
                            listOf(
                                    navArgument("type") {
                                        type = NavType.StringType
                                        defaultValue = "airline"
                                    },
                                    navArgument("fieldId") {
                                        type = NavType.IntType
                                        defaultValue = 0
                                    }
                            ),
                    enterTransition = { fadeIn(animationSpec = tween(100)) },
                    exitTransition = { fadeOut(animationSpec = tween(100)) },
                    popEnterTransition = { fadeIn(animationSpec = tween(100)) },
                    popExitTransition = { fadeOut(animationSpec = tween(100)) }
            ) { backStackEntry ->
                val typeArg = backStackEntry.arguments?.getString("type") ?: "airline"

                SearchScreen(
                    fieldId = backStackEntry.arguments?.getInt("fieldId") ?: 0,
                        authState = authState,
                        type = typeArg,
                        onSelect = { selectedValue, fieldId ->
                            if (fieldId == 0) {
                                flightViewModel.newFlightAirlineCode.value = selectedValue
                            } else if (fieldId == 1) {
                                flightViewModel.newFlightDepartureAirportCode.value = selectedValue
                            } else if (fieldId == 2) {
                                flightViewModel.newFlightArrivalAirportCode.value = selectedValue
                            }
                        },
                    navController =  navController,
                )
            }
        }
    }
}

class FlightViewModel : ViewModel() {
    val flights = mutableStateListOf<Flight>()

    val newFlightAirlineCode = mutableStateOf("")
    val newFlightDepartureAirportCode = mutableStateOf("")
    val newFlightArrivalAirportCode = mutableStateOf("")
    val newFlightSelectedDate = mutableStateOf("")
    val newFlightDuration = mutableStateOf("")

    fun loadFlights(sessionToken: String) {
        viewModelScope.launch {
            try {
                val result =
                        ApiClient.getFlightsApi.getFlights(
                                GetFlightsRequest(sessionToken = sessionToken)
                        )
                if (result.success) {
                    flights.clear()
                    flights.addAll(
                            result.flights.map {
                                Flight(
                                        airlineCode = it.airlineCode,
                                        departureCode = it.departureCode,
                                        arrivalCode = it.arrivalCode,
                                        departureAirportCoords =
                                                Pair(
                                                        it.departureAirportLat,
                                                        it.departureAirportLon
                                                ) as
                                                        Pair<Double, Double>,
                                        arrivalAirportCoords =
                                                Pair(it.arrivalAirportLat, it.arrivalAirportLon) as
                                                        Pair<Double, Double>,
                                        departureDate = it.departureDate,
                                        duration = it.duration.toString(),
                                        flightId = it.flightId.toString()
                                )
                            }
                    )
                }
            } catch (e: Exception) {
                Log.e("FlightViewModel", "Error loading flights: ${e.message}")
                // Handle error, e.g., show a snackbar or log the error
            }
        }
    }

    fun addFlight(flight: Flight) {
        flights.add(flight)
    }

    fun deleteFlight(flight: Flight, sessionToken: String) {
        flights.remove(flight)
        // call the api
        try {
            viewModelScope.launch {
                val response =
                        ApiClient.deleteFlightApi.deleteFlight(
                                DeleteFlightRequest(
                                        sessionToken = sessionToken,
                                        flightId = flight.flightId
                                )
                        )
                if (!response.success) {
                    Log.e("FlightViewModel", "Failed to delete flight: ${response.message}")
                }
            }
        } catch (e: Exception) {
            Log.e("FlightViewModel", "Error deleting flight: ${e.message}")
        }
    }

    fun clearNewFlightInputs() {
        newFlightAirlineCode.value = ""
        newFlightDepartureAirportCode.value = ""
        newFlightArrivalAirportCode.value = ""
        newFlightSelectedDate.value = ""
        newFlightDuration.value = ""
    }
}
