package ro.optistudy.ui.pages.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumedWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ro.optistudy.R
import ro.optistudy.ui.navigation.NavDirections
import ro.optistudy.ui.navigation.OSBottomNavigation
import ro.optistudy.ui.resource.values.OSResColors
import ro.optistudy.ui.resource.values.OSResDimens
import ro.optistudy.ui.resource.values.OSResShapes
import ro.optistudy.ui.resource.values.OSResTxtStyles

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Preview(showBackground = true, backgroundColor = 0xFF041B11)
@Composable
fun AboutPage(modifier: Modifier = Modifier, navController: NavController? = null) {

    val lazyListState = rememberLazyListState()
    val isAtTop = remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex == 0 && lazyListState.firstVisibleItemScrollOffset == 0
        }
    }

    val aboutItems = listOf(
        "OptiStudy by Victor Gojka",
        "Version 1.0",
        "\"Am dezvoltat o aplicație de studiu care utilizează senzori pentru a evalua și oferi feedback cu privire la optimizarea mediului de învățare. Această aplicație analizează factori precum luminositatea, zgomotul și temperatura camerei tale de studiu și îți oferă informații despre cât de optimă este configurația actuală. În funcție de rezultate, poți primi sugestii personalizate pentru a îmbunătăți mediul de studiu, cum ar fi utilizarea unei lampi pentru a crește luminozitatea sau folosirea dopurilor de urechi pentru a reduce zgomotul. Aplicația te ajută să ajustezi toți acești factori pentru a crea un mediu optim pentru concentrare și învățare eficientă.\""
    )

    Scaffold(topBar = {
            SmallTopAppBar(
                colors = if (!isAtTop.value) TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f), //Add your own color here, just to clarify.
                ) else TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = Color.Transparent //Add your own color here, just to clarify.
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
                    androidx.compose.material3.Text(
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
            OSBottomNavigation(selectedRoute = NavDirections.AboutPage.route, onItemSelected = {
                navController?.navigate(it.route)
            })
        }

    ) {
        LazyColumn(
            modifier = Modifier
                .consumedWindowInsets(it)
                .background(
                    if (!isSystemInDarkTheme())
                        Brush.verticalGradient(
                            colors = listOf(
                                OSResColors.OSYellow.copy(alpha = 0.5f),
                                OSResColors.OSYellow.copy(alpha = 0.00f),
                            )
                        )
                    else Brush.linearGradient(colors = listOf(Color.Black, Color.Black))
                )
                .padding(OSResDimens.dp24),
            contentPadding = it,
            state = lazyListState
        ) {
            item {
                Spacer(modifier = OSResShapes.Space.H24)
            }

            // Texts
            items(aboutItems) {
                AboutItem(it)
            }

            item {
                Spacer(modifier = OSResShapes.Space.H24)
            }
        }
    }
}

@Composable
fun AboutItem(text: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
               Color.Transparent
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = OSResTxtStyles.h4.copy(
                color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                fontFamily = FontFamily.Default
            ),
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}
