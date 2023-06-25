package ro.optistudy.ui.navigation

import android.hardware.Sensor
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ro.optistudy.ui.pages.about.AboutPage
import ro.optistudy.ui.pages.home.HomePage
import ro.optistudy.ui.pages.splash.SplashPage
import ro.optistudy.ui.pages.sensor.SensorPage

sealed class NavDirections(val route: String) {
    object Root : NavDirections("root")
    object Splash : NavDirections("splash_page")
    object HomePage : NavDirections("home_page")
    object SensorDetailPage : NavDirections("sensor_page")
    object AboutPage : NavDirections("about_page")
}

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = NavDirections.Splash.route) {

        composable(NavDirections.Splash.route) {
            SplashPage(navController)
        }
        composable(NavDirections.HomePage.route) {
            HomePage(
                navController = navController
            )
        }
        composable(NavDirections.AboutPage.route) {
            AboutPage(
                navController = navController
            )
        }
        composable("${NavDirections.SensorDetailPage.route}/{type}", listOf(navArgument("type") {
            type = NavType.IntType
        })) {
            SensorPage(
                navController = navController,
                type = it.arguments?.getInt("type") ?: Sensor.TYPE_LIGHT,
            )
        }
    }

}