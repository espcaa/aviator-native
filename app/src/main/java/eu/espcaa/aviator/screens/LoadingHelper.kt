import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.navigation.NavBackStackEntry
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LoadingHelper(backStackEntry: NavBackStackEntry, navController: androidx.navigation.NavController) {
    val action = backStackEntry.arguments?.getString("action")
    val nextScreen = backStackEntry.arguments?.getString("nextScreen")

    LaunchedEffect(action) {
        when (action) {
            "otpSend" -> {

            }
            else -> println("Unknown action")
        }
            navController.navigate(nextScreen.toString()) {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                }
            }
    }
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LoadingIndicator(
            modifier = Modifier
                .size(128.dp),
        )
    }

}