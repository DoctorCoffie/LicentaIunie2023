package ro.optistudy.core.utils

import ro.optistudy.core.models.ModelSensor

class SensorUtils {

    companion object {
        fun generateEmptySensorModelBy(sensorType: Int): ModelSensor {
            return ModelSensor.Builder().type(sensorType).build()
        }
    }
}