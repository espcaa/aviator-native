package eu.espcaa.aviator.screens.main

import android.graphics.Point
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.annotation.generated.PolylineAnnotation
import com.mapbox.maps.extension.compose.style.standard.MapboxStandardSatelliteStyle
import com.mapbox.maps.extension.compose.style.standard.MapboxStandardStyle
import com.mapbox.maps.extension.style.layers.properties.generated.LineJoin
import eu.espcaa.aviator.R
import eu.espcaa.aviator.screens.FlightViewModel

@Composable
fun MapScreen(navController: NavController, flightViewModel: FlightViewModel) {
    val satelliteEnabled = remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        MapboxMap(
                modifier = Modifier.fillMaxSize(),
                scaleBar = {},
                compass = {},
                style = {
                    if (satelliteEnabled.value) {
                        // ignore the error
                        MapboxStandardSatelliteStyle()
                    } else {
                        MapboxStandardStyle()
                    }
                    for (flight in flightViewModel.flights) {
                        val points =
                                listOf(
                                        com.mapbox.geojson.Point.fromLngLat(
                                                flight.departureAirportCoords.second,
                                                flight.departureAirportCoords.first
                                        ),
                                        com.mapbox.geojson.Point.fromLngLat(
                                                flight.arrivalAirportCoords.second,
                                                flight.arrivalAirportCoords.first
                                        )
                                )
                        PolylineAnnotation(points = points) {
                            lineColor = Color(0xFF3F51B5)
                            lineWidth = 9.0
                            lineOpacity = 0.8
                            lineJoin = LineJoin.ROUND
                            lineBorderColor = Color(0xFFFFFFFF)
                            lineBorderWidth = 1.0
                        }
                    }
                }
        )
        Column(Modifier.padding(WindowInsets.systemBars.asPaddingValues())) {
            Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.End,
            ) {
                Column {
                    IconButton(
                            onClick = { navController.navigate("settings") },
                            colors =
                                    IconButtonColors(
                                            containerColor = MaterialTheme.colorScheme.surface,
                                            contentColor = MaterialTheme.colorScheme.onSurface,
                                            disabledContainerColor =
                                                    MaterialTheme.colorScheme.onSurface,
                                            disabledContentColor =
                                                    MaterialTheme.colorScheme.onSurface.copy(
                                                            alpha = 0.38f
                                                    )
                                    )
                    ) {
                        Icon(
                                painter = painterResource(id = R.drawable.baseline_settings_24),
                                contentDescription = "Settings",
                                modifier = Modifier.fillMaxWidth(),
                                tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(
                            onClick = { satelliteEnabled.value = !satelliteEnabled.value },
                            colors =
                                    IconButtonColors(
                                            containerColor = MaterialTheme.colorScheme.surface,
                                            contentColor = MaterialTheme.colorScheme.onSurface,
                                            disabledContainerColor =
                                                    MaterialTheme.colorScheme.onSurface,
                                            disabledContentColor =
                                                    MaterialTheme.colorScheme.onSurface.copy(
                                                            alpha = 0.38f
                                                    )
                                    )
                    ) {
                        Icon(
                                painter =
                                        painterResource(id = R.drawable.baseline_satellite_alt_24),
                                contentDescription = "Satellite",
                                modifier = Modifier.fillMaxWidth(),
                                tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
