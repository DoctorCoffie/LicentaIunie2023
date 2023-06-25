package ro.optistudy.ui.pages.home

import android.hardware.Sensor
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch
import ro.optistudy.core.models.ModelSensor
import ro.optistudy.core.providers.config.SensorConfigProvider
import ro.optistudy.core.providers.firebase.FirebaseProvider
import ro.optistudy.core.providers.noise.NoiseProvider
import ro.optistudy.core.providers.packets.SensorPacketsProvider
import ro.optistudy.core.providers.sensor.SensorsProvider
import ro.optistudy.core.utils.SensorsConstants
import ro.optistudy.ui.pages.home.model.ModelHomeSensor
import ro.optistudy.ui.pages.home.state.SensorTresholdState
import java.time.ZonedDateTime
import java.util.Arrays
import kotlin.math.sqrt

class HomeViewModel : ViewModel() {

    private var mSensors: MutableList<ModelHomeSensor> = mutableListOf()
    private val _mSensorsList = mutableStateListOf<ModelHomeSensor>()
    val mSensorsList: SnapshotStateList<ModelHomeSensor> = _mSensorsList

    private val _mActiveSensorList = mutableListOf<ModelHomeSensor>()

    private val mIsActiveMap = mutableMapOf<Int, Boolean>(
        Pair(Sensor.TYPE_LIGHT, true),
        Pair(Sensor.TYPE_AMBIENT_TEMPERATURE, false),
        Pair(Sensor.TYPE_PRESSURE, false),
        Pair(Sensor.TYPE_GYROSCOPE, false),
        Pair(SensorsConstants.TYPE_NOISE, false)
    )

    private val _mTresholdStateFlow = MutableSharedFlow<SensorTresholdState>(replay = 1)
    val mTresholdStateFlow = merge(
        _mTresholdStateFlow.asSharedFlow().distinctUntilChanged().filter {
            it.treshold == 0
        },
        _mTresholdStateFlow.asSharedFlow().distinctUntilChanged().filter {
            it.treshold != 0
        }
    )

    var _mJob1: Job? = null
    var _mJob2: Job? = null

    var _mLastPacketTimestamp: Long = 0

    init {
        Log.d("HomeViewModel", "home viewmodel init")

        // Get all identified and filtered sensors
        _mJob1 = viewModelScope.launch {
            var sensorsFlow = combine(
                SensorsProvider.getInstance().mSensorsFlow,
                NoiseProvider.getInstance().mNoiseSensorFlow
            ) { a,b ->
                val result: MutableList<ModelSensor> = ArrayList()
                result+=a
                result+=b
                // Log.d("COMBINE", "- ###### lists resulted: $result")
                return@combine result }

            // get sensors and map them to current ModelHomeSensor model
            sensorsFlow.map { list ->
                list.map {
                    ModelHomeSensor(
                        it.type,
                        it.sensor,
                        it.info,
                        0f,
                        detectActiveByType(it.type),
                        "",
                        "",
                        SensorConfigProvider.getInstance().getActiveSensorConfigOrDefault(it.type),
                        it.sensor?.maximumRange?:0.0f
                    )
                }.toMutableList()
            }.collectLatest {
                // Log.d("HomeViewModel","${this@HomeViewModel} init sensors active: $mIsActiveMap")
                // Log.d("HomeViewModel", "sensors: $it")

                mSensors = it

                // Add to the list used here, only once
                if (_mSensorsList.size == 0) {
                    _mSensorsList.addAll(mSensors)

                    var activeSensors = it.filter { modelHomeSensor -> modelHomeSensor.isActive }
                    _mActiveSensorList.addAll(activeSensors)

                    initializeFlow()
                }
            }
        }

        // Start identifying all sensors, filtered with the types we want
        NoiseProvider.getInstance().listenSensor()
        SensorsProvider.getInstance().listenSensors()
    }

    private fun detectActiveByType(type: Int): Boolean {
        val isActiveOnHomeScreen = mIsActiveMap.getOrDefault(type, false)
        if (isActiveOnHomeScreen)
            return true

        val activeByConfig = SensorConfigProvider.getInstance().isActiveSensor(type)
        if (activeByConfig) {
            mIsActiveMap[type] = activeByConfig
        }
        return activeByConfig
    }

