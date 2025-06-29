package eu.espcaa.aviator.screens.login

import androidx.collection.FloatFloatPair
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextGeometricTransform
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.graphics.shapes.PointTransformer
import androidx.graphics.shapes.RoundedPolygon
import androidx.graphics.shapes.toPath
import androidx.graphics.shapes.transformed
import androidx.navigation.NavController
import androidx.navigation.NavHost
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.MaterialShapeUtils
import eu.espcaa.aviator.MaterialExpressiveShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WelcomeScreen(
    navController: NavController,
    message : String? = null,
) {
    var showMessage by remember { mutableStateOf(message != null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background),
        contentAlignment = Alignment.CenterStart
    ) {
        Column (
            modifier = Modifier
                .padding(32.dp)
        ) {

            if (showMessage && message != null) {
                AnimatedVisibility(visible = showMessage) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = colorScheme.onPrimaryContainer,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { showMessage = false }) {
                                Icon(
                                    painter = painterResource(eu.espcaa.aviator.R.drawable.baseline_close_24),
                                    contentDescription = "Dismiss",
                                    tint = colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = eu.espcaa.aviator.R.drawable.welcome),
                    contentDescription = "Aviator Logo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .clip(MaterialShapes.Cookie9Sided.transformed(f = object :
                            PointTransformer {
                            override fun transform(x: Float, y: Float): FloatFloatPair {
                                val scaledX = x * 0.568f
                                val scaledY = y * 0.8f
                                return FloatFloatPair(scaledX, scaledY)
                            }
                        }).toShape())
                )
                Image(
                    painter = androidx.compose.ui.res.painterResource(id = eu.espcaa.aviator.R.drawable.welcomefg),
                    contentDescription = "Aviator Background",
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        Column (
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row (
                verticalAlignment = Alignment.CenterVertically,
            ) {

                Text(
                    text = "Aviator",
                    style = MaterialTheme.typography.displayLargeEmphasized.copy(
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Default,
                        color = MaterialTheme.colorScheme.onBackground,
                        textGeometricTransform = TextGeometricTransform(scaleX = 1.5f)
                    ),
                )
            }
            Text(
                text = "A new way to manage your flights",
                style = MaterialTheme.typography.bodySmall,
                color = colorScheme.primary
            )
            Row (
                Modifier.fillMaxWidth().padding(top = 32.dp),

                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally)
            ) {
            Button(
                modifier = Modifier.height(48.dp),
                onClick = {
                    navController.navigate("login")
                },
            ) {
                Text(
                    text = "Login",
                    style = MaterialTheme.typography.bodyLarge,
                    color = colorScheme.onPrimary
                )
            }
            Button(
                modifier = Modifier.height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorScheme.primary,
                    contentColor = colorScheme.onPrimary
                ),

                onClick = {
                    navController.navigate("register")
                },
            ) {
                Text(
                    text = "Register",
                    style = MaterialTheme.typography.bodyLargeEmphasized,
                    color = colorScheme.onPrimary
                )
            }
            }
        }
        }
    }
}