package ro.optistudy.core.providers.packets

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ro.optistudy.core.models.ModelSensorPacket
import ro.optistudy.core.utils.SensorsConstants
import ro.optistudy.core.providers.config.SensorConfigProvider
import ro.optistudy.core.models.SensorPacketConfig
import java.util.Arrays

class SensorPacketsProvider : SensorEventListener {

    companion object {
        private var sSensorPacketsProvider: SensorPacketsProvider? = null

        private val lock = Any()

        fun getInstance(): SensorPacketsProvider {
            synchronized(lock) {
                if (sSensorPacketsProvider == null) {
                    sSensorPacketsProvider = SensorPacketsProvider()
                }
                return sSensorPacketsProvider!!
            }
        }
    }

    var mDefaultScope = CoroutineScope(Job() + Dispatchers.Default)

    private var mSensorManager: SensorManager? = null

    private val _mSensorPacketFlow = MutableSharedFlow<ModelSensorPacket>(replay = 0)
    val mSensorPacketFlow = _mSensorPacketFlow.asSharedFlow()

    private var _mLastSensorPacket: ModelSensorPacket? = null

    private var _mLastGravity: FloatArray? = null
    private var _mLastGeomagnetic: FloatArray? = null

    fun setSensorManager(manager: SensorManager): SensorPacketsProvider {
        mSensorManager = manager
        return this
    }

    fun attachSensor(config: SensorPacketConfig?): SensorPacketsProvider {
        Log.d("SensorPacketsProvider", "attachSensor...")
        if (config == null) return this

        Log.d("SensorPacketsProvider", "attachSensor by type: ${config.sensorType}")
        SensorConfigProvider.getInstance().addOrUpdate(config.sensorType)
        registerSensor(config)

        return this
    }

    fun detachSensor(sensorType: Int): SensorPacketsProvider {
        Log.d("SensorPacketsProvider", "detachSensor..")
        var sensorConfig = SensorConfigProvider.getInstance().getActiveSensorConfig(sensorType)
        if (sensorConfig != null) {
            Log.d("SensorPacketsProvider", "detachSensor by type: $sensorType")
            unregisterSensor(sensorConfig.sensorType)
            val sensorPacket = _mLastSensorPacket?.copy() ?: generateEmptySensorPacket(sensorType)
            sensorPacket.values = floatArrayOf(0.0f)
            mDefaultScope.launch {
                Log.d("SensorPacketsProvider", " - emit last packet with value: ${Arrays.toString(sensorPacket.values)}")
                _mSensorPacketFlow.emit(
                    sensorPacket
                )
            }
            SensorConfigProvider.getInstance().removeActive(sensorConfig.sensorType)
        }
        return this
    }

    private fun unregisterSensor(sensorType: Int) {
        if (mSensorManager == null) return

        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            Log.d("SensorPacketsProvider unsub", " - double sensor $sensorType + ${Sensor.TYPE_MAGNETIC_FIELD}")
            mSensorManager?.unregisterListener(
                this,
                mSensorManager!!.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
            )
        }

