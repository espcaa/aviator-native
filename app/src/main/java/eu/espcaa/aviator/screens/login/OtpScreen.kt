package eu.espcaa.aviator.screens.login

import AuthInfoViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.navigation.NavController
import eu.espcaa.aviator.ApiClient
import eu.espcaa.aviator.scripts.OtpRequest
import eu.espcaa.aviator.scripts.RegisterRequest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun OtpScreen(
    authInfoViewModel : AuthInfoViewModel,
    navController : NavController
) {
    var otpDigits = remember { List(4) { mutableStateOf("") } }
    val focusRequesters = remember { List(4) { FocusRequester() } }
    val focusManager = LocalFocusManager.current
    val errorText = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val isLoading = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                text = "Verify Your Email",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Enter the 64-digit code we just sent you!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                otpDigits.forEachIndexed { index, digitState ->
                    OutlinedTextField(
                        value = digitState.value,
                        onValueChange = { new ->
                            if (new.length <= 1 && new.all { it.isDigit() }) {
                                digitState.value = new

                                if (new.isNotEmpty()) {
                                    if (index < otpDigits.lastIndex) {
                                        focusRequesters[index + 1].requestFocus()
                                    } else {
                                        focusManager.clearFocus()
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .width(48.dp)
                            .height(64.dp)
                            .focusRequester(focusRequesters[index]),
                        singleLine = true,
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 24.sp,
                            textAlign = TextAlign.Center
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    val fullOtp = otpDigits.joinToString("") { it.value }
                    if (fullOtp.length == 4) {
                        isLoading.value = true
                        // Coroutine to handle account creation
                        coroutineScope.launch {
                            val response = createAccount(
                                email = authInfoViewModel.email,
                                password = authInfoViewModel.password,
                                otp = fullOtp
                            )
                            if (response == "success") {

                                navController.navigate("welcome?message=Account created successfully! Please log in.") {
                                    popUpTo("login") { inclusive = true }
                                }

                            } else {
                                errorText.value = response
                            }
                            isLoading.value = false
                        }

                    } else {
                        errorText.value = "Please enter a valid 4-digit code."
                    }
                },
                modifier = Modifier.fillMaxWidth(0.6f),
                enabled = !isLoading.value,

            ) {
                Text("Submit", style = MaterialTheme.typography.bodyLarge)
            }

            TextButton(
                onClick = { /* TODO: Resend wOTP logic */ }
            ) {
                Text("Resend code (not implemented)", color = MaterialTheme.colorScheme.secondary)
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

suspend fun createAccount(
    email: String,
    password: String,
    otp: String
): String {
    try {
        val response = ApiClient.userCreationApi.createAccount(RegisterRequest(
            email = email,
            password = password,
            otp = otp
        ))
        return response.message
    } catch (e: Exception) {
        return "Unknown server error..."
    }
}