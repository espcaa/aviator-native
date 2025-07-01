package eu.espcaa.aviator.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import eu.espcaa.aviator.*
import eu.espcaa.aviator.R
import eu.espcaa.aviator.scripts.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SearchScreen(
    type: String, // "airline" or "airport"
    authState: AuthState,
    fieldId: Number = 0,
    onSelect : (String, Number) -> Unit,
    navController : NavController
) {
    val searchQuery = remember { mutableStateOf("") }
    val isLoading = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val airlineResults = remember { mutableStateOf(listOf<Airline>()) }
    val airportResults = remember { mutableStateOf(listOf<Airport>()) }

    // Initial search
    LaunchedEffect(type, authState) {
        if (authState is AuthState.Authenticated) {
            isLoading.value = true
            if (type == "airline") {
                val result = ApiClient.getAirlinesApi.getAirlines(
                    GetAirlinesRequest(authState.sessionToken, "", 10)
                )
                airlineResults.value = result.airlines
            } else if (type == "airport") {
                val result = ApiClient.getAirportsApi.getAirports(
                    GetAirlinesRequest(authState.sessionToken, "", 10)
                )
                airportResults.value = result.airports
            }
            isLoading.value = false
        }
    }

    Column(
        modifier = Modifier.padding(WindowInsets.systemBars.asPaddingValues()).verticalScroll(scrollState)
    ) {
        SearchBar(
            query = searchQuery.value,
            onQueryChange = { query ->
                searchQuery.value = query
                scope.launch {
                    isLoading.value = true
                    if (authState is AuthState.Authenticated) {
                        if (type == "airline") {
                            val result = ApiClient.getAirlinesApi.getAirlines(
                                GetAirlinesRequest(authState.sessionToken, query, 10)
                            )
                            airlineResults.value = result.airlines
                        } else if (type == "airport") {
                            val result = ApiClient.getAirportsApi.getAirports(
                                GetAirlinesRequest(authState.sessionToken, query, 10)
                            )
                            airportResults.value = result.airports
                        }
                    }
                    isLoading.value = false
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (isLoading.value) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.Center)
                    .padding(top = 32.dp)
            ) {
                LinearWavyProgressIndicator(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
                )
            }

        } else {
            when (type) {
                "airline" -> airlineResults.value.forEach { airline ->
                    Card(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp).clickable{
                        onSelect(airline.code, fieldId)
                        // go back to previous screen
                        navController.navigate("flight_log?showBottomSheet=true")
                    }) {
                        Row (
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFFEEEEEE)),
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data("${BuildConfig.BASE_URL}api/logo/getLogo?icao=${airline.code}")
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Airline Logo",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                        .size(48.dp)
                                )
                            }
                            Text(
                                text = airline.name+" -> "+airline.code,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )

                        }
                    }
                }

                "airport" -> airportResults.value.forEach { airport ->
                    Card(modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp).fillMaxWidth().clickable{
                        onSelect(airport.code, fieldId)
                        // go back to previous screen
                        navController.navigate("flight_log?showBottomSheet=true")
                    }) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = airport.code)
                            Text(text = airport.name)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        label = { Text("Search...") },
        singleLine = true,
        colors = TextFieldDefaults.colors(
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
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    )
}
