package eu.espcaa.aviator.screens.login

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun RegisterScreen(
    onRegister: (password: String, username: String) -> Unit,
) {
    Column {
        Text(
            text = "Welcome to Aviator! Please register to continue.",
            style = androidx.compose.material3.MaterialTheme.typography.headlineLarge
        )
    }
}