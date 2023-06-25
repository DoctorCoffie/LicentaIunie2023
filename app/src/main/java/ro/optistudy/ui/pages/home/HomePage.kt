package ro.optistudy.ui.pages.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ro.optistudy.R
import ro.optistudy.core.utils.HintUtils
import ro.optistudy.ui.navigation.NavDirections
import ro.optistudy.ui.navigation.OSBottomNavigation
import ro.optistudy.ui.pages.home.sections.createLazyVerticalGrip
import ro.optistudy.ui.resource.values.OSResColors
import ro.optistudy.ui.resource.values.OSResDimens
import ro.optistudy.ui.resource.values.OSResTxtStyles
import kotlin.math.roundToInt

fun getRandomHint(): String {
    val randomIndex = (Math.random() * (HintUtils.HINTS.size -  1)).roundToInt()
    return HintUtils.HINTS[randomIndex];
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, backgroundColor = 0xFF041B11)
@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    navController: NavController? = null,
    viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory()
    )
) {

    val lazyListState = rememberLazyListState()
    val isAtTop = remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex == 0 && lazyListState.firstVisibleItemScrollOffset == 0
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clear()
        }
    }

    Scaffold(
        modifier = Modifier.background(
            if (isSystemInDarkTheme()) Color.Black else Color.Transparent
        ),
        topBar = {
        SmallTopAppBar(
            colors = if (!isAtTop.value) TopAppBarDefaults.mediumTopAppBarColors(
                containerColor = if (isSystemInDarkTheme()) Color.Black else OSResColors.OSYellow.copy(alpha = 1.0f),
            ) else TopAppBarDefaults.mediumTopAppBarColors(
                containerColor = Color.Transparent
            ),
            
            navigationIcon = {
                Box(Modifier.padding(horizontal = OSResDimens.dp20)) {
                    Image(
                        painterResource(
                            id = if (isSystemInDarkTheme()) R.drawable.pic_logo_white else R.drawable.pic_logo_black
                        ),
                        modifier = Modifier
                            .width(OSResDimens.dp32)
                            .height(OSResDimens.dp36),
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds
                    )
                }
            },
            title = {
                Text(
                    text = "OptiStudy",
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    style = OSResTxtStyles.h4,
                    fontWeight = FontWeight(400),
                    modifier = modifier.fillMaxWidth(),
                )
            },
            actions = {
                Box(Modifier.padding(horizontal = OSResDimens.dp20)) {
                    Image(
                        painterResource(id = R.drawable.pic_logo),
                        modifier = Modifier
                            .alpha(0f)
                            .width(OSResDimens.dp32)
                            .height(OSResDimens.dp36),
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds
                    )
                }
            },
        )
    },
    bottomBar = {
        OSBottomNavigation(selectedRoute = NavDirections.HomePage.route, onItemSelected = {
            navController?.navigate(it.route)
        })
    }

    ) {
        createLazyVerticalGrip(viewModel, navController, it)
    }
}


