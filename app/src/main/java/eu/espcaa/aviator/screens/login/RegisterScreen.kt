package eu.espcaa.aviator.screens.login

import AuthInfoViewModel
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import eu.espcaa.aviator.ApiClient
import eu.espcaa.aviator.scripts.EmailExistsRequest
import eu.espcaa.aviator.scripts.OtpRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    authInfoViewModel: AuthInfoViewModel
) {

    val passwordState = remember { mutableStateOf("") }
    val passwordTwoState = remember { mutableStateOf("") }
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
    ) {
        Column {
            Row (
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.End,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ){
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(MaterialShapes.Flower.toShape())
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
                text = "Register",
                style = androidx.compose.material3.MaterialTheme.typography.headlineLarge
            )
            Text(
                text = "Fill the info below to create an account!",
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
            TextField(
                value = passwordTwoState.value,
                onValueChange = {
                    passwordTwoState.value = it
                },
                label = { Text("Repeat Password") },
                visualTransformation = if (passwordShown.value) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
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
                enabled = !isLoading.value,
                onClick = {
                    if (usernameState.value.isEmpty() || passwordState.value.isEmpty() || passwordTwoState.value.isEmpty()) {
                        errorText.value = "Please fill in all fields."
                        return@Button
                    }
                    if (passwordState.value != passwordTwoState.value) {
                        errorText.value = "Passwords do not match."
                        return@Button
                    }
                    if (passwordState.value.length < 8) {
                        errorText.value = "Password must be at least 8 characters long."
                        return@Button
                    }
                    coroutineScope.launch {
                        isLoading.value = true
                        errorText.value = ""
                        val emailExists = CheckEmailExists(usernameState.value)
                        if (emailExists) {
                            errorText.value = "Email already exists."
                            isLoading.value = false
                            return@launch
                        }
                        val result = SendOtp(usernameState.value)
                        if (result == "success") {
                            // Navigate to OTP screen with email & password
                            authInfoViewModel.email = usernameState.value
                            authInfoViewModel.password = passwordState.value
                            navController.navigate("otp")
                        } else {
                            errorText.value = result
                        }
                        delay(1000)
                        isLoading.value = false
                    }

                },

                ) {
                Text(
                    text = "Register now!",
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

suspend fun SendOtp(
    email: String,
) : String{
    try {
        val result = ApiClient.otpApi.generateOtp(OtpRequest(email = email))
        return result.message
    } catch (e: Exception) {
        return "Server error..."
    }
}

suspend fun CheckEmailExists(
    email: String,
): Boolean {
    return try {
        val result = ApiClient.emailExistsApi.emailExists(EmailExistsRequest(email = email))
        result.exists
    } catch (e: Exception) {
        println("Email check error: ${e.message}")
        false
    }
}