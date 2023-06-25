package ro.optistudy.ui.pages.sensor.sections

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ro.optistudy.R
import ro.optistudy.core.models.ModelSensor
import ro.optistudy.core.utils.SensorsConstants
import ro.optistudy.ui.extensions.topBorder
import ro.optistudy.ui.resource.sensors.SensorsIcons
import ro.optistudy.ui.resource.values.OSResColors
import ro.optistudy.ui.resource.values.OSResDimens
import ro.optistudy.ui.resource.values.OSResShapes
import ro.optistudy.ui.resource.values.OSResTxtStyles

@Composable
fun SensorItem(
    type: Int = -1,
    unit: String = " %",
    modelSensor: ModelSensor? = null,
    modelTreshold: Int = 0,
    value: Float = 0.0f
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(OSResDimens.dp1))
            .background(
                color = if (isSystemInDarkTheme()) Color.Black else Color.White
            )
            .border(
                width = OSResDimens.dp1,
                color = if (isSystemInDarkTheme()) OSResColors.NoteDark else Color.LightGray,
                shape = RoundedCornerShape(OSResDimens.dp1)
            )
            .padding(OSResDimens.dp16)
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize(1f)
                    .clip(RectangleShape)
                    .background(Color.Transparent)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .defaultMinSize(minHeight = OSResDimens.dp196)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Image(
                        painterResource(
                            if (modelSensor != null)
                                SensorsIcons.MAP_TYPE_TO_ICON.get(
                                    modelSensor.type,
                                    R.drawable.ic_sensor_home
                                )
                            else R.drawable.ic_sensor_sound
                        ),
                        contentDescription = modelSensor?.name,
                        colorFilter = ColorFilter.tint(
                            if (isSystemInDarkTheme()) Color.White else OSResColors.OSYellow
                        ),

                        modifier = Modifier
                            .width(OSResDimens.dp64)
                            .height(OSResDimens.dp64)
                            .padding(start = 1.dp, end = 1.dp, top = 1.dp, bottom = 1.dp)
                    )
                    Spacer(
                        modifier = Modifier
                            .height(OSResDimens.dp64)
                            .fillMaxWidth(1f)
                            .weight(1f, true)
                    )
                }
            }

            Spacer(
                modifier = Modifier
                    .height(OSResDimens.dp64)
                    .fillMaxWidth(1f)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = OSResDimens.dp64)
                    .padding(horizontal = 16.dp, vertical = 0.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${value}",
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    style = OSResTxtStyles.h1.merge(
                        other = TextStyle(fontSize = OSResDimens.sp48)
                    ),
                    maxLines = 1,
                    fontWeight = FontWeight(600),
                )
                Spacer(modifier = OSResShapes.Space.H4)
                Text(
                    text = "$unit",
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Left,
                    style = OSResTxtStyles.h4,
                    maxLines = 1,
                    fontWeight = FontWeight(200),
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = OSResDimens.dp16)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = if (modelSensor != null)
                        SensorsConstants.MAP_TYPE_TO_NAME.get(
                        modelSensor.type,
                        modelSensor.name ?: "")
                        else ""
                    ,
                    color = if (isSystemInDarkTheme()) OSResColors.NoteGray else Color.Black,
                    textAlign = TextAlign.Left,
                    style = OSResTxtStyles.caption.merge(
                        other = TextStyle(fontSize = OSResDimens.sp20)
                    ),
                    maxLines = 1,
                    fontWeight = FontWeight.Normal,
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = OSResDimens.dp44)
                    .background(
                        if (isSystemInDarkTheme())
                            Brush.verticalGradient(
                                colors = listOf(
                                    OSResColors.NoteWhite.copy(alpha = 0.2f),
                                    Color.Black.copy(alpha = 0.00f),
                                )
                            )
                        else
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.0f),
                                    Color.White.copy(alpha = 0.00f),
                                )
                            )
                    )
                    .topBorder(
                        strokeWidth =
                        if (isSystemInDarkTheme())
                            OSResDimens.dp1
                        else
                            OSResDimens.dp0,
                        if (isSystemInDarkTheme()) OSResColors.NoteDark else Color.LightGray
                    ),
                verticalAlignment = Alignment.Bottom
            ) {

                Column(
                    modifier = Modifier.padding(start = 16.dp, top = 0.dp, end = 0.dp, bottom = 0.dp),
                ) {
                    Image(
                        painterResource(
                            if (modelTreshold == 0)
                                R.drawable.ic_smiley_happy
                            else
                                R.drawable.ic_smiley_sad
                        ),
                        contentDescription = modelSensor?.name,
                        colorFilter = ColorFilter.tint(
                            if (modelTreshold == 0)
                                if (isSystemInDarkTheme()) Color.Green.copy(alpha = 0.6f) else OSResColors.OSGreen30.copy(alpha = 1.0f)
                            else if (modelTreshold == 1)
                                Color.Red
                            else
                                Color.Red.copy(alpha = 0.5f)
                        ),

                        modifier = Modifier
                            .width(OSResDimens.dp32)
                            .height(OSResDimens.dp32)
                            .padding(start = 1.dp, end = 1.dp, top = 1.dp, bottom = 1.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(0.2f))

                Text(
                    text = if (modelTreshold != 0) "Conditii neoptime" else "Conditii optime",
                    fontSize = OSResDimens.sp18,
                    color = if (isSystemInDarkTheme())
                        if (modelTreshold == 0) Color.Green.copy(alpha = 0.6f)
                        else if (modelTreshold == 1) Color.Red else Color.Red.copy(alpha = 0.5f)
                    else
                        if (modelTreshold == 0) OSResColors.OSGreen30.copy(alpha = 1.0f)
                        else if (modelTreshold == 1) OSResColors.NoteDarkRed else OSResColors.NoteDarkRed.copy(alpha = 0.5f)
                )

                Spacer(modifier = Modifier.weight(1f))

            }
        }

    }

}
