package com.sspl.session

import com.sspl.core.models.User
import com.sspl.storage.Storage
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 12/12/2024.
 * se.muhammadimran@gmail.com
 */
class UserSession(private val storage: Storage) {
    companion object {
        private const val TOKEN_KEY = "user_token_key"
        private const val USER_KEY = "user_object_key"
        private const val DEVICE_TOKEN_KEY = "device_token_key"
    }

    var token: String? = null
        private set
        get() {
            if (field == null) {
                runBlocking { field = getToken() }
            }
            return field
        }

    var deviceToken: String? = null
        private set
        get() {
            if (field == null) {
                runBlocking { field = getDeviceToken() }
            }
            return field
        }

    private suspend fun getToken(): String? = storage.getString(TOKEN_KEY)
    private suspend fun getDeviceToken(): String? = storage.getString(DEVICE_TOKEN_KEY)

    suspend fun setToken(token: String) {
        println(">>>Token: Saving $token")
        this.token = token
        storage.putString(TOKEN_KEY, token)
    }

    suspend fun setDeviceToken(token: String) {
        println(">>>Device Token: Saving $token")
        this.deviceToken = token
        storage.putString(DEVICE_TOKEN_KEY, token)
    }

    suspend fun resetToken() {
        storage.putString(TOKEN_KEY, null)
        this.token = null
    }

    suspend fun setUser(user: User) {
        val userString = Json.encodeToString(user)
        storage.putString(USER_KEY, userString)
    }

    suspend fun getUser(): User? {
        val userString = storage.getString(USER_KEY)
        return if (userString != null) {
            Json.decodeFromString<User>(userString)
        } else {
            null
        }
    }

    suspend fun resetUser() {
        storage.putString(USER_KEY, null)
        token = null
    }


}