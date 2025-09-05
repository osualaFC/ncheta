package com.fredrickosuala.ncheta.domain.audio

import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.refTo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import platform.AVFAudio.*
import platform.CoreAudioTypes.kAudioFormatMPEG4AAC
import platform.Foundation.NSData
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfURL
import platform.Foundation.getBytes
import kotlinx.cinterop.refTo
import kotlinx.cinterop.usePinned
import platform.darwin.dispatch_after
import platform.darwin.dispatch_get_main_queue
import platform.darwin.dispatch_time
import platform.darwin.NSEC_PER_SEC

@OptIn(ExperimentalForeignApi::class)
actual class AudioRecorder {
    private var recorder: AVAudioRecorder? = null
    private var audioFileUrl: NSURL? = null

    private val _state = MutableStateFlow<AudioRecorderState>(AudioRecorderState.Idle)
    actual val state = _state.asStateFlow()

    actual fun startRecording() {
        val audioSession = AVAudioSession.sharedInstance()
        audioSession.requestRecordPermission { isGranted ->
            if (!isGranted) {
                _state.update { AudioRecorderState.Error("Microphone permission denied.") }
                return@requestRecordPermission
            }

            // Setup file path and recording settings
            val fileName = "audiorecord.m4a"
            val fileUrl = NSURL.fileURLWithPathComponents(listOf(NSTemporaryDirectory(), fileName))!!
            this.audioFileUrl = fileUrl

            val settings: Map<Any?, *> = mapOf(
                AVFormatIDKey to kAudioFormatMPEG4AAC,
                AVSampleRateKey to 44100.0,
                AVNumberOfChannelsKey to 1,
                AVEncoderAudioQualityKey to AVAudioQuality.MAX_VALUE
            )

            try {
                audioSession.setCategory(category = AVAudioSessionCategoryPlayAndRecord, mode = AVAudioSessionModeDefault, options = AVAudioSessionCategoryOptions.MAX_VALUE, error = null)
                audioSession.setActive(true, null)

                recorder = AVAudioRecorder(fileUrl, settings = settings, error = null).apply {
                    record()
                }
                _state.update { AudioRecorderState.Recording }
            } catch (e: Exception) {
                _state.update { AudioRecorderState.Error("Failed to start recording: ${e.message}") }
            }
        }
    }

    actual fun stopRecording() {
        recorder?.stop()
        recorder = null

        val url = audioFileUrl ?: run {
            _state.update { AudioRecorderState.Error("Audio file not found.") }
            return
        }
        audioFileUrl = null

        // Delay slightly to ensure file is flushed
        dispatch_after(dispatch_time(0.toULong(), (0.1.toULong() * NSEC_PER_SEC).toLong()), dispatch_get_main_queue()) {
            try {
                val audioData = NSData.dataWithContentsOfURL(url)
                if (audioData != null) {
                    val bytes = audioData.toByteArray()  // pinned safe conversion
                    _state.update { AudioRecorderState.Success(bytes, "audio/mp4") }
                } else {
                    _state.update { AudioRecorderState.Error("Failed to read audio data.") }
                }
            } catch (e: Exception) {
                _state.update { AudioRecorderState.Error("Failed to process audio file: ${e.message}") }
            }

            // Deactivate audio session after reading
            AVAudioSession.sharedInstance().setActive(false, null)
        }
    }



    actual fun onCleared() {
        recorder?.stop()
        recorder = null
    }
}

// Helper to convert NSData to ByteArray
@OptIn(ExperimentalForeignApi::class)
fun NSData.toByteArray(): ByteArray =
    ByteArray(length.toInt()).also { byteArray ->
        byteArray.usePinned { pinned ->
            getBytes(pinned.addressOf(0), length)
        }
    }

