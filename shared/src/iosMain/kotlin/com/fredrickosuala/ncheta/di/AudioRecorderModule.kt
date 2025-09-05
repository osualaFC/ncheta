package com.fredrickosuala.ncheta.di

import com.fredrickosuala.ncheta.domain.audio.AudioRecorder
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

actual fun audioRecorderModule(): Module = module {
    factoryOf(::AudioRecorder)
}