package ro.optistudy.ui.pages.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import ro.optistudy.R
import ro.optistudy.ui.navigation.NavDirections
import ro.optistudy.ui.resource.values.OSResColors
import ro.optistudy.ui.resource.values.OSResShapes
import ro.optistudy.ui.resource.values.OSResTxtStyles


@Composable
fun SplashPage(navController: NavController) {
    LaunchedEffect(key1 = true) {
        delay(timeMillis = 1000)
        navController.popBackStack()
        navController.navigate(NavDirections.HomePage.route)
    }

    SplashScreen()
}

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                if (!isSystemInDarkTheme())
                    Brush.verticalGradient(
                        colors = listOf (
                            OSResColors.OSYellow.copy(alpha = 0.5f),
                            OSResColors.OSYellow.copy(alpha = 0.00f),
                        )
                    )
                else Brush.linearGradient(colors = listOf(Color.Black, Color.Black))
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(
                    id = if (isSystemInDarkTheme()) R.drawable.pic_logo_light else R.drawable.pic_logo_dark
                ),
                contentDescription = "OptiStudy Splash",
                modifier = modifier
                    .size(120.dp)
            )
            Spacer(modifier = OSResShapes.Space.H24)
            Text(
                text = "OPTISTUDY",
                style = OSResTxtStyles.h2.merge(
                    other = TextStyle(
                        color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                )
            )
        }
    }
}