package ro.optistudy.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import ro.optistudy.R

sealed class NavScreen(
    val route: String,
    @StringRes val title: Int,
    @DrawableRes val image: Int
) {
    object Home: NavScreen(NavDirections.HomePage.route, R.string.home, R.drawable.ic_sensor_home)
    object Light: NavScreen(NavDirections.SensorDetailPage.route + "/5", R.string.light, R.drawable.ic_sensor_brightness)
    object Temperature: NavScreen(NavDirections.SensorDetailPage.route + "/13", R.string.temperature, R.drawable.ic_sensor_temperature)
    object Noise: NavScreen(NavDirections.SensorDetailPage.route + "/100", R.string.noise, R.drawable.ic_sensor_sound)
    object Compass: NavScreen(NavDirections.SensorDetailPage.route + "/1", R.string.gyroscope, R.drawable.ic_sensor_compass)
    object About: NavScreen(NavDirections.AboutPage.route, R.string.about, R.drawable.ic_sensor_about)
}
