package ro.optistudy.core.utils

import android.hardware.Sensor
import android.util.SparseArray
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import ro.optistudy.ui.resource.values.OSResDimens
import ro.optistudy.ui.resource.values.OSResTxtStyles


object SensorsConstants {

    const val TYPE_NOISE = 100
    const val TYPE_NOISE_NAME = "Zgomot"

    const val DETAIL_KEY_Range = "Range"

    val SENSORS = arrayOf(
        Sensor.TYPE_LIGHT,
        Sensor.TYPE_ACCELEROMETER,
        Sensor.TYPE_AMBIENT_TEMPERATURE
    )

    val SENSORS_INCLUSIVE = arrayOf(
        Sensor.TYPE_LIGHT,
        Sensor.TYPE_ACCELEROMETER,
        Sensor.TYPE_AMBIENT_TEMPERATURE,
        TYPE_NOISE
    )

    val MAP_TYPE_TO_NAME: SparseArray<String> = object : SparseArray<String>() {
        init {
            put(Sensor.TYPE_ACCELEROMETER, "Busola") //1
            put(Sensor.TYPE_MAGNETIC_FIELD, "Magnetic") //2
            put(Sensor.TYPE_ORIENTATION, "Orientare") //3
            put(Sensor.TYPE_GYROSCOPE, "Busola") //4
            put(Sensor.TYPE_LIGHT, "Luminozitate") //5
            put(Sensor.TYPE_PRESSURE, "Presiune") //6
            put(Sensor.TYPE_TEMPERATURE, "Temperatura") //7
            put(Sensor.TYPE_PROXIMITY, "Proximitate") //8
            put(Sensor.TYPE_GRAVITY, "Gravitatie") //9
            put(Sensor.TYPE_LINEAR_ACCELERATION, "Acce. Lineara") //10
            put(Sensor.TYPE_ROTATION_VECTOR, "Rotatie") //11
            put(Sensor.TYPE_RELATIVE_HUMIDITY, "Umiditate") //12
            put(Sensor.TYPE_AMBIENT_TEMPERATURE, "Temperatura") //13
            put(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED, "Magnetic") //14
            put(Sensor.TYPE_GAME_ROTATION_VECTOR, "Game Rotation") //15
            put(Sensor.TYPE_GYROSCOPE_UNCALIBRATED, "Gyroscop") //16
            put(Sensor.TYPE_SIGNIFICANT_MOTION, "Motion") //17
            put(Sensor.TYPE_STEP_DETECTOR, "Detector de pasi") //18
            put(Sensor.TYPE_STEP_COUNTER, "Pedometru") //19
            put(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR, "Busola") //20
            put(Sensor.TYPE_HEART_RATE, "Ritm cardiac") //21
            put(Sensor.TYPE_RELATIVE_HUMIDITY, "Umiditate relativa") //21

            put(TYPE_NOISE, "Zgomot ambiental") //100
        }
    }

    val MAP_TYPE_TO_MIN_TRESHOLD: SparseArray<Float> = object : SparseArray<Float>() {
        init {
            put(Sensor.TYPE_LIGHT, 515.0F) //5
            put(Sensor.TYPE_PRESSURE, 0.0F) //6
            put(Sensor.TYPE_GYROSCOPE, 1.0F) //4
            put(Sensor.TYPE_AMBIENT_TEMPERATURE, 21.0F) //13
            put(Sensor.TYPE_MAGNETIC_FIELD, 0.0F) //2
            put(Sensor.TYPE_ACCELEROMETER, 20.0F) //1
            put(TYPE_NOISE, 36.0F) //100
        }
    }

    val MAP_TYPE_TO_MAX_TRESHOLD: SparseArray<Float> = object : SparseArray<Float>() {
        init {
            put(Sensor.TYPE_LIGHT, 700.0F) //5
            put(Sensor.TYPE_PRESSURE, 0.0F) //6
            put(Sensor.TYPE_GYROSCOPE, 12.0F) //4
            put(Sensor.TYPE_AMBIENT_TEMPERATURE, 23.5F) //13
            put(Sensor.TYPE_MAGNETIC_FIELD, 100.0F) //2
            put(Sensor.TYPE_ACCELEROMETER, 120.0F) //1
            put(TYPE_NOISE, 72.0F) //100
        }
    }


