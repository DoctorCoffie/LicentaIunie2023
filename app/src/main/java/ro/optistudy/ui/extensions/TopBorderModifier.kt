package ro.optistudy.ui.extensions

import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

fun Modifier.topBorder(strokeWidth: Dp, color: Color) = composed(
    factory = {
        val density = LocalDensity.current
        val strokeWidthPx = density.run { strokeWidth.toPx() }

        Modifier.drawBehind {
            val width = size.width
            val height = size.height - strokeWidthPx/2
            val offset = 0 + strokeWidthPx/2

            drawLine(
                color = color,
                start = Offset(x = 0f, y = offset),
                end = Offset(x = width , y = offset),
                strokeWidth = strokeWidthPx
            )
        }
    }
)