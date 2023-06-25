package ro.optistudy

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import ro.optistudy.core.audio.OSAudioRecorder
import ro.optistudy.core.providers.config.SensorConfigProvider
import ro.optistudy.core.providers.firebase.FirebaseProvider
import ro.optistudy.core.providers.noise.NoiseProvider
import ro.optistudy.core.providers.packets.SensorPacketsProvider
import ro.optistudy.core.providers.sensor.SensorsProvider
import ro.optistudy.ui.navigation.Navigation
import ro.optistudy.ui.resource.themes.OptiStudyM3Theme
import ro.optistudy.ui.resource.values.OSResColors

class MainActivity : ComponentActivity() {

    private val recorder by lazy {
        OSAudioRecorder(applicationContext, cacheDir)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        askForAudioRecordingPermissions()
        SensorConfigProvider.getInstance().initialize()
        NoiseProvider.getInstance().setNoiseRecorder(recorder)
        SensorsProvider.getInstance().setSensorManager(sensorManager)
        SensorPacketsProvider.getInstance().setSensorManager(sensorManager)

        FirebaseProvider.getInstance().initialize(applicationContext)

        lifecycleScope.launch {
            setContent {
                OptiStudyM3Theme {
                    // A surface container using the 'background' color from the theme
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = if (isSystemInDarkTheme()) Color.Black else OSResColors.OSYellow
                    ) {
                        Navigation()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Log.d("[RESUME]", "##### RESUME")
    }

    override fun onPause() {
        super.onPause()
        // Log.d("[PAUSE]", "##### PAUSE")
    }

    override fun onDestroy() {
        super.onDestroy()

        // Log.d("[DESTROY]", "##### DESTROY")

        // unregister sensor manager
        SensorsProvider.getInstance().clearAll();
        SensorPacketsProvider.getInstance().clearAll();
        NoiseProvider.getInstance().clearAll();
        SensorConfigProvider.getInstance().clearAll();
        FirebaseProvider.getInstance().clearAll();
    }

    private fun askForAudioRecordingPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            val permissions = arrayOf(android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE)
            ActivityCompat.requestPermissions(this, permissions,0)
        }
    }
}