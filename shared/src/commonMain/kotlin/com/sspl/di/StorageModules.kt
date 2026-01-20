package com.sspl.di

import com.sspl.session.UserSession
import org.koin.dsl.module

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 02/01/2025.
 * se.muhammadimran@gmail.com
 */

internal val storageModules = module {
    single { UserSession(get()) }
}
