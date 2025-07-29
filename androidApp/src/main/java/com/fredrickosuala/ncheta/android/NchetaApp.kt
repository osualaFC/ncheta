package com.fredrickosuala.ncheta.android

import android.app.Application
import com.fredrickosuala.ncheta.di.initKoin
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import org.koin.android.ext.koin.androidContext

class NchetaApp : Application()  {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@NchetaApp)
        }
        PDFBoxResourceLoader.init(this)
    }
}