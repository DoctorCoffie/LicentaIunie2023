package ro.optistudy.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ro.optistudy.ui.resource.values.OSResColors
import ro.optistudy.ui.resource.values.OSResDimens

@Composable
fun OSBottomNavigation(
    selectedRoute: String,
    onItemSelected: (NavScreen) -> Unit
) {

    val items = listOf(
        NavScreen.Light,
        NavScreen.Temperature,
        NavScreen.About,
        NavScreen.Home,
        NavScreen.Compass,
        NavScreen.Noise,
    ).filter {
        if (selectedRoute != NavScreen.Home.route) {
            return@filter (it.route != NavScreen.About.route)
        }
        return@filter (it.route != NavScreen.Home.route)
    }

    val navShape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(navShape)
            .background(if (isSystemInDarkTheme()) Color.Black else OSResColors.OSYellow)
            .padding(vertical = 22.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {

        items.forEach {
            val isSelected = (it.route == selectedRoute)
            val color = if (isSystemInDarkTheme())
                            if (isSelected) OSResColors.OSYellow else Color.White
                        else
                            if (isSelected) Color.White else Color.Black

            IconButton(onClick = {
                if (!isSelected)
                    onItemSelected(it)
            }) {
                Image(
                    painterResource(it.image),
                    contentDescription = "${it.title}",
                    colorFilter = ColorFilter.tint(color),

                    modifier = Modifier
                        .width(OSResDimens.dp36)
                        .height(OSResDimens.dp36)
                        .padding(start = 1.dp, end = 1.dp, top = 1.dp, bottom = 1.dp)
                )
            }
         }
    }
}