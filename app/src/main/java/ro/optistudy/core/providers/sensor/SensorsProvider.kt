package ro.optistudy.core.providers.sensor

import android.hardware.Sensor
import android.hardware.SensorManager
import android.util.Log
import ro.optistudy.core.utils.SensorsConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ro.optistudy.core.models.ModelSensor

class SensorsProvider {

    companion object {
        private var sSensorsProvider: SensorsProvider? = null

        private val lock = Any()

        fun getInstance(): SensorsProvider {
            synchronized(lock) {
                if (sSensorsProvider == null) {
                    sSensorsProvider = SensorsProvider()
                }
                return sSensorsProvider!!
            }
        }
    }

    private var mSensorManager: SensorManager? = null

    private var mSensors: List<ModelSensor> = mutableListOf()

    private val _mSensorsFlow = MutableSharedFlow<List<ModelSensor>>(replay = 1)
    val mSensorsFlow = _mSensorsFlow.asSharedFlow()

    var mDefaultScope = CoroutineScope(Job() + Dispatchers.Default)

    fun setSensorManager(manager: SensorManager): SensorsProvider {
        mSensorManager = manager
        manager.getDefaultSensor(100)
        return this
    }

    fun listenSensors(): SensorsProvider {
        Log.d("SensorsProvider","listenSensors: ")
        if (mSensors.isEmpty()) {
            // Get all sensors and filter them, so we have only the ones used (in constants)
            val sensorList = mSensorManager!!.getSensorList(Sensor.TYPE_ALL).filter {
                SensorsConstants.SENSORS.contains(it.type)
            }.distinctBy { it.type }.toList()
            Log.d("SensorProvider", "$sensorList")
            // Add them to the list, with custom mapping of the sensor data
            mSensors = sensorList.map { ModelSensor(it.type, it) }.toList()
        }

        mDefaultScope.launch {
            Log.d("SensorsProvider","listenSensors 2: ${mSensors.size}")
            _mSensorsFlow.emit(mSensors)
        }
        return this
    }

    fun listenSensor(sensorType: Int): Flow<ModelSensor?> {
        var flow = mSensorsFlow.map { sensors -> return@map sensors.singleOrNull { modelSensor ->
            modelSensor.type == sensorType }  }
        return flow
    }

    fun getSensor(sensorType: Int): ModelSensor? {
        return mSensors.singleOrNull { modelSensor ->
            modelSensor.type == sensorType }
    }

    fun clearAll() {
        mSensorManager = null
        mSensors = mutableListOf()
    }
}