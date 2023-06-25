package ro.optistudy.core.audio

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import java.io.File
import java.io.IOException

class OSAudioRecorder(
    private val context: Context,
    private val outputFilePath: File
): AudioRecorder {

    companion object {
        const val RATE = 44100
    }

    private var recorder: MediaRecorder? = null

    private var isRecording: Boolean = false

    private fun createRecorder(): MediaRecorder {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else MediaRecorder()
    }

    private fun createAudioRecordingFile(): File? {
        var tmpRecordingFile: File? = null
        try {
            tmpRecordingFile = File(outputFilePath, "AudioRecording.3gp")
            if (tmpRecordingFile.exists()) {
                tmpRecordingFile.delete();
                tmpRecordingFile = File(outputFilePath, "AudioRecording.3gp")
                tmpRecordingFile.createNewFile()
            }
        } catch (ise:  IllegalStateException) {
            Log.e("[Error]", "IOException: " + ise.message)
        } catch (ioe: IOException) {
            Log.e("[Error]", "SecurityException: " + ioe.message)
        }

        return tmpRecordingFile
    }

    override fun create(outputFile: File?): OSAudioRecorder? {
        if (outputFile != null) {
            createRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setAudioSamplingRate(RATE)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    setOutputFile(outputFile)
                } else {
                    setOutputFile("/dev/null")
                }

                recorder = this
            }
        }

        return this
    }

    override fun getMaxAmplitude(): Double {
        return if (recorder != null) recorder!!.maxAmplitude.toDouble() else 0.0
    }

    override fun start() {
        try {
            create(createAudioRecordingFile())
            recorder!!.prepare()
            recorder!!.start()
            isRecording = true
        } catch (ise: IllegalStateException) {
            Log.d("[Error]", " - Illegal state exception: " + ise.message)
        }
    }

    override fun stop() {
        if (recorder != null) {
            try {
                if (isRecording) {
                    recorder?.stop()
                } else {
                    recorder?.reset()
                }
            } catch (ise: IllegalStateException) {
                Log.d("[Error]", " -- Illegal state exception: " + ise.message)
            }
        }
    }

    override fun clear() {
        stop()
        recorder?.release()
        recorder = null
    }
}