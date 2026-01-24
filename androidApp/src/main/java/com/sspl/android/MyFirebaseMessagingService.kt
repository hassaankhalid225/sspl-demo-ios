package com.sspl.android

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sspl.core.models.SessionNotification
import com.sspl.core.repositories.NotificationRepository
import com.sspl.session.UserSession
import com.sspl.core.push.PushNotificationService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val notificationRepository: NotificationRepository by inject()
    private val userSession: UserSession by inject()
    private val pushNotificationService: PushNotificationService by inject()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if message contains data payload
        // Check for session-related data and show notification even if notification payload is missing
        remoteMessage.data.isNotEmpty().let {
            val type = remoteMessage.data["type"]
            val joinCode = remoteMessage.data["join_code"]
            val scenarioTitle = remoteMessage.data["scenario_title"] ?: "New Session"
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")

            when (type) {
                "session_invite" -> {
                    handleSessionInvite(remoteMessage.data)
                    // If no notification payload, show one manually for invites
                    if (remoteMessage.notification == null) {
                        showNotification("🎯 New Quiz Session!", scenarioTitle, remoteMessage.data)
                    }
                }
                "session_started" -> {
                    handleSessionStarted(remoteMessage.data)
                    // If no notification payload, show one manually for starts
                    if (remoteMessage.notification == null) {
                        showNotification("🚀 Session Started!", scenarioTitle, remoteMessage.data)
                    }
                }
                else -> {
                    // For any other data-only notification that has a title/body
                    val title = remoteMessage.data["title"]
                    val body = remoteMessage.data["body"] ?: remoteMessage.data["message"]
                    
                    if (title != null || body != null) {
                        val finalTitle = title ?: "Notification"
                        val finalBody = body ?: ""
                        
                        val notificationType = when(type) {
                            "announcement" -> com.sspl.core.models.NotificationType.ANNOUNCEMENT
                            else -> com.sspl.core.models.NotificationType.GENERAL
                        }
                        
                        notificationRepository.addNotification(finalTitle, finalBody, notificationType)
                        
                        if (remoteMessage.notification == null) {
                            showNotification(finalTitle, finalBody, remoteMessage.data)
                        }
                    }
                }
            }
        }

        // Show notification for other messages with a notification payload
        // only if they were not already handled as session/known types above
        remoteMessage.notification?.let {
            val type = remoteMessage.data["type"]
            if (type != "session_invite" && type != "session_started" && type != "announcement") {
                val title = it.title ?: "Notification"
                val body = it.body ?: ""
                showNotification(title, body, remoteMessage.data)
                
                // Save to repository
                notificationRepository.addNotification(title, body)
            }
        }
    }

    private fun handleSessionInvite(data: Map<String, String>) {
        val joinCode = data["join_code"]
        val scenarioTitle = data["scenario_title"] ?: "New Quiz Session"
        val joinUrl = data["join_url"]
        val sessionId = data["session_id"]

        Log.d(TAG, "Session invite received: $joinCode - $scenarioTitle")

        // Store invite notification in repository for in-app display
        if (joinCode != null) {
            val sessionNotification = SessionNotification(
                sessionId = sessionId?.toIntOrNull() ?: 0,
                joinCode = joinCode,
                scenarioTitle = scenarioTitle,
                joinUrl = joinUrl
            )
            notificationRepository.addSessionNotification(sessionNotification)
            Log.d(TAG, "Session invite stored in repository")
        }
    }

    private fun handleSessionStarted(data: Map<String, String>) {
        val joinCode = data["join_code"]
        val sessionId = data["session_id"]
        val scenarioTitle = data["scenario_title"] ?: "New Session"
        val startedAt = data["started_at"]
        val currentQuestionIndex = data["current_question_index"]
        val timePerQuestion = data["time_per_question"]

        Log.d(TAG, "Session started: $joinCode")

        // Add to repository for in-app UI updates
        val sessionNotification = SessionNotification(
            sessionId = sessionId?.toIntOrNull() ?: 0,
            joinCode = joinCode ?: "",
            scenarioTitle = scenarioTitle,
            startedAt = startedAt,
            currentQuestionIndex = currentQuestionIndex,
            timePerQuestion = timePerQuestion
        )
        notificationRepository.addSessionNotification(sessionNotification)

        // Send broadcast if needed for specific screen navigation
        val intent = Intent("SESSION_STARTED").apply {
            putExtra("join_code", joinCode)
            putExtra("session_id", sessionId)
        }
        sendBroadcast(intent)
    }

    private fun showNotification(title: String, body: String, data: Map<String, String>) {
        val channelId = "session_notifications"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        Log.d(TAG, "showNotification called")
        Log.d(TAG, "Title: $title")
        Log.d(TAG, "Body: $body")
        Log.d(TAG, "Data: $data")

        // Create notification channel (Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Session Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for quiz session updates"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Always launch MainActivity first to ensure app is properly initialized
        // MainActivity will then launch SessionActivity based on the intent data
        val notificationType = data["type"]
        Log.d(TAG, "Notification type: $notificationType")
        
        val intent = Intent(this, MainActivity::class.java).apply {
            // Pass all notification data
            putExtra("notification_type", data["type"])
            putExtra("join_code", data["join_code"])
            putExtra("join_url", data["join_url"])
            putExtra("scenario_title", data["scenario_title"])
            putExtra("session_id", data["session_id"])
            
            // Use FLAG_ACTIVITY_CLEAR_TOP to bring existing MainActivity to front
            // or create new one if app is not running
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            
            Log.d(TAG, "Creating intent for MainActivity with session data:")
            Log.d(TAG, "  notification_type: ${data["type"]}")
            Log.d(TAG, "  join_code: ${data["join_code"]}")
            Log.d(TAG, "  join_url: ${data["join_url"]}")
            Log.d(TAG, "  scenario_title: ${data["scenario_title"]}")
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Build notification
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM token: $token")

        // Save new token to shared user session
        serviceScope.launch {
            userSession.setDeviceToken(token)
            pushNotificationService.registerDeviceToken()
        }

        // TODO: Send token to server if user is logged in
    }

    companion object {
        private const val TAG = "FCMService"
    }
}
