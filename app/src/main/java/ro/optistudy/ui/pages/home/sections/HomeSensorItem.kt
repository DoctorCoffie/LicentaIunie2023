package ro.optistudy.ui.pages.home.sections

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ro.optistudy.R
import ro.optistudy.core.utils.SensorsConstants
import ro.optistudy.ui.extensions.topBorder
import ro.optistudy.ui.pages.home.model.ModelHomeSensor
import ro.optistudy.ui.resource.sensors.SensorsIcons
import ro.optistudy.ui.resource.values.OSResColors
import ro.optistudy.ui.resource.values.OSResDimens
import ro.optistudy.ui.resource.values.OSResShapes
import ro.optistudy.ui.resource.values.OSResTxtStyles

@Preview(showBackground = true, backgroundColor = 0xFFf5f5f5)
@Composable
fun HomeSensorItem(
    modelSensor: ModelHomeSensor = ModelHomeSensor.Builder(-1).build(),
    onClick: (sensorType: Int) -> Unit = {
    },
    onCheckChange: (Int, Boolean) -> Unit = { type: Int, isChecked: Boolean -> }
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(OSResDimens.dp1))
            .clickable(
                enabled = true,
                onClickLabel = "Card Click",
                onClick = {
                    onClick.invoke(modelSensor.type)
                })
            .background(
                color = if (isSystemInDarkTheme()) Color.Black else Color.White
            )
            .border(
                width = OSResDimens.dp1,
                color = if (isSystemInDarkTheme()) OSResColors.NoteDark else Color.LightGray,
                shape = RoundedCornerShape(OSResDimens.dp1)
            )
    ) {

        Column(
            modifier = Modifier.padding(horizontal = 0.dp, vertical = 16.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .clip(CircleShape)
                    .background(Color.Transparent)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = OSResDimens.dp48)
                        .padding(horizontal = 16.dp, vertical = 0.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painterResource(
                            SensorsIcons.MAP_TYPE_TO_ICON.get(
                                modelSensor.type,
                                R.drawable.ic_sensor_home
                            )
                        ),
                        contentDescription = modelSensor.sensor?.name,
                        colorFilter = ColorFilter.tint(
                            if (isSystemInDarkTheme()) Color.White else OSResColors.OSYellow
                        ),

                        modifier = Modifier
                            .width(OSResDimens.dp44)
                            .height(OSResDimens.dp44)
                            .padding(start = 1.dp, end = 1.dp, top = 1.dp, bottom = 1.dp)
                    )
                    Spacer(
                        modifier = Modifier
                            .height(OSResDimens.dp32)
                            .fillMaxWidth(1f)
                            .weight(1f, true)
                    )
                }
            }

            Spacer(
                modifier = Modifier
                    .height(OSResDimens.dp32)
                    .fillMaxWidth(1f)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = OSResDimens.dp36)
                    .padding(horizontal = 16.dp, vertical = 0.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${modelSensor.valueRms}",
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    style = OSResTxtStyles.h2,
                    maxLines = 1,
                    fontWeight = FontWeight(600),
                )
                Spacer(modifier = OSResShapes.Space.H4)
                Text(
                    text = "${modelSensor.unit}",
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Left,
                    style = OSResTxtStyles.h5,
                    maxLines = 1,
                    fontWeight = FontWeight(200),
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = OSResDimens.dp16)
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = SensorsConstants.MAP_TYPE_TO_NAME.get(
                        modelSensor.type,
                        modelSensor.sensor?.name ?: "Zgomot"
                    ),
                    color = if (isSystemInDarkTheme()) OSResColors.NoteGray else Color.Black,
                    textAlign = TextAlign.Left,
                    style = OSResTxtStyles.caption,
                    maxLines = 1,
                    fontWeight = FontWeight.Normal,
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = OSResDimens.dp24)
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
                            if (modelSensor.config?.treshold == 0)
                                R.drawable.ic_smiley_happy
                            else
                                R.drawable.ic_smiley_sad
                        ),
                        contentDescription = modelSensor.sensor?.name,
                        colorFilter = ColorFilter.tint(
                            if (modelSensor.config?.treshold == 0)
                                Color.Green else Color.Red
                        ),

                        modifier = Modifier
                            .width(OSResDimens.dp20)
                            .height(OSResDimens.dp20)
                            .padding(start = 1.dp, end = 1.dp, top = 1.dp, bottom = 1.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Switch(
                    checked = modelSensor.isActive,
                    onCheckedChange = {
                        onCheckChange.invoke(modelSensor.type, it)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF13ED6A),
                        uncheckedThumbColor = Color(0xFF898989),
                        checkedTrackColor = Color(0x4D00FF66),
                        uncheckedTrackColor = Color(0x33B1B1B1)
                    ),
                    modifier = Modifier
                        .width(OSResDimens.dp32)
                        .height(OSResDimens.dp16)
                        .padding(end = 16.dp, bottom = 1.dp),
                    )
            }
        }

    }

}
