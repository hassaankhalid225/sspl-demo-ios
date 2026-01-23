package com.sspl.storage

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 11/01/2025.
 * se.muhammadimran@gmail.com
 */
interface Storage {
    suspend fun putString(key: String, value: String?): Boolean
    suspend fun getString(key: String, default: String): String
    suspend fun getString(key: String): String?
    suspend fun putLong(key: String, value: Long): Boolean
    suspend fun getLong(key: String, default: Long = 0): Long
    suspend fun remove(key: String): Boolean
}