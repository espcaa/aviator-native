package eu.espcaa.aviator.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun LoginScreen(onLogin: (password: String, username: String) -> Unit, navController: NavController) {

    val passwordState = remember { mutableStateOf("") }
    val usernameState = remember { mutableStateOf("") }
    var errorText = remember { mutableStateOf("") }
    val passwordShown = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background),
        contentAlignment = Alignment.CenterStart
    )
    {
        Column()
        {
            Text(
                text = "Login",
                style = androidx.compose.material3.MaterialTheme.typography.headlineLarge
            )
            Text(
                text = "Please enter your credentials to continue.",
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                color = colorScheme.primary,
                modifier = Modifier.padding(top=4.dp)
            )
            TextField(
                value = usernameState.value,
                onValueChange = {
                usernameState.value = it
                },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp).fillMaxWidth()
            )
            TextField(
                value = passwordState.value,
                onValueChange = {
                    passwordState.value = it
                },
                label = { Text("Password") },
                visualTransformation = if (passwordShown.value) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                singleLine = true,
                modifier = Modifier.padding(bottom = 8.dp).fillMaxWidth()
            )
            Row (
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            ) {
                Checkbox(
                    checked = passwordShown.value,
                    onCheckedChange = {
                        if (passwordShown.value) {
                            passwordShown.value = false
                        } else {
                            passwordShown.value = true
                        }
                    },
                )
                Text(
                    text = "Show Password",
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 2.dp)
                )
            }
            Button(
                onClick = {
                    if (usernameState.value.isEmpty() || passwordState.value.isEmpty()) {
                        errorText.value = "Please fill in all fields."
                        return@Button
                    }
                    // Check if the username is a valid email
                    onLogin(
                        passwordState.value,
                        usernameState.value
                    )
                },

            ) {
                Text(
                    text = "Login",
                    style = androidx.compose.material3.MaterialTheme.typography.bodyLarge
                )
            }
            Text(
                text = errorText.value,
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                color = colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
