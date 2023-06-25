package ro.optistudy.ui.pages.sensor

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ro.optistudy.core.models.ModelSensorPacket
import ro.optistudy.core.providers.config.SensorConfigProvider
import ro.optistudy.core.providers.noise.NoiseProvider
import ro.optistudy.core.providers.packets.SensorPacketsProvider
import ro.optistudy.core.utils.SensorsConstants
import java.util.Arrays
import kotlin.math.sqrt

class SensorViewModel(var mSensorType: Int) : ViewModel() {

    private val _mSensorUnit = MutableSharedFlow<String>(replay = 1)
    val mSensorUnit = _mSensorUnit.asSharedFlow()

    private val _mSensorModulus = MutableSharedFlow<Float>(replay = 1)
    val mSensorModulus = _mSensorModulus.asSharedFlow()

    private val _mSensorTreshold = MutableSharedFlow<Int>(replay = 1)
    val mSensorTreshold = _mSensorTreshold.asSharedFlow()

    private var mLatestPacket: ModelSensorPacket? = null

    var mDefaultScope = CoroutineScope(Job() + Dispatchers.Default)
    private var _mJob: Job? = null

    init {
        Log.d("SensorViewModel", "init")
        initializeFlow()
    }

    private fun initializeFlow() {
        val sensorPacketFlow = if (mSensorType == SensorsConstants.TYPE_NOISE)
            NoiseProvider.getInstance().mNoisePacketFlow
            else SensorPacketsProvider.getInstance().mSensorPacketFlow

        if (mSensorType == SensorsConstants.TYPE_NOISE) {
            Log.d("SensorViewModel", " - init noise")
            if (!SensorConfigProvider.getInstance().isActiveSensor(mSensorType)) {
                NoiseProvider.getInstance().attachSensor(
                    SensorConfigProvider.getInstance().generateSensorConfig(mSensorType)
                )
            }
        } else {
            Log.d("SensorViewModel", " - init sensor")
            if (!SensorConfigProvider.getInstance().isActiveSensor(mSensorType)) {
                Log.d("SensorViewModel", " - attach sensor to listen to ${mSensorType}")
                SensorPacketsProvider.getInstance().attachSensor(
                    SensorConfigProvider.getInstance().generateSensorConfig(mSensorType)
                )
            }
        }

        _mJob = mDefaultScope.launch {
            Log.d("SensorViewModel", " - listen to sensor packets")
            // This is just to fetch just the different events every 1000ms, rate limit
            sensorPacketFlow.collect {
                if (it.type == mSensorType) {
                    Log.d("SensorViewModel", " - packet: ${it?.type}  ${it?.unit}")
                    mLatestPacket = computeValues(it.copy())
                } else {
                    Log.d("SensorViewModel", " - not my packet: ${it?.type} != ${mSensorType}")
                }
            }
        }
    }

    fun computeValues(packet: ModelSensorPacket): ModelSensorPacket {
        var valueRms = 0.0f
        var treshold = 0
        var unit = " %"

        Log.d("receiveSensorPacket", " -- packet we have ${packet?.type}")
        if (packet != null) {
            packet?.values?.let {
                Log.d("compute receiveSensorPacket", " --- packet values: ${Arrays.toString(it)}")

                var output = if (it.size > 1) {
                    sqrt((it.fold(0.0f) { acc, fl -> acc + fl * fl }).toDouble()).toFloat()
                } else if (it.size == 1) {
                    it.first()
                } else {
                    0.0f
                }

                if (packet.config!!.percentage) {
                    // sensor.sensor.maxmiumRange
                    val percent =
                        Math.round(output * 100.0f).toFloat() / (packet.sensorEvent?.sensor?.maximumRange ?: 100.0f)
                    valueRms = Math.round(percent * 100.0f).toFloat() / 100.00f
                } else {
                    unit = packet.unit
                    valueRms = Math.round(output * 100.0f).toFloat() / 100.00f
                }

                // Compute treshhold values
                val rms = valueRms ?: 0.0f
                if (rms < (packet.config?.minTreshold ?: 0.0f)) {
                   treshold = -1
                }
                if (rms > (packet.config?.maxTreshold ?: 0.0f)) {
                   treshold = 1
                }
                Log.d("TRESHOLD","-- type: ${packet?.type} treshold: $treshold")
            }
            mDefaultScope.launch {
                _mSensorModulus.emit(valueRms)
                _mSensorTreshold.emit(treshold)
                _mSensorUnit.emit(unit)
            }
        }

        return packet
    }

    fun onBackStackEntryChanged(backStackEntry: NavBackStackEntry) {
        // Check if the destination has changed or if a composable is removed from view
        val destinationChanged = backStackEntry.destination.route

        // Handle the changes as needed
        if (destinationChanged != null) {
            // Do something when the destination changes
            //_mJob?.cancel()
            Log.d("changenav", "changed nav destination")
        }
    }

    fun clear() {
        _mJob?.cancel()
        Log.d("DISPOSE", "## -- dispose and clear subs hopefully")
    }

}
