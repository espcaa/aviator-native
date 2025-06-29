package eu.espcaa.aviator.screens.main

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
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.style.standard.MapboxStandardSatelliteStyle
import com.mapbox.maps.extension.compose.style.standard.MapboxStandardStyle
import com.mapbox.maps.extension.style.expressions.dsl.generated.color
import eu.espcaa.aviator.R
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text


@Composable
fun MapScreen(
    navController: NavController
) {
    val satelliteEnabled = remember { mutableStateOf(false) }

    Box {
        MapboxMap(modifier = Modifier.fillMaxSize(), scaleBar = {}, compass = {}, style = {
            if (satelliteEnabled.value) {
                MapboxStandardSatelliteStyle()
            } else {
                MapboxStandardStyle()
            }
        }
        )
        Column (
            Modifier.padding(
                WindowInsets.systemBars.asPaddingValues())
        ) {
            Row (
                modifier = Modifier.fillMaxWidth().padding(horizontal =16.dp, vertical=8.dp),
                horizontalArrangement = Arrangement.End,

            ) {
                Column {
                IconButton(
                    onClick = { navController.navigate("settings") },
                    colors = IconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        disabledContainerColor = MaterialTheme.colorScheme.onSurface,
                        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
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
                    onClick = {
                        satelliteEnabled.value = !satelliteEnabled.value
                    },
                    colors = IconButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        disabledContainerColor = MaterialTheme.colorScheme.onSurface,
                        disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_satellite_alt_24),
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
