package ro.optistudy.core.models

import android.hardware.Sensor
import androidx.compose.ui.text.AnnotatedString
import ro.optistudy.core.utils.SensorsConstants

class ModelSensor(
    var type: Int = -1,
    var sensor: Sensor? = null,
    var info: Map<String, Any> = mutableMapOf(),
    var name: String =  "",
    var unit: String = ""
) {
    data class Builder(
        var type: Int = -1,
        var sensor: Sensor? = null,
        var info: Map<String, Any>? = mutableMapOf(),
        var name: String? = "",
        var unit: String? = ""
    ) {
        fun type(type: Int) = apply { this.type = type }
        fun sensor(sensor: Sensor) = apply { this.sensor = sensor }
        fun info(info: Map<String, Any>) = apply { this.info = info }
        fun name(name: String) = apply { this.name = name }
        fun unit(unit: String) = apply { this.unit = unit }

        fun build() = ModelSensor(type, sensor, info!!, name!!, unit!!)
    }

    init {
        if (sensor != null) {
            name = SensorsConstants.MAP_TYPE_TO_NAME.get( type,sensor?.name?:"")
            unit = SensorsConstants.getUnit(AnnotatedString.Builder(), type).toAnnotatedString().text
            if (info is MutableMap<String, Any>) {
                (info as MutableMap<String, Any>)[SensorsConstants.DETAIL_KEY_Range] =
                    sensor!!.maximumRange
            }
        }
    }
}