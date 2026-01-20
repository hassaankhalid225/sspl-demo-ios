package com.sspl.di


import com.sspl.platform.getClient
import com.sspl.session.UserSession

import io.ktor.client.HttpClient
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 02/01/2025.
 * se.muhammadimran@gmail.com
 */

internal val networkModules = module {
    factoryOf(::provideHttpClient)
}

var cacheSize: Long = 5 * 1024 * 1024 // 5MB

//private fun provideLoggingInterceptor() = HttpLoggingInterceptor().apply {
//    this.level = HttpLoggingInterceptor.Level.BODY
//}
//
//private fun provideCacheInterceptor() = Interceptor { chain ->
//    val response: Response = chain.proceed(chain.request())
//    val cacheControl = CacheControl.Builder()
//        .maxAge(1, TimeUnit.DAYS)
//        .build()
//    response.newBuilder()
//        .header("Cache-Control", cacheControl.toString())
//        .build()
//}
//
private fun provideHttpClient(userSession: UserSession): HttpClient =
    HttpClientFactory(userSession = userSession).getClient()

class HttpClientFactory(private val userSession: UserSession) {
    private val httpClient by lazy {
        runBlocking {
            getClient(userSession = userSession)
        }
    }

    fun getClient(): HttpClient = httpClient
}

//
//
//private fun provideRetrofit(
//    okHttpClient: OkHttpClient
//): Retrofit {
//    val networkJson = Json {
//        ignoreUnknownKeys = true
//        explicitNulls = false
//    }
//    val converter = networkJson.asConverterFactory("application/json; charset=utf-8".toMediaType())
//    return Retrofit.Builder()
//        .baseUrl(BASE_URL)
//        .client(okHttpClient)
//        .addConverterFactory(converter)
//        .build()
//}
//
//private fun provideService(retrofit: Retrofit): ApiService =
//    retrofit.create(ApiService::class.java)