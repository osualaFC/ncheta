package com.fredrickosuala.ncheta.domain.audio

import kotlinx.coroutines.flow.StateFlow

expect class AudioRecorder {
    val state: StateFlow<AudioRecorderState>
    fun startRecording()
    fun stopRecording()
    fun onCleared()
}


sealed class AudioRecorderState {
    data object Idle : AudioRecorderState()
    data object Recording : AudioRecorderState()
    data class Error(val message: String) : AudioRecorderState()
    data class Success(val audioData: ByteArray, val mimeType: String) : AudioRecorderState() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as Success

            if (!audioData.contentEquals(other.audioData)) return false
            if (mimeType != other.mimeType) return false

            return true
        }

        override fun hashCode(): Int {
            var result = audioData.contentHashCode()
            result = 31 * result + mimeType.hashCode()
            return result
        }
    }
}