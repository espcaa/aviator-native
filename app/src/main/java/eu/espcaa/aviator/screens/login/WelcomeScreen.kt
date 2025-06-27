package eu.espcaa.aviator.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHost

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WelcomeScreen(
    navController: NavController
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background),
        contentAlignment = Alignment.CenterStart
    ) {
        Column {
            Row (
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = androidx.compose.ui.res.painterResource(id = eu.espcaa.aviator.R.drawable.icon),
                    contentDescription = "Aviator",
                    modifier = Modifier.padding(end = 8.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                )
                Text(
                    text = "Aviator",
                    style = MaterialTheme.typography.headlineLargeEmphasized,
                    fontWeight = FontWeight.W900,
                    color = colorScheme.onBackground
                )
            }
            Text(
                text = "A new way to manage your flights",
                style = MaterialTheme.typography.bodySmall,
                color = colorScheme.primary
            )
            Button(
                onClick = {
                    navController.navigate("login")
                },
                modifier = Modifier.padding(top = 32.dp),
            ) {
                Text(
                    text = "Login",
                    style = MaterialTheme.typography.bodyLarge,
                    color = colorScheme.onPrimary
                )
            }
            Button(
                onClick = {
                    navController.navigate("register")
                },
                modifier = Modifier.padding(top = 8.dp),
            ) {
                Text(
                    text = "Register",
                    style = MaterialTheme.typography.bodyLarge,
                    color = colorScheme.onPrimary
                )
            }
        }
    }
}