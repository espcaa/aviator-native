package eu.espcaa.aviator.screens.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    onLogout: () -> Unit,
) {
    Column {
    TopAppBar(
        title = { Text("Settings") },
        navigationIcon = {
            androidx.compose.material3.IconButton(
                onClick = { navController.navigateUp() }
            )  {
                androidx.compose.material3.Icon(
                    painter = painterResource(id = eu.espcaa.aviator.R.drawable.baseline_arrow_back_24),
                    contentDescription = "Back"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        )
    )


    Box (
        modifier = Modifier
            .padding(16.dp)
    )
    {

        Column {
            Button(
                onClick = onLogout,
                modifier = Modifier.padding(bottom = 16.dp),
                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.onErrorContainer,
                    disabledContentColor = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.38f)
                ),
                shape = MaterialTheme.shapes.large,
            ) {
                Text("Logout")
            }
        }
    }
        }
}