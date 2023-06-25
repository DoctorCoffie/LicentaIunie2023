package ro.optistudy.core.models

import android.hardware.SensorManager

data class SensorPacketConfig(
    var sensorType: Int,
    var sensorDelay: Int = SensorManager.SENSOR_DELAY_UI,
    var percentage: Boolean = false,
    var minTreshold: Float?,
    var maxTreshold: Float?,
    var treshold: Int?
) {
    data class Builder(
        var sensorType: Int = 0,
        var sensorDelay: Int = SensorManager.SENSOR_DELAY_UI,
        var percentage: Boolean = false,
        var minTreshold: Float? = 0.0F,
        var maxTreshold: Float? = 0.0F,
        var treshold: Int? = 0
    ) {
        fun sensorType(sensorType: Int) = apply { this.sensorType = sensorType }
        fun sensorDelay(sensorDelay: Int) = apply { this.sensorDelay = sensorDelay }
        fun percentage(percentage: Boolean) = apply { this.percentage = percentage }
        fun minTreshold(minTreshold: Float) = apply { this.minTreshold = minTreshold }
        fun maxTreshold(maxTreshold: Float) = apply { this.maxTreshold = maxTreshold }
        fun treshold(treshold: Int) = apply { this.treshold = treshold }

        fun build() = SensorPacketConfig(sensorType, sensorDelay, percentage, minTreshold, maxTreshold, treshold)
    }
}