    val MAP_TYPE_TO_PERCENTAGE: SparseArray<Boolean> = object : SparseArray<Boolean>() {
        init {
            put(Sensor.TYPE_LIGHT, false) //5
            put(Sensor.TYPE_PRESSURE, false) //6
            put(Sensor.TYPE_GYROSCOPE, false) //4
            put(Sensor.TYPE_AMBIENT_TEMPERATURE, false) //13
            put(Sensor.TYPE_MAGNETIC_FIELD, false) //2
            put(Sensor.TYPE_ACCELEROMETER, false) //1
            put(TYPE_NOISE, false) //100
        }
    }

    fun hasUnit( sensorType: Int): Boolean {
       val hasUnitValue =
            when (sensorType) {
                Sensor.TYPE_ACCELEROMETER -> true
                Sensor.TYPE_MAGNETIC_FIELD -> true
                Sensor.TYPE_ORIENTATION -> true
                Sensor.TYPE_GYROSCOPE -> true
                Sensor.TYPE_LIGHT -> true
                Sensor.TYPE_PRESSURE -> true
                Sensor.TYPE_TEMPERATURE -> true
                Sensor.TYPE_PROXIMITY -> true
                Sensor.TYPE_GRAVITY -> true
                Sensor.TYPE_LINEAR_ACCELERATION -> true
                Sensor.TYPE_RELATIVE_HUMIDITY -> true
                Sensor.TYPE_AMBIENT_TEMPERATURE -> true
                Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED -> true
                Sensor.TYPE_GYROSCOPE_UNCALIBRATED -> true
                TYPE_NOISE -> true
                else -> false
            }


        return hasUnitValue
    }

    fun getUnit(builder: AnnotatedString.Builder, sensorType: Int): AnnotatedString.Builder {
        builder.apply {
            when (sensorType) {
                Sensor.TYPE_ACCELEROMETER -> append(" \u00b0") // getSquaredText(this," m/s", "2")
                Sensor.TYPE_MAGNETIC_FIELD -> append(" \u00B5T")
                Sensor.TYPE_ORIENTATION -> append(" \u00b0")
                Sensor.TYPE_GYROSCOPE -> append(" rad/s")
                Sensor.TYPE_LIGHT -> append(" lx")
                Sensor.TYPE_PRESSURE -> append(" hPa")
                Sensor.TYPE_TEMPERATURE -> append(" \u00b0C")
                Sensor.TYPE_PROXIMITY -> append(" cm")
                Sensor.TYPE_GRAVITY -> getSquaredText(this," m/s", "2")
                Sensor.TYPE_LINEAR_ACCELERATION -> getSquaredText(this," m/s", "2")
                Sensor.TYPE_RELATIVE_HUMIDITY -> append(" %")
                Sensor.TYPE_AMBIENT_TEMPERATURE -> append(" \u00b0C")
                Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED -> append(" \u00B5T")
                Sensor.TYPE_GYROSCOPE_UNCALIBRATED -> append(" rad/s")
                TYPE_NOISE -> append(" db")
                else -> append("")
            }
        }

        return builder;
    }

    fun isPercentage(type: Int): Boolean {
        return MAP_TYPE_TO_PERCENTAGE.get(type) ?: false
    }

    private fun getSquaredText(builder: AnnotatedString.Builder, text: String, supText: String): AnnotatedString.Builder {

        val superscript = SpanStyle(
            baselineShift = BaselineShift.Superscript, // font size of superscript
            fontFamily = OSResTxtStyles.fontsOS,
            fontWeight = FontWeight.Normal,
            fontSize = OSResDimens.sp20
        )

        builder.apply {
            append(text)
            withStyle(superscript){
                append(supText)
            }
        }

        return builder
    }

}