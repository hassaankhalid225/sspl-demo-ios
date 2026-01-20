package com.sspl

import com.sspl.platform.IosGpsChecker
import com.sspl.platform.shared.UrlOpener
import com.sspl.storage.Storage
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 11/01/2025.
 * se.muhammadimran@gmail.com
 */

val iosModules = module {
    singleOf(::IosStorage).bind<Storage>()
    singleOf(::UrlOpener)
    single { IosGpsChecker() }
}