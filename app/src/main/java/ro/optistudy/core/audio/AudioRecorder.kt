package ro.optistudy.core.audio

import android.media.MediaRecorder
import java.io.File

interface AudioRecorder {

    fun create(outputFile: File?): OSAudioRecorder?

    fun getMaxAmplitude(): Double

    fun start()
    fun stop()

    fun clear()
}