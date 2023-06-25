package ro.optistudy.core.models

import android.hardware.SensorEvent
import androidx.compose.ui.text.AnnotatedString
import ro.optistudy.core.utils.SensorsConstants

data class ModelSensorPacket(
    var sensorEvent: SensorEvent? = null,
    var values: FloatArray?,
    var type: Int,
    var delay: Int,
    var config: SensorPacketConfig?,
    var timestamp: Long,
    var unit: String
    ) {

    init {
        unit = SensorsConstants.getUnit(AnnotatedString.Builder(), type).toAnnotatedString().text
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ModelSensorPacket

        if (sensorEvent != other.sensorEvent) return false
        if (values != null) {
            if (other.values == null) return false
            if (!values.contentEquals(other.values)) return false
        } else if (other.values != null) return false
        if (type != other.type) return false
        if (delay != other.delay) return false
        if (timestamp != other.timestamp) return false

        return true
    }

    override fun hashCode(): Int {
        var result = sensorEvent?.hashCode() ?: 0
        result = 31 * result + (values?.contentHashCode() ?: 0)
        result = 31 * result + type
        result = 31 * result + delay
        result = 31 * result + timestamp.hashCode()
        return result
    }

}