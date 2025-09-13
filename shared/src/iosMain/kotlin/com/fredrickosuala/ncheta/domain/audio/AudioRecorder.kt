package com.fredrickosuala.ncheta.domain.audio

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
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
import kotlinx.cinterop.usePinned
import platform.Foundation.NSNumber
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

            val fileName = "audiorecord.m4a"
            val fileUrl = NSURL.fileURLWithPathComponents(listOf(NSTemporaryDirectory(), fileName))!!
            this.audioFileUrl = fileUrl

            val settings: Map<Any?, Any?> = mapOf(
                AVFormatIDKey to kAudioFormatMPEG4AAC,
                AVSampleRateKey to NSNumber(double = 44100.0),
                AVNumberOfChannelsKey to NSNumber(int = 1),
                AVEncoderAudioQualityKey to NSNumber(int = AVAudioQualityHigh.toInt())
            )

            try {
                audioSession.setCategory(
                    category = AVAudioSessionCategoryPlayAndRecord,
                    mode = AVAudioSessionModeDefault,
                    options = 0u,
                    error = null
                )
                audioSession.setActive(true, null)

                recorder = AVAudioRecorder(fileUrl, settings, null)?.apply {
                    record()
                }

                if (recorder == null) {
                    _state.update { AudioRecorderState.Error("Failed to initialize recorder.") }
                } else {
                    _state.update { AudioRecorderState.Recording }
                }
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