    private suspend fun initializeFlow() {
        var sensorPacketFlow =
            SensorPacketsProvider.getInstance().mSensorPacketFlow
        var noisePacketFlow =
            NoiseProvider.getInstance().mNoisePacketFlow

        var packetsFlow = merge(sensorPacketFlow, noisePacketFlow)

        // Listen to sensors
        for (sensor in _mActiveSensorList) {
            attachPacketListener(sensor)
        }

        // Read sensor changes (packets), map them to current Model (ModelHomeSensor)
        // and update view if sensor is active
        _mJob2 = viewModelScope.launch {
            packetsFlow.collect {
                Log.d("HomeViewModel", "read Packet from flows: ")

                var index = mSensors.indexOfFirst { sensor -> sensor.type == it.type }
                if (index >= 0 && mIsActiveMap.getOrDefault(it.type, false)) {
                    // Log.d("HomeViewModel", "onSensorChecked: Index: $index")
                    val sensorPacket = it
                    var sensor = mSensors[index]
                    var updatedSensor =
                        ModelHomeSensor(
                            sensor.type,
                            sensor.sensor,
                            sensor.info,
                            sensor.valueRms,
                            mIsActiveMap.getOrDefault(sensor.type, false),
                            sensor.name,
                            it.unit,
                            sensor.config,
                            sensorPacket.sensorEvent?.sensor?.maximumRange ?: 100.0f
                        )

                    it.values?.let {
                        Log.d("Compute and emit value: ", "${Arrays.toString(it)}")
                        var output = if (it.size > 1) {
                            sqrt((it.fold(0.0f) { acc, fl -> acc + fl * fl }).toDouble()).toFloat()
                        } else if (it.size == 1) {
                            it.first()
                        } else {
                            0.0f
                        }
                        if (sensor.config!!.percentage) {
                            // sensor.sensor.maxmiumRange
                            val percent =
                                Math.round(output * 100.0f).toFloat() / (updatedSensor.maxRange
                                    ?: 100.0f)
                            updatedSensor.valueRms =
                                Math.round(percent * 100.0f).toFloat() / 100.00f
                        } else {
                            updatedSensor.valueRms =
                                Math.round(output * 100.0f).toFloat() / 100.00f
                        }

                        // Compute treshhold values
                        val rms = updatedSensor.valueRms ?: 0.0f
                        updatedSensor.config?.treshold = 0

                        if (rms < (updatedSensor.config?.minTreshold ?: 0.0f)) {
                            updatedSensor.config?.treshold = -1
                        }

                        if (rms > (updatedSensor.config?.maxTreshold ?: 0.0f)) {
                            updatedSensor.config?.treshold = 1
                        }

                        var tresholdValue = SensorTresholdState(updatedSensor.type, 0)
                        if ((updatedSensor.config?.treshold ?: 0) != 0) {
                            tresholdValue = SensorTresholdState(
                                updatedSensor.type,
                                updatedSensor.config?.treshold
                            )
                        }
                        // Log.d("H-TRESHOLD", "-- type: ${updatedSensor.type} treshold: ${updatedSensor.config?.treshold}")

                        var sendToFirebase = false;
                        val nowMillis = ZonedDateTime.now().toInstant().toEpochMilli()
                        if (tresholdValue.treshold == 0 && (nowMillis - _mLastPacketTimestamp > 5000)) {
                            _mLastPacketTimestamp = nowMillis
                            _mTresholdStateFlow.emit(
                                tresholdValue
                            )
                            sendToFirebase = true;
                        } else {
                            if (tresholdValue.treshold != 0) {
                                _mTresholdStateFlow.emit(
                                    tresholdValue
                                )
                                sendToFirebase = true
                            }
                        }

                        if (sendToFirebase) {
                            FirebaseProvider.getInstance().emit(tresholdValue.treshold?:0)
                        }
                    }

                    mSensors[index] = updatedSensor
                    mSensorsList[index] = updatedSensor
                }
            }
        }
    }

    fun onSensorSwitch(type: Int, isChecked: Boolean) {
        var isCheckedPrev = mIsActiveMap.getOrDefault(type, false)

        if (isCheckedPrev != isChecked) {
            mIsActiveMap[type] = isChecked
        }

        var index = mSensors.indexOfFirst { it.type == type }
        if (index >= 0) {
            // Log.d("HomeViewModel", "onSensorChecked: Index: $index $isChecked")
            var sensor = mSensors[index]
            var updatedSensor =
                ModelHomeSensor(
                    sensor.type,
                    sensor.sensor,
                    sensor.info,
                    sensor.valueRms,
                    isChecked,
                    sensor.name,
                    sensor.unit,
                    sensor.config
                )
            mSensors[index] = updatedSensor

            mSensorsList[index] = updatedSensor
            updateActiveSensor(updatedSensor, isChecked)
        }
    }

    private fun updateActiveSensor(sensor: ModelHomeSensor, isChecked: Boolean = false) {
        var index = _mActiveSensorList.indexOfFirst { it.type == sensor.type }

        if (!isChecked && index >= 0) {
            detachPacketListener(sensor)
            _mActiveSensorList.removeAt(index)

        } else if (isChecked && index < 0) {
            _mActiveSensorList.add(sensor)
            attachPacketListener(sensor)
        }
    }

    private fun attachPacketListener(sensor: ModelHomeSensor) {
        Log.d("HomeViewModel", "attachPacketListener: ${sensor.type}")
        if (!SensorConfigProvider.getInstance().isActiveSensor(sensor.type)) {
            Log.d("HomeViewModel", " - inactive at the moment : ${sensor.type}")
            if (sensor.type == SensorsConstants.TYPE_NOISE) {
                NoiseProvider.getInstance().attachSensor(
                    SensorConfigProvider.getInstance().getActiveSensorConfigOrDefault(sensor.type)
                )
            } else {
                SensorPacketsProvider.getInstance().attachSensor(
                    SensorConfigProvider.getInstance().getActiveSensorConfigOrDefault(sensor.type)
                )
            }
        } else {
            Log.d("HomeViewModel", " - sensor already active: ${sensor.type}")
        }
    }

    private fun detachPacketListener(sensor: ModelHomeSensor) {
        if (sensor.type == SensorsConstants.TYPE_NOISE) {
            NoiseProvider.getInstance().detachSensor()
        } else {
            SensorPacketsProvider.getInstance().detachSensor(
                sensor.type
            )
        }
    }

    fun clear() {
        _mJob1?.cancel()
        _mJob2?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("HomeViewModel", "onCleared")
    }
}