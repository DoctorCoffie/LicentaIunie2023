package ro.optistudy.ui.pages.sensor

import android.hardware.Sensor
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumedWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowLeft
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import ro.optistudy.R
import ro.optistudy.core.providers.sensor.SensorsProvider
import ro.optistudy.ui.navigation.NavDirections
import ro.optistudy.ui.navigation.OSBottomNavigation
import ro.optistudy.ui.pages.home.getRandomHint
import ro.optistudy.ui.pages.sensor.sections.SensorItem
import ro.optistudy.ui.resource.values.OSResColors
import ro.optistudy.ui.resource.values.OSResDimens
import ro.optistudy.ui.resource.values.OSResTxtStyles

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class
)
@Preview(showBackground = true, backgroundColor = 0xFF041B11)
@Composable
fun SensorPage(
    modifier: Modifier = Modifier,
    navController: NavController? = null,
    type: Int = Sensor.TYPE_LIGHT,
    viewModel: SensorViewModel = viewModel(
        factory = SensorViewModelFactory(
            type
        )
    )
) {
    val currentBackStackEntry = navController?.currentBackStackEntryAsState()

    // Listen for back stack entry changes
    currentBackStackEntry?.value?.let { backStackEntry ->
        // Called whenever the back stack entry changes
        viewModel.onBackStackEntryChanged(backStackEntry)
    }

    val lazyListState = rememberLazyListState()

    var sensorFlowState = SensorsProvider.getInstance().listenSensor(type)
        .collectAsState(initial = SensorsProvider.getInstance().getSensor(type));
    var sensorState = remember {
        sensorFlowState
    }

    val sensorUnitState = viewModel.mSensorUnit.collectAsState(initial = " %")

    var sensorRms = viewModel.mSensorModulus.collectAsState(initial = 0.0f)
    var sensorRmsState = remember { sensorRms }

    var sensorTreshold = viewModel.mSensorTreshold.collectAsState(initial = 0)
    var sensorTresholdState = remember { sensorTreshold }

    val hintState = remember {
        mutableStateOf(getRandomHint())
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clear()
        }
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                colors = if (lazyListState.firstVisibleItemIndex > 0) TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f), //Add your own color here, just to clarify.
                ) else TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color.Transparent
                ),

                modifier = Modifier.padding(horizontal = OSResDimens.dp16),

                navigationIcon = {
                    IconButton(
                        onClick = { navController?.navigateUp() },
                    ) {
                        Icon(Icons.Rounded.KeyboardArrowLeft, "back")
                    }

                },
                title = {
                    Text(
                        text = "OptiStudy",
                        color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                        textAlign = TextAlign.Center,
                        style = OSResTxtStyles.h4,
                        fontWeight = FontWeight(400),
                        modifier = modifier.fillMaxWidth(),
                    )
                }, actions = {
                    Box(Modifier.padding(horizontal = OSResDimens.dp20)) {
                        Image(
                            painterResource(
                                id = if (isSystemInDarkTheme()) R.drawable.pic_logo_white else R.drawable.pic_logo_black
                            ),
                            modifier = Modifier
                                .alpha(0f)
                                .width(OSResDimens.dp32)
                                .height(OSResDimens.dp36),
                            contentDescription = null,
                            contentScale = ContentScale.FillBounds
                        )
                    }
                }
            )
        },

        bottomBar = {
            OSBottomNavigation(selectedRoute = "${NavDirections.SensorDetailPage.route}/${type}", onItemSelected = {
                navController?.navigate(it.route)
            })
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .consumedWindowInsets(it)
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
            contentPadding = it,
            state = lazyListState
        ) {
            item {
                Spacer(modifier = Modifier.height(OSResDimens.dp4))
            }

            item {
                Column(
                    modifier = Modifier
                        .padding(OSResDimens.dp24)
                        .fillMaxSize(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .background(Color.Transparent)
                            .fillMaxSize()
                    ) {
                        SensorItem(
                            type = type,
                            unit = sensorUnitState?.value!!,
                            modelSensor = sensorState?.value,
                            modelTreshold = sensorTresholdState.value,
                            value = sensorRmsState.value
                        )
                    }

                    Spacer(modifier = Modifier.height(OSResDimens.dp32))

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
            }

            item {
                Spacer(modifier = Modifier.height(OSResDimens.dp32))
            }
        }
    }
}