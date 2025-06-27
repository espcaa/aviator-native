import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import eu.espcaa.aviator.screens.login.*

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(onLogin: (password: String, username: String) -> Unit) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "welcome",
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(WindowInsets.systemBars.asPaddingValues())
            .padding(32.dp)
    ) {
        composable (
            route = "welcome",
            enterTransition = { slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }, animationSpec = tween(300)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { fullWidth -> -fullWidth }, animationSpec = tween(300)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { fullWidth -> -fullWidth }, animationSpec = tween(300)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth }, animationSpec = tween(300)) }
        ) {
            WelcomeScreen(navController)
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
            RegisterScreen(onRegister = { _, _ ->
                navController.navigate("login")
            })
        }
        composable(
            route = "otp",
            enterTransition = { slideInHorizontally(initialOffsetX = { fullWidth -> fullWidth }, animationSpec = tween(300)) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { fullWidth -> -fullWidth }, animationSpec = tween(300)) },
            popEnterTransition = { slideInHorizontally(initialOffsetX = { fullWidth -> -fullWidth }, animationSpec = tween(300)) },
            popExitTransition = { slideOutHorizontally(targetOffsetX = { fullWidth -> fullWidth }, animationSpec = tween(300)) }
        ) {
            OtpScreen()
        }
    }
}
