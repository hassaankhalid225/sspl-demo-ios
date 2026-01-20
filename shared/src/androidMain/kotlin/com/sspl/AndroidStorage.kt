package com.sspl

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.sspl.storage.Storage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 11/01/2025.
 * se.muhammadimran@gmail.com
 */
class AndroidStorage(context: Context) : Storage {
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
    private var masterKey: MasterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private var sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "sspl_app_secret_shared_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    override suspend fun putString(key: String, value: String?) =
        withContext(dispatcher) {
            sharedPreferences.edit().putString(key, value).commit()
        }

    override suspend fun getString(key: String, default: String): String = withContext(dispatcher) {
        sharedPreferences.getString(key, default) ?: default
    }

    override suspend fun getString(key: String): String? = withContext(dispatcher) {
        sharedPreferences.getString(key, null)
    }

    override suspend fun putLong(key: String, value: Long): Boolean =
        withContext(dispatcher) {
            sharedPreferences.edit().putLong(key, value).commit()
        }

    override suspend fun getLong(key: String, default: Long): Long = withContext(dispatcher) {
        sharedPreferences.getLong(key, default)
    }
}