package ro.optistudy.ui.pages.home.model

import android.hardware.Sensor
import androidx.compose.ui.text.AnnotatedString
import ro.optistudy.core.models.SensorPacketConfig
import ro.optistudy.core.utils.SensorsConstants

data class ModelHomeSensor(
    var type: Int = -1,
    var sensor: Sensor? = null,
    var info: Map<String, Any>? = mutableMapOf(),
    var valueRms: Float? = 0.0f,
    var isActive:  Boolean =  false,
    var name: String? = "",
    var unit: String? = "",
    var config: SensorPacketConfig? = null,
    var maxRange: Float? = .0f
) {
    data class Builder(
        var type: Int = -1,
        var sensor: Sensor? = null,
        var info: Map<String, Any>? = mutableMapOf(),
        var valueRms: Float? = 0.0f,
        var isActive: Boolean? =  false,
        var name: String? = "",
        var unit: String? = "",
        var config: SensorPacketConfig? = null,
        var maxRange: Float? = .0f
    ) {
        fun type(type: Int) = apply { this.type = type }
        fun sensor(sensor: Sensor) = apply { this.sensor = sensor }
        fun info(info: Map<String, Any>) = apply { this.info = info }
        fun valueRms(valueRms: Float) = apply { this.valueRms = valueRms }
        fun isActive(isActive: Boolean) = apply { this.isActive = isActive }
        fun name(name: String) = apply { this.name = name }
        fun unit(unit: String) = apply { this.unit = unit }
        fun config(config: SensorPacketConfig) = apply { this.config = config }
        fun maxRange(maxRange: Float) = apply { this.maxRange = maxRange }

        fun build() = ModelHomeSensor(type, sensor, info, valueRms, isActive!!, name, unit, config, maxRange)
    }

    init {
        name = SensorsConstants.MAP_TYPE_TO_NAME.get( type,sensor?.name?:"")
        val isPercentage = config?.percentage?:false
        unit = if (!isPercentage)
            SensorsConstants.getUnit(AnnotatedString.Builder(), type).toAnnotatedString().text
            else " %"
    }
}