        mSensorManager?.unregisterListener(
            this,
            mSensorManager!!.getDefaultSensor(sensorType)
        )
    }

    private fun registerSensor(config: SensorPacketConfig) {
        if (mSensorManager == null) return
        Log.d("SensorPacketsProvider subscribe", "$config.sensorType")
        if (config.sensorType == Sensor.TYPE_ACCELEROMETER) {
            Log.d("SensorPacketsProvider subscribe", " - double sensor ${config.sensorType} + ${Sensor.TYPE_MAGNETIC_FIELD}")
            mSensorManager?.registerListener(
                this,
                mSensorManager!!.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                config.sensorDelay
            )
        }
        mSensorManager?.registerListener(
            this,
            mSensorManager!!.getDefaultSensor(config.sensorType),
            config.sensorDelay
        )
    }

    override fun onSensorChanged(newData: SensorEvent?) {
        if (newData != null) {
            mDefaultScope.launch {
                onSensorEvent(newData)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, type: Int) {
        Log.d("[SENSOR]", "Update accuracy on sensor: $type")
    }

    private suspend fun onSensorEvent(sensorEvent: SensorEvent) {
        synchronized(this) {
            val sensorType = sensorEvent.sensor.type
            var sensorConfig = SensorConfigProvider.getInstance().getActiveSensorConfig(sensorType)

            if (sensorConfig != null) {
                var values: FloatArray? = floatArrayOf(0.0f)
                var sensorPacket: ModelSensorPacket? = null
                if (sensorType == Sensor.TYPE_ACCELEROMETER) {
                    Log.d("[COMPASS]", " - double sensor compute")

                    val accelerometerReading = FloatArray(3)
                    val magnetometerReading = _mLastGeomagnetic ?: FloatArray(3)

                    System.arraycopy(sensorEvent.values, 0, accelerometerReading, 0, sensorEvent.values.size)

                    // Compute the rotation matrix
                    val rotationMatrix = FloatArray(9)
                    SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReading, magnetometerReading)

                    // Get the orientation angles from the rotation matrix
                    val orientationAngles = FloatArray(3)
                    SensorManager.getOrientation(rotationMatrix, orientationAngles)

                    // Extract the individual orientation angles
                    val azimuth = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
                    // val pitch = Math.toDegrees(orientationAngles[1].toDouble()).toFloat()
                    // val roll = Math.toDegrees(orientationAngles[2].toDouble()).toFloat()
                    values = floatArrayOf(azimuth)
                    sensorPacket = generateSensorPacket(sensorEvent, values, sensorConfig)
                } else {
                    sensorPacket = generateSensorPacket(sensorEvent, sensorConfig)
                }

                _mLastSensorPacket = sensorPacket.copy()

                Log.d("SensorPacketsProvider", "emit ${sensorPacket?.type} ${sensorPacket?.unit}")
                mDefaultScope.launch {
                    Log.d("SensorPacketsProvider", " - values: ${Arrays.toString(sensorEvent.values)}")
                    _mSensorPacketFlow.emit(
                        sensorPacket
                    )
                }
            } else {
                if (sensorEvent.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                    val magnetometerReading = FloatArray(3)
                    System.arraycopy(
                        sensorEvent.values,
                        0,
                        magnetometerReading,
                        0,
                        sensorEvent.values.size
                    )
                    _mLastGeomagnetic = magnetometerReading
                } else {
                    Log.d("[SENSOR]", " - unknown sensor")
                }
            }
        }
    }
    fun generateEmptySensorPacket(sensorType: Int): ModelSensorPacket {
        val sensorConfig = SensorConfigProvider.getInstance().getActiveSensorConfigOrDefault(sensorType)
        return ModelSensorPacket(
            null,
            floatArrayOf(0.0f),
            sensorType,
            sensorConfig.sensorDelay,
            sensorConfig,
            System.currentTimeMillis(),
            ""
        )
    }

    private fun generateSensorPacket(sensorEvent: SensorEvent, sensorConfig: SensorPacketConfig): ModelSensorPacket {
        return ModelSensorPacket(
            sensorEvent,
            sensorEvent.values,
            sensorEvent.sensor.type,
            sensorConfig.sensorDelay,
            sensorConfig,
            System.currentTimeMillis(),
            ""
        )
    }

    private fun generateSensorPacket(sensorEvent: SensorEvent, values: FloatArray, sensorConfig: SensorPacketConfig): ModelSensorPacket {
        return ModelSensorPacket(
            sensorEvent,
            values,
            sensorEvent.sensor.type,
            sensorConfig.sensorDelay,
            sensorConfig,
            System.currentTimeMillis(),
            ""
        )
    }

    fun clearAll() {
        val activeSensorTypes = SensorConfigProvider.getInstance()
            .getActiveSensorTypes(listOf(SensorsConstants.TYPE_NOISE))
        if (activeSensorTypes.isNotEmpty()) {
            for (sensorType in activeSensorTypes){
                Log.d("SensorPacketsProvider","clearAll unregisterSensor")
                unregisterSensor(sensorType)
            }
        }
        mSensorManager = null
    }

}


