package ro.optistudy.ui.resource.values

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import ro.optistudy.R

object OSResTxtStyles {

    val fontsOS = FontFamily(
        Font(R.font.sarala_regular, FontWeight.Light),
        Font(R.font.sarala_regular),
        Font(R.font.sarala_regular, FontWeight.Medium),
        Font(R.font.sarala_bold, FontWeight.Bold)
    )

    val h1 = TextStyle(
        fontFamily = fontsOS,
        fontWeight = FontWeight.Bold,
        fontSize = OSResDimens.sp42
    )

    val h2 = TextStyle(
        fontFamily = fontsOS,
        fontWeight = FontWeight.Normal,
        fontSize = OSResDimens.sp32
    )

    val h3 = TextStyle(
        fontFamily = fontsOS,
        fontWeight = FontWeight.Normal,
        fontSize = OSResDimens.sp28
    )

    val h4 = TextStyle(
        fontFamily = fontsOS,
        fontWeight = FontWeight.Normal,
        fontSize = OSResDimens.sp20
    )

    val h5 = TextStyle(
        fontFamily = fontsOS,
        fontWeight = FontWeight.Normal,
        fontSize = OSResDimens.sp18
    )

    val h6 = TextStyle(
        fontFamily = fontsOS,
        fontWeight = FontWeight.Normal,
        fontSize = OSResDimens.sp14
    )

    val p1 = TextStyle(
        fontFamily = fontsOS,
        fontWeight = FontWeight.Normal,
        fontSize = OSResDimens.sp20
    )

    val p2 = TextStyle(
        fontFamily = fontsOS,
        fontWeight = FontWeight.Normal,
        fontSize = OSResDimens.sp16
    )

    val p3 = TextStyle(
        fontFamily = fontsOS,
        fontWeight = FontWeight.Normal,
        fontSize = OSResDimens.sp12
    )


    val button = TextStyle(
        fontFamily = fontsOS,
        fontWeight = FontWeight.Medium
    )

    val caption = TextStyle(
        fontFamily = fontsOS,
        fontWeight = FontWeight.Normal,
        fontSize = OSResDimens.sp12
    )

}