 package com.sspl.core
import com.sspl.session.UserSession
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.cache.storage.CacheStorage
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.http.ContentType
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 11/01/2025.
 * se.muhammadimran@gmail.com
 */


suspend fun <T : HttpClientEngineConfig> getBaseClient(
    userSession: UserSession,
    engineFactory: HttpClientEngineFactory<T>,
    config: HttpClientConfig<T>.() -> Unit
): HttpClient {
    return HttpClient(engineFactory) {
        install(Resources)
//        install(HttpCache) {
//            publicStorage(CacheStorage.Disabled) // Disables response caching
//        }
        install(ContentNegotiation) {
            json(Json { 
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.BODY
            sanitizeHeader { header -> header == HttpHeaders.Authorization }
        }
        defaultRequest {
            url("${com.sspl.utils.BASE_URL}/")
            contentType(ContentType.Application.Json)
            val token = userSession.token
            token?.let {
                bearerAuth(it)
            }
            println(">>>Token: $token")
            header(HttpHeaders.UserAgent, "SSPL Application")
        }

        config() // Apply additional configuration
    }
}
//}
//}
//        install(ContentNegotiation) {
//            json(Json {
//                ignoreUnknownKeys = true
//                prettyPrint = true
////                isLenient = true
//            })
//        }
//        install(Logging) {
//            logger = Logger.DEFAULT
//            level = LogLevel.ALL
//            filter { request ->
//                request.url.host.contains(HOST_URL)
//            }
//            sanitizeHeader { header -> header == HttpHeaders.Authorization }
//        }
//        defaultRequest {
//            url {
//                host = HOST_URL
//                port = 3000
//                protocol = URLProtocol.HTTP
//            } // Set to HTTP if needed
//            val token = ""
//            header(HttpHeaders.Authorization, "Bearer $token")
//            header(HttpHeaders.UserAgent, "SSPL Application")
//        }
//        config()
//    }
//}