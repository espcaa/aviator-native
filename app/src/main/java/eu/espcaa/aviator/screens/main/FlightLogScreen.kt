import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.mapbox.maps.extension.style.expressions.dsl.generated.color
import eu.espcaa.aviator.ApiClient
import eu.espcaa.aviator.AuthState
import eu.espcaa.aviator.BuildConfig
import eu.espcaa.aviator.R
import eu.espcaa.aviator.screens.Flight
import eu.espcaa.aviator.screens.FlightViewModel
import eu.espcaa.aviator.scripts.CreateFlightRequest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FlightLogScreen(
        authState: AuthState,
        flightViewModel: FlightViewModel,
        navController: NavController,
        showBottomSheet : Boolean = false
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(showBottomSheet) }
    val flightSearch = remember { mutableStateOf("") }
    val showDetailsBottomSheet = remember { mutableStateOf(false) }

    val airlineName = remember { mutableStateOf("") }
    val errorText = remember { mutableStateOf("") }
    val bottomSheetLoading: MutableState<Boolean> = remember { mutableStateOf(false) }

    val showDatePicker = remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val activeFlightId = remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize().padding(WindowInsets.systemBars.asPaddingValues())) {
        Column {
            TextField(
                    value = flightSearch.value,
                    onValueChange = { flightSearch.value = it },
                    label = { Text("Search one of your flight!") },
                    singleLine = true,
                    colors =
                            TextFieldDefaults.colors(
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                            ),
                    leadingIcon = {
                        Icon(
                                painter = painterResource(id = R.drawable.baseline_search_24),
                                contentDescription = "Search Icon",
                                tint = Color.Gray
                        )
                    },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(16.dp).fillMaxWidth()
            )
            if (flightViewModel.flights.isEmpty()) {
                Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                            text = "No flights yet, go ahead and add one!",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                Column(
                        modifier =
                                Modifier.padding(16.dp)
                                        .verticalScroll(scrollState)
                                        .padding(bottom = 112.dp)
                ) {
                    val sortedFlights = sortFlightsByLatestFirst(flightViewModel.flights)
                    for (flight in sortedFlights) {
                        Card(
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .padding(bottom = 8.dp)
                                                .clickable {}
                                                .pointerInput(Unit) {
                                                    detectTapGestures(
                                                            onLongPress = {
                                                                activeFlightId.value =
                                                                        flight.flightId
                                                                showDetailsBottomSheet.value = true
                                                            }
                                                    )
                                                },
                        ) {
                            Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Log.d(
                                            "FlightLogScreen",
                                            "Image URL: ${BuildConfig.BASE_URL}api/logo/getLogo?icao=${flight.airlineCode}"
                                    )
                                    Box(
                                            modifier =
                                                    Modifier.size(50.dp)
                                                            .clip(RoundedCornerShape(8.dp))
                                                            .background(Color(0xFFEEEEEE)),
                                            contentAlignment = Alignment.Center
                                    ) {
                                        AsyncImage(
                                                model =
                                                        ImageRequest.Builder(LocalContext.current)
                                                                .data(
                                                                        "${BuildConfig.BASE_URL}api/logo/getLogo?icao=${flight.airlineCode}"
                                                                )
                                                                .crossfade(true)
                                                                .build(),
                                                contentDescription = "airlinelogo",
                                                contentScale = ContentScale.Fit,
                                                modifier = Modifier.size(48.dp)
                                        )
                                    }
                                    Text(
                                            text =
                                                    flight.departureCode +
                                                            " to " +
                                                            flight.arrivalCode,
                                            style = MaterialTheme.typography.headlineSmall,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.padding(start = 16.dp)
                                    )
                                }
                                Row() {
                                    Text(
                                            text = "On the ${flight.departureDate}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier =
                                                    Modifier.padding(start = 16.dp, bottom = 8.dp)
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text(
                                            text = "Duration: " + flight.duration + "h",
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.padding(end = 16.dp, bottom = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    Box(modifier = Modifier.fillMaxSize().padding(bottom = 116.dp)) {
        FloatingActionButton(
                onClick = {
                    showBottomSheet = true
                    scope.launch { sheetState.show() }
                },
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Icon(
                    painter = painterResource(id = R.drawable.baseline_add_24),
                    contentDescription = "Add a flight",
            )
        }
    }

    if (showDetailsBottomSheet.value) {

        ModalBottomSheet(
                onDismissRequest = { showDetailsBottomSheet.value = false },
                sheetState = sheetState
        ) {
            if (bottomSheetLoading.value) {
                Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                ) {
                    CircularWavyProgressIndicator(
                            modifier = Modifier.size(72.dp),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    )
                }
            } else {
                // get the flight by id
                val flight =
                        flightViewModel.flights.firstOrNull { it.flightId == activeFlightId.value }
                if (flight == null) {
                    Text(
                            text = "Flight not found",
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.headlineSmall
                    )
                } else {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                                text = "Flight Details",
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                        ) {
                            Box(
                                    modifier =
                                            Modifier.size(50.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(Color(0xFFEEEEEE)),
                                    contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                        model =
                                                ImageRequest.Builder(LocalContext.current)
                                                        .data(
                                                                "${BuildConfig.BASE_URL}api/logo/getLogo?icao=${flight.airlineCode}"
                                                        )
                                                        .crossfade(true)
                                                        .build(),
                                        contentDescription = "airlinelogo",
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier.size(48.dp)
                                )
                            }
                            Text(
                                    text =
                                            flight.airlineCode +
                                                    " - " +
                                                    flight.departureCode +
                                                    " to " +
                                                    flight.arrivalCode,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                        Text(
                                text = "Departure Date: ${flight.departureDate}",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                                text = "Duration: ${flight.duration}h",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Button(
                                colors =
                                        ButtonColors(
                                                containerColor =
                                                        MaterialTheme.colorScheme.errorContainer,
                                                contentColor =
                                                        MaterialTheme.colorScheme.onErrorContainer,
                                                disabledContainerColor =
                                                        MaterialTheme.colorScheme.onErrorContainer,
                                                disabledContentColor =
                                                        MaterialTheme.colorScheme.onErrorContainer
                                                                .copy(alpha = 0.38f)
                                        ),
                                onClick = {
                                    scope.launch {
                                        bottomSheetLoading.value = true
                                        if (authState is AuthState.Authenticated) {
                                            delay(2000)
                                            flightViewModel.deleteFlight(
                                                    flight = flight,
                                                    sessionToken = authState.sessionToken
                                            )
                                        }
                                        bottomSheetLoading.value = false
                                        showDetailsBottomSheet.value = false
                                        scope.launch { sheetState.hide() }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                        ) {
                            Text(
                                    text = "Delete Flight",
                                    style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                    }
                }
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                modifier = Modifier.fillMaxWidth()
        ) {
            if (bottomSheetLoading.value) {
                Box(
                        modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center
                ) {
                    CircularWavyProgressIndicator(
                            modifier = Modifier.size(72.dp),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    )
                }
            } else {

                Text(
                        text = "Add a new flight",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.headlineSmall
                )
                Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier =
                                Modifier.fillMaxWidth()
                                        .padding(
                                                top = 8.dp,
                                                bottom = 8.dp,
                                                start = 16.dp,
                                                end = 16.dp
                                        )
                ) {
                    OutlinedTextField(
                            value = flightViewModel.newFlightAirlineCode.value,
                            onValueChange = {
                                if (it.length < 3) {
                                    flightViewModel.newFlightAirlineCode.value =
                                            it.uppercase(Locale.getDefault())
                                } else {
                                    flightViewModel.newFlightAirlineCode.value =
                                            it.substring(0, 3).uppercase(Locale.getDefault())
                                }
                            },
                            label = { Text("Airline Code") },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.weight(1f)
                    )
                    IconButton(
                            onClick = { navController.navigate("search?type=airline&fieldId=0") },
                            colors =
                                    IconButtonDefaults.iconButtonColors(
                                            containerColor = MaterialTheme.colorScheme.primary,
                                            contentColor = MaterialTheme.colorScheme.onPrimary,
                                    ),
                            modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Icon(
                                painter = painterResource(id = R.drawable.baseline_search_24),
                                contentDescription = "Search icon",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier =
                                Modifier.fillMaxWidth()
                                        .padding(
                                                top = 8.dp,
                                                bottom = 8.dp,
                                                start = 16.dp,
                                                end = 16.dp
                                        )
                ) {
                    OutlinedTextField(
                            value = flightViewModel.newFlightDepartureAirportCode.value,
                            onValueChange = {
                                if (it.length < 3) {
                                    flightViewModel.newFlightDepartureAirportCode.value =
                                            it.uppercase(Locale.getDefault())
                                } else {
                                    flightViewModel.newFlightDepartureAirportCode.value =
                                            it.substring(0, 3).uppercase(Locale.getDefault())
                                }
                            },
                            label = { Text("Departure Airport Code") },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.weight(1f)
                    )
                    IconButton(
                            onClick = { navController.navigate("search?type=airport&fieldId=1") },
                            colors =
                                    IconButtonDefaults.iconButtonColors(
                                            containerColor = MaterialTheme.colorScheme.primary,
                                            contentColor = MaterialTheme.colorScheme.onPrimary,
                                    ),
                            modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Icon(
                                painter = painterResource(id = R.drawable.baseline_search_24),
                                contentDescription = "Search icon",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier =
                                Modifier.fillMaxWidth()
                                        .padding(
                                                top = 8.dp,
                                                bottom = 8.dp,
                                                start = 16.dp,
                                                end = 16.dp
                                        )
                ) {
                    OutlinedTextField(
                            value = flightViewModel.newFlightArrivalAirportCode.value,
                            onValueChange = {
                                if (it.length < 3) {
                                    flightViewModel.newFlightArrivalAirportCode.value =
                                            it.uppercase(Locale.getDefault())
                                } else {
                                    flightViewModel.newFlightArrivalAirportCode.value =
                                            it.substring(0, 3).uppercase(Locale.getDefault())
                                }
                            },
                            label = { Text("Arrival Airport Code") },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.weight(1f)
                    )
                    IconButton(
                            onClick = { navController.navigate("search?type=airport&fieldId=2") },
                            colors =
                                    IconButtonDefaults.iconButtonColors(
                                            containerColor = MaterialTheme.colorScheme.primary,
                                            contentColor = MaterialTheme.colorScheme.onPrimary,
                                    ),
                            modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Icon(
                                painter = painterResource(id = R.drawable.baseline_search_24),
                                contentDescription = "Search icon",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(24.dp)
                        )
                    }
                }
                OutlinedButton(
                        onClick = { showDatePicker.value = true },
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        shape = RoundedCornerShape(16.dp),
                ) {
                    Text(
                            text =
                                    if (flightViewModel.newFlightSelectedDate.value.isEmpty())
                                            "Select a date"
                                    else flightViewModel.newFlightSelectedDate.value,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.primary
                    )
                }
                Button(
                        onClick = {
                            if (flightViewModel.newFlightArrivalAirportCode.value.isEmpty() ||
                                            flightViewModel.newFlightAirlineCode.value.isEmpty() ||
                                            flightViewModel.newFlightDepartureAirportCode.value
                                                    .isEmpty()
                            ) {
                                errorText.value =
                                        "Please fill all of the fields with a correct value."
                            } else if (flightViewModel.newFlightArrivalAirportCode.value.length <
                                            3 ||
                                            flightViewModel.newFlightAirlineCode.value.length < 3 ||
                                            flightViewModel
                                                    .newFlightDepartureAirportCode
                                                    .value
                                                    .length < 3
                            ) {
                                errorText.value =
                                        "Please ensure all codes are at least 3 characters long."
                            } else if (flightViewModel.newFlightSelectedDate.value.isEmpty()) {
                                errorText.value = "Please select a date for your flight."
                            } else if (flightViewModel.newFlightDepartureAirportCode.value ==
                                            flightViewModel.newFlightArrivalAirportCode.value
                            ) {
                                errorText.value =
                                        "Departure and arrival airport codes cannot be the same."
                            } else {
                                bottomSheetLoading.value = true
                                scope.launch {
                                    if (authState is AuthState.Authenticated) {
                                        val token = authState.sessionToken
                                        try {
                                            val response =
                                                    ApiClient.createFlightApi.createFlight(
                                                            CreateFlightRequest(
                                                                    sessionToken = token,
                                                                    airlineCode =
                                                                            flightViewModel
                                                                                    .newFlightAirlineCode
                                                                                    .value,
                                                                    arrivalCode =
                                                                            flightViewModel
                                                                                    .newFlightArrivalAirportCode
                                                                                    .value,
                                                                    departureCode =
                                                                            flightViewModel
                                                                                    .newFlightDepartureAirportCode
                                                                                    .value,
                                                                    departureDate =
                                                                            flightViewModel
                                                                                    .newFlightSelectedDate
                                                                                    .value,
                                                            )
                                                    )

                                            if (response.success) {
                                                val currentAirlineCode =
                                                        flightViewModel.newFlightAirlineCode.value
                                                val currentDepartureCode =
                                                        flightViewModel
                                                                .newFlightDepartureAirportCode
                                                                .value
                                                val currentArrivalCode =
                                                        flightViewModel
                                                                .newFlightArrivalAirportCode
                                                                .value
                                                val currentDepartureDate =
                                                        flightViewModel.newFlightSelectedDate.value

                                                airlineName.value = ""
                                                flightViewModel.clearNewFlightInputs()

                                                delay(2000)
                                                bottomSheetLoading.value = false

                                                flightViewModel.addFlight(
                                                        Flight(
                                                                airlineCode = currentAirlineCode,
                                                                departureCode =
                                                                        currentDepartureCode,
                                                                arrivalCode = currentArrivalCode,
                                                                departureDate =
                                                                        currentDepartureDate,
                                                                departureAirportCoords =
                                                                        Pair(
                                                                                response.positions
                                                                                        .departure
                                                                                        .lat,
                                                                                response.positions
                                                                                        .departure
                                                                                        .lon
                                                                        ) as
                                                                                Pair<
                                                                                        Double,
                                                                                        Double>,
                                                                arrivalAirportCoords =
                                                                        Pair(
                                                                                response.positions
                                                                                        .arrival
                                                                                        .lat,
                                                                                response.positions
                                                                                        .arrival
                                                                                        .lon
                                                                        ) as
                                                                                Pair<
                                                                                        Double,
                                                                                        Double>,
                                                                duration =
                                                                        response.duration
                                                                                .toString(),
                                                                flightId = response.flightId
                                                        )
                                                )

                                                showBottomSheet = false
                                                scope.launch { sheetState.hide() }
                                            } else {
                                                delay(2000)
                                                bottomSheetLoading.value = false
                                                errorText.value = "Failed to save flight..."
                                            }
                                        } catch (e: retrofit2.HttpException) {
                                            // HTTP error (like 400, 401, 500...)
                                            val errorBody = e.response()?.errorBody()?.string()
                                            errorText.value =
                                                    "Server error: HTTP ${e.code()}\n$errorBody"
                                            bottomSheetLoading.value = false
                                        } catch (e: Exception) {
                                            // Network or unexpected error
                                            errorText.value =
                                                    "Unexpected error: ${e.localizedMessage}"
                                            bottomSheetLoading.value = false
                                        }
                                    } else {
                                        errorText.value = "You must be logged in."
                                        bottomSheetLoading.value = false
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) { Text("Save your flight!") }
                if (errorText.value.isNotEmpty()) {
                    Text(
                            text = errorText.value,
                            color = MaterialTheme.colorScheme.error,
                            modifier =
                                    Modifier.padding(
                                            top = 16.dp,
                                            bottom = 16.dp,
                                            start = 16.dp,
                                            end = 16.dp
                                    ),
                            style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }

    if (showDatePicker.value) {
        DatePickerDialog(
                onDismissRequest = { showDatePicker.value = false },
                confirmButton = {
                    Button(
                            onClick = {
                                val millis = datePickerState.selectedDateMillis ?: 0L
                                flightViewModel.newFlightSelectedDate.value =
                                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                                .format(Date(millis))
                                showDatePicker.value = false
                            }
                    ) { Text("OK") }
                },
                dismissButton = {
                    Button(onClick = { showDatePicker.value = false }) { Text("Cancel") }
                }
        ) { DatePicker(state = datePickerState) }
    }
}

fun sortFlightsByLatestFirst(flights: List<Flight>): List<Flight> {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return flights.sortedByDescending { flight ->
        dateFormat.parse(flight.departureDate)?.time ?: 0L
    }
}
