package ro.optistudy.core.providers.config

import android.hardware.SensorManager
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ro.optistudy.core.models.SensorPacketConfig
import ro.optistudy.core.utils.SensorsConstants

class SensorConfigProvider {

    companion object {
        private var sSensorConfigProvider: SensorConfigProvider? = null

        private val lock = Any()

        fun getInstance(): SensorConfigProvider {
            synchronized(lock) {
                if (sSensorConfigProvider == null) {
                    sSensorConfigProvider = SensorConfigProvider()
                }
                return sSensorConfigProvider!!
            }
        }
    }

    private var mAllSensorConfigs: MutableList<SensorPacketConfig> = mutableListOf()

    private var mActiveSensorConfigs: MutableList<SensorPacketConfig> = mutableListOf()

    private val _mSensorConfigsFlow = MutableSharedFlow<List<SensorPacketConfig>>(replay = 1)
    val mSensorConfigsFlow = _mSensorConfigsFlow.asSharedFlow()

    var mDefaultScope = CoroutineScope(Job() + Dispatchers.Default)

    fun initialize() {
        for (index in SensorsConstants.SENSORS_INCLUSIVE.indices) {
            mAllSensorConfigs.add(
                generateSensorConfig(
                    SensorsConstants.SENSORS_INCLUSIVE[index]
                )
            )
        }
    }

    fun isActiveSensor(sensorType: Int): Boolean {
        val index = mActiveSensorConfigs.indexOfFirst { it.sensorType == sensorType }
        return index >= 0
    }

    fun getActiveSensorTypes(excludes: List<Int>?): List<Int> {
        val activeSensorConfigTypes = mActiveSensorConfigs.map {
            return@map it.sensorType
        }.toList()

        if (!excludes.isNullOrEmpty()) {
            return activeSensorConfigTypes.filter {
                excludes!!.contains(it)
            }.toList()
        }

        return activeSensorConfigTypes;
    }

    fun addOrUpdate(sensorType: Int) {
        var index = mAllSensorConfigs.indexOfFirst { it.sensorType == sensorType }
        if (index >= 0) {
            var inIndex = mActiveSensorConfigs.indexOfFirst { it.sensorType == sensorType }
            if (inIndex >= 0) {
                mActiveSensorConfigs[inIndex] = mAllSensorConfigs[index]
            } else {
                mActiveSensorConfigs.add(mAllSensorConfigs[index])
            }
            mDefaultScope.launch {
                Log.d("SensorConfigsProvider","add config: $mActiveSensorConfigs[inIndex]")
                _mSensorConfigsFlow.emit(mActiveSensorConfigs)
            }
        }
    }

    fun addOrUpdate(config: SensorPacketConfig) {
        var index = mActiveSensorConfigs.indexOfFirst { it.sensorType == config.sensorType }
        if (index >= 0) {
            mActiveSensorConfigs[index] = config
        } else {
            mActiveSensorConfigs.add(config)
        }

        mDefaultScope.launch {
            Log.d("SensorConfigsProvider","add or update config: $config")
            _mSensorConfigsFlow.emit(mActiveSensorConfigs)
        }
    }

    fun removeActive(sensorType: Int) {
        var index = mAllSensorConfigs.indexOfFirst { it.sensorType == sensorType }
        if (index >= 0) {
            var inIndex = mActiveSensorConfigs.indexOfFirst { it.sensorType == sensorType }
            if (inIndex >= 0) {
                mActiveSensorConfigs.removeAt(inIndex)

                mDefaultScope.launch {
                    Log.d("SensorConfigsProvider", "remove config: $mActiveSensorConfigs[inIndex]")
                    _mSensorConfigsFlow.emit(mActiveSensorConfigs)
                }
            }
        }
    }

    fun removeActive(config: SensorPacketConfig) {
        var index = mActiveSensorConfigs.indexOfFirst { it.sensorType == config.sensorType }
        if (index >= 0) {
            mActiveSensorConfigs.removeAt(index)
        }

        mDefaultScope.launch {
            Log.d("SensorConfigsProvider","remove config: $config")
            _mSensorConfigsFlow.emit(mActiveSensorConfigs)
        }
    }

    fun getActiveSensorConfig(sensorType: Int): SensorPacketConfig? {
        return mActiveSensorConfigs.singleOrNull {
            it.sensorType == sensorType
        }
    }

    fun getActiveSensorConfigOrDefault(sensorType: Int): SensorPacketConfig {
        var config = mActiveSensorConfigs.singleOrNull {
            it.sensorType == sensorType
        }
        if (config == null) {
            config = getSensorConfig(sensorType)
        }

        return config ?: generateSensorConfig(sensorType)
    }

    fun getSensorConfig(sensorType: Int): SensorPacketConfig? {
        return mAllSensorConfigs.singleOrNull {
            it.sensorType == sensorType
        }
    }

    fun clear(list: List<Int>?) {
        if (!list.isNullOrEmpty()) {
            mActiveSensorConfigs.removeIf { list.contains(it.sensorType) }
        }
    }

    fun clearAll() {
        mActiveSensorConfigs = mutableListOf()
    }

    fun generateSensorConfig(sensorType: Int): SensorPacketConfig {
        return SensorPacketConfig.Builder().apply {
            sensorType(sensorType)
            sensorDelay(SensorManager.SENSOR_DELAY_NORMAL)
            percentage(SensorsConstants.isPercentage(sensorType))
            minTreshold(SensorsConstants.MAP_TYPE_TO_MIN_TRESHOLD.get(sensorType))
            maxTreshold(SensorsConstants.MAP_TYPE_TO_MAX_TRESHOLD.get(sensorType))
        }.build()
    }
}