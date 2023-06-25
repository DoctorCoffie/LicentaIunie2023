package ro.optistudy.core.providers.noise

import android.util.Log
import androidx.compose.ui.text.AnnotatedString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import ro.optistudy.core.audio.AudioRecorder
import ro.optistudy.core.utils.SensorsConstants
import ro.optistudy.core.providers.config.SensorConfigProvider
import ro.optistudy.core.models.SensorPacketConfig
import ro.optistudy.core.models.ModelSensorPacket
import ro.optistudy.core.models.ModelSensor
import ro.optistudy.core.utils.SensorUtils


class NoiseProvider {

    companion object {
        private var sNoiseProvider: NoiseProvider? = null

        private val lock = Any()

        fun getInstance(): NoiseProvider {
            synchronized(lock) {
                if (sNoiseProvider == null) {
                    sNoiseProvider = NoiseProvider()
                }
                return sNoiseProvider!!
            }
        }
    }

    private var mIsAttached: Boolean = false
    private var mIsRunningPeriod: Boolean = false

    var mDefaultScope = CoroutineScope(Job() + Dispatchers.Default)

    private val _mNoiseDbFlow = MutableSharedFlow<ModelSensorPacket>(replay = 1)
    val mNoisePacketFlow = _mNoiseDbFlow.asSharedFlow()

    private val _mNoiseSensorFlow = MutableSharedFlow<List<ModelSensor>>(replay = 1)
    val mNoiseSensorFlow = _mNoiseSensorFlow.asSharedFlow()

    var mRecorder: AudioRecorder? = null;

    var _mJob1: Job? = null
    var _mJob2: Job? = null

    var mEMA = 0.00

    fun listenSensor() {
        mDefaultScope.launch {
            _mNoiseSensorFlow.emit(
                mutableListOf(
                    SensorUtils.generateEmptySensorModelBy(
                        SensorsConstants.TYPE_NOISE
                    )
                )
            )
        }
    }

    fun setNoiseRecorder(recorder: AudioRecorder?): NoiseProvider {
        Log.d("NoiseProvider", "set recorder")
        mRecorder = recorder
        return this
    }

    fun attachSensor(config: SensorPacketConfig?): NoiseProvider {
        Log.d("NoiseProvider", " # attach sensor config $config")
        if (config == null) return this

        if (mRecorder != null && !mIsAttached) {
            mIsAttached = true
            Log.d("NoiseProvider", "attachSensor")

            mEMA = 0.0;
            mRecorder!!.start()

            if (!mIsRunningPeriod) {
                mIsRunningPeriod = true

                _mJob1 = mDefaultScope.launch {
                    SensorConfigProvider.getInstance().addOrUpdate(config!!.sensorType)
                    delay(100)
                    runPeriodicTask()
                }
            }
        } else {
            Log.d("[INVALID]", "-- #### Already attached or NO recorder set")
        }

        return this
    }

    fun detachSensor(): NoiseProvider {
        if (mRecorder != null && mIsAttached) {
            Log.d("NoiseProvider", "dettachSensor")
            mIsAttached = false

            mEMA = 0.0;
            mRecorder?.stop()

            mDefaultScope.launch {
                val sensorPacket = generateSensorPacket(0.0F)
                _mNoiseDbFlow.emit(sensorPacket)
            }
            SensorConfigProvider.getInstance().removeActive(SensorsConstants.TYPE_NOISE)

            mIsRunningPeriod = false;
            _mJob1?.cancel()
            _mJob2?.cancel()
        } else {
            mRecorder?.stop()

            Log.d("[INVALID]", "-- #### Inactive or NO recorder set")
        }

        return this
    }

    private fun runPeriodicTask() {
        _mJob2 = mDefaultScope.launch {
            if (this.isActive) {
                val db = soundDb().toBigDecimal().toFloat()
                Log.d("[NOISE]", " - noise level: $db db")

                val sensorPacket = generateSensorPacket(db)
                _mNoiseDbFlow.emit(
                    sensorPacket
                )

                delay(800L)
                if (mIsRunningPeriod) {
                    runPeriodicTask()
                }
            }
        }
    }

    private fun generateSensorPacket(value: Float): ModelSensorPacket {
        val sensorConfig = SensorConfigProvider.getInstance().getActiveSensorConfigOrDefault(
            SensorsConstants.TYPE_NOISE)

        return ModelSensorPacket(
                null,
                floatArrayOf(value),
                SensorsConstants.TYPE_NOISE,
                sensorConfig.sensorDelay,
                sensorConfig,
                System.currentTimeMillis(),
                SensorsConstants.getUnit(AnnotatedString.Builder(), SensorsConstants.TYPE_NOISE).toAnnotatedString().text
            )
    }

    private fun getAmplitudeEMA(): Double {
        val amp = getAmplitude()
        Log.d("[NOISE-AMPL]", " - amplitude is: $amp (EMA: $mEMA)")
        mEMA = 0.6 * amp + (1.0 - 0.6) * mEMA
        return mEMA
    }

    fun getAmplitude(): Double {
        return if (mRecorder != null) mRecorder!!.getMaxAmplitude() else 0.00
    }

    fun soundDb(): Double {
        var amplitude = getAmplitudeEMA()
        if (amplitude <= 0) {
            return 0.00
        }
        return 20 * Math.log10(amplitude)
    }

    fun clearAll() {
        _mJob1?.cancel()
        _mJob2?.cancel()

        if (mRecorder != null) {
            mRecorder!!.clear();
        }
    }
}