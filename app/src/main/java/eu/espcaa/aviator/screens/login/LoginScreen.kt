package eu.espcaa.aviator.screens.login

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import eu.espcaa.aviator.ApiClient
import eu.espcaa.aviator.AuthState
import eu.espcaa.aviator.SecureTokenStorage
import eu.espcaa.aviator.scripts.LoginRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LoginScreen(onLogin: (password: String, username: String) -> Unit, navController: NavController) {

    val passwordState = remember { mutableStateOf("") }
    val usernameState = remember { mutableStateOf("") }
    var errorText = remember { mutableStateOf("") }
    val passwordShown = remember { mutableStateOf(false) }
    val isLoading = remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .padding(32.dp),
        contentAlignment = Alignment.CenterStart
    )
    {

        Column()
        {
            Row (
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.End,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ){
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(MaterialShapes.Boom.toShape())
                        .background(MaterialTheme.colorScheme.primary),
                )
            }
            LinearWavyProgressIndicator(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        color = if (isLoading.value) {
            colorScheme.primary
        } else {
            colorScheme.onBackground.copy(alpha = 0.0f)
        },
        trackColor = if (isLoading.value) {
            colorScheme.onBackground.copy(alpha = 0.1f)
        } else {
            colorScheme.onBackground.copy(alpha = 0.0f)
        }
        )
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
                errorText.value = ""
                isLoading.value = true
                // Try to login
                coroutineScope.launch {
                    val success = CheckLogin(usernameState.value, passwordState.value)
                    isLoading.value = false
                    if (success) {
                        onLogin(passwordState.value, usernameState.value)
                    } else {
                        errorText.value = "Login failed. Please check your credentials."
                    }
                }

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

suspend fun CheckLogin(
    username: String,
    password: String,
): Boolean {
    return try {
        val result = ApiClient.loginApi.getRefreshToken(LoginRequest(email = username, password = password))
        result.token.isNotEmpty() && result.token.length > 10
    } catch (e: Exception) {
        println("Login error: ${e.message}")
        false
    }
}
