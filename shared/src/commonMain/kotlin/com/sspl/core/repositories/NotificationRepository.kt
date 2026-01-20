package com.sspl.core.repositories

import com.sspl.core.models.NotificationItem
import com.sspl.core.models.NotificationType
import com.sspl.core.models.SessionNotification
import com.sspl.utils.DateTimeUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class NotificationRepository {
    
    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()
    
    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()
    
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
    }
    
    fun markAllAsRead() {
        _notifications.update { currentList ->
            currentList.map { it.copy(isRead = true) }
        }
        updateUnreadCount()
    }
    
    fun deleteNotification(notificationId: String) {
        _notifications.update { currentList ->
            currentList.filter { it.id != notificationId }
        }
        updateUnreadCount()
    }
    
    fun clearAll() {
        _notifications.value = emptyList()
        _unreadCount.value = 0
    }
    
    private fun updateUnreadCount() {
        _unreadCount.value = _notifications.value.count { !it.isRead }
    }
    
    fun getNotificationById(id: String): NotificationItem? {
        return _notifications.value.firstOrNull { it.id == id }
    }
}
