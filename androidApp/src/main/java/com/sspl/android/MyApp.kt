package com.sspl.android

import android.app.Application
import com.sspl.androidModules
import com.sspl.di.appModules

import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 17/11/2024.
 * se.muhammadimran@gmail.com
 */
class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApp)
            androidLogger()
            modules(appModules + listOf(androidModules))
        }
    }
}