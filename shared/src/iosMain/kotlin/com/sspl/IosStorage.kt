package com.sspl

import com.sspl.storage.Storage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.Foundation.NSUserDefaults
import platform.Foundation.setValue

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 11/01/2025.
 * se.muhammadimran@gmail.com
 */
class IosStorage : Storage {
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
    private val defaults = NSUserDefaults.standardUserDefaults
    override suspend fun putString(key: String, value: String?): Boolean =
        withContext(dispatcher) {
            defaults.setValue(value, key)
//        defaults.setObject(value, key)
            true
        }

    override suspend fun getString(key: String, default: String): String =
        withContext(dispatcher) {
            defaults.stringForKey(key) ?: default
        }

    override suspend fun getString(key: String): String? =
        withContext(dispatcher) {
            defaults.stringForKey(key)
        }


    override suspend fun putLong(key: String, value: Long): Boolean = withContext(dispatcher) {
        defaults.setDouble(value.toDouble(), key)
        true
    }

    override suspend fun getLong(key: String, default: Long): Long = withContext(dispatcher) {
        defaults.objectForKey(key)?.let {
            defaults.doubleForKey(key).toLong()
        } ?: default
    }

    override suspend fun remove(key: String): Boolean = withContext(dispatcher) {
        defaults.removeObjectForKey(key)
        true
    }
}