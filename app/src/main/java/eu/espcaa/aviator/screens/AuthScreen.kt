import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import eu.espcaa.aviator.screens.login.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel

class AuthInfoViewModel : ViewModel() {
    var email by mutableStateOf("")
    var password by mutableStateOf("")
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(onLogin: (password: String, username: String) -> Unit) {
    val navController = rememberNavController()
    val authInfoViewModel: AuthInfoViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "welcome",
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(WindowInsets.systemBars.asPaddingValues())
    ) {
        composable (
            route = "welcome?message={message}",
            arguments = listOf(
                navArgument("message") {
                    type = NavType.StringType
                    defaultValue= null
                    nullable = true
                }
            ),
            enterTransition = { slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }, animationSpec = tween(300)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { fullWidth -> -fullWidth }, animationSpec = tween(300)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { fullWidth -> -fullWidth }, animationSpec = tween(300)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth }, animationSpec = tween(300)) }
        ) {
            WelcomeScreen(navController, message = it.arguments?.getString("message"))
        }

        composable(
            route = "login",
            enterTransition = { slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }, animationSpec = tween(300)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { fullWidth -> -fullWidth }, animationSpec = tween(300)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { fullWidth -> -fullWidth }, animationSpec = tween(300)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth }, animationSpec = tween(300)) }
        ) {
            LoginScreen(onLogin = onLogin, navController = navController)
        }

        composable(
            route = "register",
            enterTransition = { slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }, animationSpec = tween(300)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { fullWidth -> -fullWidth }, animationSpec = tween(300)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { fullWidth -> -fullWidth }, animationSpec = tween(300)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth }, animationSpec = tween(300)) }
        ) {
            RegisterScreen(
                navController = navController,
                authInfoViewModel = authInfoViewModel
            )
        }
        composable(
            route = "otp",
            enterTransition = { slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }, animationSpec = tween(300)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { fullWidth -> -fullWidth }, animationSpec = tween(300)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { fullWidth -> -fullWidth }, animationSpec = tween(300)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth }, animationSpec = tween(300)) }
        ) {
            OtpScreen(authInfoViewModel=authInfoViewModel, navController = navController)
        }
    }
}
