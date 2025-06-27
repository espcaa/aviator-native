package eu.espcaa.aviator

import AuthScreen
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import eu.espcaa.aviator.ui.theme.AviatorTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import dagger.hilt.android.AndroidEntryPoint
import eu.espcaa.aviator.screens.LoadingScreen
import eu.espcaa.aviator.screens.main.MainAppScreen
import kotlinx.coroutines.delay
import androidx.lifecycle.viewModelScope
import com.google.gson.GsonBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import eu.espcaa.aviator.scripts.LoginApi
import eu.espcaa.aviator.scripts.LoginRequest
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
                        is AuthState.Authenticated -> MainAppScreen(onLogout = { authViewModel.logout() })
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
}


@HiltViewModel
class AuthViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    private val _authState = mutableStateOf<AuthState>(AuthState.Loading)
    val authState: State<AuthState> = _authState

    init {
        _authState.value = AuthState.Unauthenticated
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
                    _authState.value = AuthState.Authenticated
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
    object Authenticated : AuthState()
    object Loading : AuthState()
}