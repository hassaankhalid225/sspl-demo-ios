package com.sspl.core.repositories

import com.sspl.core.models.NotificationItem
import com.sspl.core.models.NotificationType
import com.sspl.core.models.SessionNotification
import com.sspl.storage.Storage
import com.sspl.utils.DateTimeUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class NotificationRepository(private val storage: Storage) {
    
    companion object {
        private const val NOTIFICATIONS_KEY = "stored_notifications_key"
    }
    
    private val scope = CoroutineScope(Dispatchers.Default)
    private val json = Json { ignoreUnknownKeys = true }
    
    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()
    
    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()
    
    init {
        // Load persisted notifications on initialization
        scope.launch {
            loadNotifications()
        }
    }
    
    private suspend fun loadNotifications() {
        try {
            val storedJson = storage.getString(NOTIFICATIONS_KEY)
            if (!storedJson.isNullOrEmpty()) {
                val loaded = json.decodeFromString<List<NotificationItem>>(storedJson)
                _notifications.value = loaded
                updateUnreadCount()
                println("NotificationRepository: Loaded ${loaded.size} notifications from storage")
            }
        } catch (e: Exception) {
            println("NotificationRepository: Error loading notifications - ${e.message}")
        }
    }
    
    private suspend fun saveNotifications() {
        try {
            val jsonString = json.encodeToString(_notifications.value)
            storage.putString(NOTIFICATIONS_KEY, jsonString)
            println("NotificationRepository: Saved ${_notifications.value.size} notifications to storage")
        } catch (e: Exception) {
            println("NotificationRepository: Error saving notifications - ${e.message}")
        }
    }
    
    fun addSessionNotification(sessionNotification: SessionNotification) {
        // Updated to use KMP-compatible DateTimeUtils instead of System.currentTimeMillis()
        val notification = NotificationItem(
            id = "session_${sessionNotification.sessionId}_${DateTimeUtils.timeInMilliNow()}",
            title = "New Live Session",
            message = "${sessionNotification.scenarioTitle} is now live! Join with code: ${sessionNotification.joinCode}",
            timestamp = DateTimeUtils.timeInMilliNow(),
            isRead = false,
            type = NotificationType.SESSION_LIVE,
            sessionNotification = sessionNotification
        )
        
        _notifications.update { currentList ->
            listOf(notification) + currentList
        }
        
        updateUnreadCount()
        
        // Persist after adding
        scope.launch {
            saveNotifications()
        }
    }
    
    fun addNotification(title: String, message: String, type: NotificationType = NotificationType.GENERAL) {
        val notification = NotificationItem(
            id = "gen_${DateTimeUtils.timeInMilliNow()}",
            title = title,
            message = message,
            timestamp = DateTimeUtils.timeInMilliNow(),
            isRead = false,
            type = type,
            sessionNotification = null
        )
        
        _notifications.update { currentList ->
            listOf(notification) + currentList
        }
        
        updateUnreadCount()
        
        // Persist after adding
        scope.launch {
            saveNotifications()
        }
    }
    
    fun markAsRead(notificationId: String) {
        _notifications.update { currentList ->
            currentList.map { notification ->
                if (notification.id == notificationId) {
                    notification.copy(isRead = true)
                } else {
                    notification
                }
            }
        }
        updateUnreadCount()
        
        // Persist after update
        scope.launch {
            saveNotifications()
        }
    }
    
    fun markAllAsRead() {
        _notifications.update { currentList ->
            currentList.map { it.copy(isRead = true) }
        }
        updateUnreadCount()
        
        // Persist after update
        scope.launch {
            saveNotifications()
        }
    }
    
    fun deleteNotification(notificationId: String) {
        _notifications.update { currentList ->
            currentList.filter { it.id != notificationId }
        }
        updateUnreadCount()
        
        // Persist after delete
        scope.launch {
            saveNotifications()
        }
    }
    
    fun clearAll() {
        _notifications.value = emptyList()
        _unreadCount.value = 0
        
        // Persist after clear
        scope.launch {
            saveNotifications()
        }
    }
    
    private fun updateUnreadCount() {
        _unreadCount.value = _notifications.value.count { !it.isRead }
    }
    
    fun getNotificationById(id: String): NotificationItem? {
        return _notifications.value.firstOrNull { it.id == id }
    }
}
