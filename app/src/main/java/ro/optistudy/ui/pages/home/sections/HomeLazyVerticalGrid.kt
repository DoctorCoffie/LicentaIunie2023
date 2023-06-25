package ro.optistudy.ui.pages.home.sections

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ro.optistudy.R
import ro.optistudy.ui.navigation.NavDirections
import ro.optistudy.ui.pages.home.HomeViewModel
import ro.optistudy.ui.pages.home.state.SensorTresholdState
import ro.optistudy.ui.pages.home.getRandomHint
import ro.optistudy.ui.resource.values.OSResColors
import ro.optistudy.ui.resource.values.OSResDimens


@Composable
fun createLazyVerticalGrip(
    viewModel: HomeViewModel,
    navController: NavController?,
    paddingValues: PaddingValues
) {
    val sensorTresholdState = viewModel.mTresholdStateFlow.collectAsState(initial = SensorTresholdState())

    val hintState = remember {
        mutableStateOf(getRandomHint())
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxSize()
            .background(
                if (!isSystemInDarkTheme())
                    Brush.verticalGradient(
                        colors = listOf(
                            OSResColors.OSYellow.copy(alpha = 0.5f),
                            OSResColors.OSYellow.copy(alpha = 0.00f),
                        )
                    )
                else Brush.linearGradient(colors = listOf(Color.Black, Color.Black))
            ),
        contentPadding = PaddingValues(24.dp)
    ) {

        item(span = { GridItemSpan(maxLineSpan) }) {
            Spacer(modifier = Modifier
                .fillMaxWidth()
                .height(50.dp))
        }

        items(viewModel.mSensorsList.size) { idx ->
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                HomeSensorItem(
                    modelSensor = viewModel.mSensorsList[idx],

                    onCheckChange = { type: Int, isChecked: Boolean ->
                        viewModel.onSensorSwitch(type, isChecked)

                    },
                    onClick = {
                        navController?.navigate("${NavDirections.SensorDetailPage.route}/${it}")
                    }
                )
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(
                modifier = Modifier
                    .padding(
                        start = OSResDimens.dp40, end = OSResDimens.dp32,
                        top = OSResDimens.dp12, bottom = OSResDimens.dp16
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painterResource(
                        if (sensorTresholdState.value?.treshold == 0)
                            R.drawable.ic_smiley_happy
                        else
                            R.drawable.ic_smiley_sad
                    ),
                    contentDescription = "Treshold",
                    colorFilter = ColorFilter.tint(
                        if (sensorTresholdState?.value?.treshold == 0)
                            if (isSystemInDarkTheme()) Color.Green.copy(alpha = 0.6f) else OSResColors.OSGreen30.copy(alpha = 1.0f)
                        else if (sensorTresholdState?.value?.treshold == 1)
                            Color.Red
                        else
                            Color.Red.copy(alpha = 0.5f)
                    ),

                    modifier = Modifier
                        .width(OSResDimens.dp32)
                        .height(OSResDimens.dp32)
                        .padding(start = 1.dp, end = 1.dp, top = 1.dp, bottom = 1.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = if (sensorTresholdState?.value?.treshold != 0) "Conditii neoptime" else "Conditii optime",
                    fontSize = OSResDimens.sp20,
                    color = if (isSystemInDarkTheme())
                        if (sensorTresholdState?.value?.treshold == 0) Color.Green.copy(alpha = 0.6f)
                        else if (sensorTresholdState?.value?.treshold == 1) Color.Red else Color.Red.copy(alpha = 0.5f)
                    else
                        if (sensorTresholdState?.value?.treshold == 0) OSResColors.OSGreen30.copy(alpha = 1.0f)
                        else if (sensorTresholdState?.value?.treshold == 1) OSResColors.NoteDarkRed else OSResColors.NoteDarkRed.copy(alpha = 0.5f)
                )
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Box(
                modifier = Modifier
                    .padding(0.dp)
                    .border(
                        border = BorderStroke(
                            OSResDimens.dp1,
                            if (isSystemInDarkTheme()) OSResColors.NoteDark else Color.LightGray
                        ),
                        shape = MaterialTheme.shapes.extraSmall
                    )
                    .background(
                        color = if (isSystemInDarkTheme()) Color.Black else Color.White
                    ),
            ) {
                Text(
                    text = "${hintState?.value}",
                    fontSize = OSResDimens.sp16,
                    color = if (isSystemInDarkTheme()) Color.LightGray else Color.Black,
                    modifier = Modifier.padding(OSResDimens.dp12)
                )
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Box(
                modifier = Modifier.padding(0.dp).height(OSResDimens.dp48)
            ) {
                Spacer(modifier = Modifier.fillMaxSize())
            }
        }
    }
}