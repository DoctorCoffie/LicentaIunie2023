package ro.optistudy.ui.resource.sensors

import android.hardware.Sensor

import android.util.SparseIntArray
import ro.optistudy.R
import ro.optistudy.core.utils.SensorsConstants

object SensorsIcons {

    val MAP_TYPE_TO_ICON: SparseIntArray = object : SparseIntArray() {
        init {
            put(Sensor.TYPE_ACCELEROMETER, R.drawable.ic_sensor_compass) //1
            put(Sensor.TYPE_MAGNETIC_FIELD, R.drawable.ic_sensor_magnet) //2
            put(Sensor.TYPE_ORIENTATION, R.drawable.ic_sensor_compass) //3
            put(Sensor.TYPE_GYROSCOPE, R.drawable.ic_sensor_compass) //4
            put(Sensor.TYPE_LIGHT, R.drawable.ic_sensor_brightness) //5
            put(Sensor.TYPE_PRESSURE, R.drawable.ic_sensor_pressure) //6
            put(Sensor.TYPE_TEMPERATURE, R.drawable.ic_sensor_temprature) //7
            put(Sensor.TYPE_PROXIMITY, R.drawable.ic_sensor_proximity) //8
            put(Sensor.TYPE_GRAVITY, R.drawable.ic_sensor_gravity) //9
            put(Sensor.TYPE_LINEAR_ACCELERATION, R.drawable.ic_sensor_linear_acceleration) //10
            put(Sensor.TYPE_ROTATION_VECTOR, R.drawable.ic_sensor_rotation) //11
            put(Sensor.TYPE_RELATIVE_HUMIDITY, R.drawable.ic_sensor_humidity) //12
            put(Sensor.TYPE_AMBIENT_TEMPERATURE, R.drawable.ic_sensor_temperature) //13
            put(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED, R.drawable.ic_sensor_magnet) //14
            put(Sensor.TYPE_GAME_ROTATION_VECTOR, R.drawable.ic_sensor_rotation) //15
            put(Sensor.TYPE_GYROSCOPE_UNCALIBRATED, R.drawable.ic_sensor_compass) //16
            put(Sensor.TYPE_SIGNIFICANT_MOTION, R.drawable.ic_sensor_unknown) //17
            put(Sensor.TYPE_STEP_DETECTOR, R.drawable.ic_sensor_unknown) //18
            put(Sensor.TYPE_STEP_COUNTER, R.drawable.ic_sensor_unknown) //19
            put(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR, R.drawable.ic_sensor_rotation) //20
            put(Sensor.TYPE_HEART_RATE, R.drawable.ic_sensor_unknown) //21

            put(SensorsConstants.TYPE_NOISE, R.drawable.ic_sensor_sound)//100
        }
    }
}