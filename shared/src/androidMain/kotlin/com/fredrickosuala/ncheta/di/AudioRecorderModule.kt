package com.fredrickosuala.ncheta.di

import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.android.ext.koin.androidApplication
import com.fredrickosuala.ncheta.domain.audio.AudioRecorder

actual fun audioRecorderModule(): Module = module {
    factory {
        AudioRecorder(app = androidApplication())
    }
}