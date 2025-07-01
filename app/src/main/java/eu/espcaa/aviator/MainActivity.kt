package eu.espcaa.aviator

import AuthScreen
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.se.omapi.Session
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecureTextField
import androidx.compose.ui.Modifier
import eu.espcaa.aviator.ui.theme.AviatorTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import dagger.hilt.android.AndroidEntryPoint
import eu.espcaa.aviator.screens.LoadingScreen
import eu.espcaa.aviator.screens.MainAppScreen
import kotlinx.coroutines.delay
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import eu.espcaa.aviator.scripts.CreateAccountApi
import eu.espcaa.aviator.scripts.CreateFlightApi
import eu.espcaa.aviator.scripts.DeleteFlightApi
import eu.espcaa.aviator.scripts.EmailExistsApi
import eu.espcaa.aviator.scripts.GetAirlinesApi
import eu.espcaa.aviator.scripts.GetAirportsApi
import eu.espcaa.aviator.scripts.GetFlightsApi
import eu.espcaa.aviator.scripts.LoginApi
import eu.espcaa.aviator.scripts.LoginRequest
import eu.espcaa.aviator.scripts.OtpApi
import eu.espcaa.aviator.scripts.SessionTokenApi
import eu.espcaa.aviator.scripts.SessionTokenRequest
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AviatorTheme {
                val authState = authViewModel.authState.value

                Scaffold(modifier = Modifier.fillMaxSize()) {
                    when (authState) {
                        is AuthState.Loading -> LoadingScreen()
                        is AuthState.Unauthenticated -> AuthScreen(onLogin = { password, username ->
                            authViewModel.login(username, password)
                        })
                        is AuthState.Authenticated -> MainAppScreen(onLogout = { authViewModel.logout() }, authState= authState)
                    }
                }
            }
        }
    }
}

object ApiClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val loginApi : LoginApi = retrofit.create(LoginApi::class.java)
    val otpApi: OtpApi = retrofit.create(OtpApi::class.java)
    val emailExistsApi : EmailExistsApi = retrofit.create(EmailExistsApi::class.java)
    val userCreationApi : CreateAccountApi = retrofit.create(CreateAccountApi::class.java)
    val sessionTokenApi : SessionTokenApi = retrofit.create(SessionTokenApi::class.java)
    val getFlightsApi : GetFlightsApi = retrofit.create(GetFlightsApi::class.java)
    val deleteFlightApi : DeleteFlightApi = retrofit.create(DeleteFlightApi::class.java)
    val createFlightApi : CreateFlightApi = retrofit.create(CreateFlightApi::class.java)
    val getAirlinesApi : GetAirlinesApi = retrofit.create(GetAirlinesApi::class.java)
    val getAirportsApi : GetAirportsApi = retrofit.create(GetAirportsApi::class.java)
}


@HiltViewModel
class AuthViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    private val _authState = mutableStateOf<AuthState>(AuthState.Loading)
    val authState: State<AuthState> = _authState

    init {
        _authState.value = AuthState.Loading
        val token = SecureTokenStorage.loadToken(appContext)
        if (token?.isNotEmpty() ?: false) {
            Log.d("AuthViewModel", "Token found: $token")
            viewModelScope.launch {
                try {
                    ApiClient.sessionTokenApi.getSessionToken(SessionTokenRequest(refreshToken = token)).let { response ->
                        if (response.success) {
                            Log.d("AuthViewModel", "Session token retrieved successfully: ${response.token}")
                            delay(500)
                            _authState.value = AuthState.Authenticated(response.token)
                        } else {
                            Log.d("AuthViewModel", "Failed to retrieve session token: ${response.message}")
                            delay(500)
                            _authState.value = AuthState.Unauthenticated
                        }
                    }
                }
                catch (e: Exception) {
                    Log.e("AuthViewModel", "Error retrieving session token: ${e.message}")
                    delay(500)
                    _authState.value = AuthState.Unauthenticated
                }
            }
        } else {
            Log.d("AuthViewModel", "No token found, setting state to Unauthenticated")
            _authState.value = AuthState.Unauthenticated
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = ApiClient.loginApi.getRefreshToken(LoginRequest(email = username, password = password))
                if (result.token.isEmpty()) {
                    delay(1000)
                    println("Login failed: ${result.message}")
                    _authState.value = AuthState.Unauthenticated
                    return@launch
                }
                if (result.token.length > 10) {
                    SecureTokenStorage.saveToken(appContext, result.token)
                    Log.d("AuthViewModel", "Token saved successfully: ${result.token}")
                    delay(1000)
                    _authState.value = AuthState.Authenticated(result.token)
                }
            } catch (e: Exception) {
                println("Login error: ${e.message}")
                delay(1000)
                _authState.value = AuthState.Unauthenticated
            }
        }
    }

    fun logout() {
        SecureTokenStorage.clearToken(appContext)
        _authState.value = AuthState.Unauthenticated
    }
}


sealed class AuthState {
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Authenticated(val sessionToken: String = "") : AuthState()

}