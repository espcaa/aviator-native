package eu.espcaa.aviator.screens.main

import androidx.compose.material3.Button
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MainAppScreen(onLogout: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(16.dp)
    ) {
        Button(
            onClick = { /* Handle logout */ }
        ) {
            Text("Logout")
        }
    }
}