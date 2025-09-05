package com.fredrickosuala.ncheta.domain.audio

import android.app.Application
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import android.media.MediaRecorder
import android.os.Build
import kotlinx.coroutines.flow.StateFlow
import java.io.File

actual class AudioRecorder(
    private val app: Application
) {
    private var recorder: MediaRecorder? = null
    private var audioFile: File? = null

    private val _state = MutableStateFlow<AudioRecorderState>(AudioRecorderState.Idle)
    actual val state: StateFlow<AudioRecorderState>
        get() = _state.asStateFlow()


    private fun createRecorder(): MediaRecorder {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(app)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }
    }

    actual fun startRecording() {
        val outputFile = File(app.cacheDir, "audiorecord.mp4")
        audioFile = outputFile

        recorder = createRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(outputFile.absolutePath)

            try {
                prepare()
                start()
                _state.update { AudioRecorderState.Recording }
            } catch (e: Exception) {
                _state.update { AudioRecorderState.Error("Failed to start recording: ${e.message}") }
            }
        }
    }

    actual fun stopRecording() {
        recorder?.stop()
        recorder?.release()
        recorder = null

        val recordedAudioFile = audioFile ?: return

        try {
            val audioData = recordedAudioFile.readBytes()
            _state.update { AudioRecorderState.Success(audioData, "audio/mp4") }
        } catch (e: Exception) {
            _state.update { AudioRecorderState.Error("Failed to read audio file: ${e.message}") }
        } finally {
            // Clean up the temporary file
            recordedAudioFile.delete()
            audioFile = null
        }
    }

    actual fun onCleared() {
        // Ensure recorder is released if it's still active
        recorder?.stop()
        recorder?.release()
        recorder = null
        audioFile?.delete()
    }

}