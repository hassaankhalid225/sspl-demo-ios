package com.sspl.session

import com.sspl.core.models.User
import com.sspl.storage.Storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
        private const val PROFILE_IMAGE_KEY = "profile_image_key"
    }

    private val _userFlow = MutableStateFlow<User?>(null)
    val userFlow = _userFlow.asStateFlow()

    private val _profileImageFlow = MutableStateFlow<String?>(null)
    val profileImageFlow = _profileImageFlow.asStateFlow()

    init {
        // Initialize flow with stored value
        runBlocking {
            val userString = storage.getString(USER_KEY)
            if (userString != null) {
                _userFlow.value = Json.decodeFromString<User>(userString)
            }
            _profileImageFlow.value = storage.getString(PROFILE_IMAGE_KEY)
        }
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
        storage.remove(TOKEN_KEY)
        this.token = null
    }

    suspend fun setUser(user: User) {
        val userString = Json.encodeToString(user)
        storage.putString(USER_KEY, userString)
        _userFlow.value = user
    }

    suspend fun getUser(): User? {
        val userString = storage.getString(USER_KEY)
        val user = if (userString != null) {
            Json.decodeFromString<User>(userString)
        } else {
            null
        }
        _userFlow.value = user
        return user
    }

    suspend fun resetUser() {
        storage.remove(USER_KEY)
        storage.remove(PROFILE_IMAGE_KEY)
        _userFlow.value = null
        _profileImageFlow.value = null
        token = null
    }

    suspend fun setProfileImage(base64: String?) {
        if (base64 == null) {
            storage.remove(PROFILE_IMAGE_KEY)
        } else {
            storage.putString(PROFILE_IMAGE_KEY, base64)
        }
        _profileImageFlow.value = base64
    }

    suspend fun getProfileImage(): String? {
        val img = storage.getString(PROFILE_IMAGE_KEY)
        _profileImageFlow.value = img
        return img
    }
}