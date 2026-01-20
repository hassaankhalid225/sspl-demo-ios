package com.sspl.android.pusher

import android.util.Log
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.Channel
import com.pusher.client.channel.SubscriptionEventListener
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionState
import com.pusher.client.connection.ConnectionStateChange
import com.sspl.core.models.SessionNotification
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.json.JSONObject

/**
 * Created by M Imran
 * Senior Software Engineer at
 * BhimSoft on 08/01/2025.
 * se.muhammadimran@gmail.com
 */

class PusherManager(
    private val appKey: String,
    private val cluster: String
) {
    private var pusher: Pusher? = null
    private var sessionChannel: Channel? = null
    
    private val _sessionNotifications = MutableSharedFlow<SessionNotification>(replay = 0)
    val sessionNotifications: SharedFlow<SessionNotification> = _sessionNotifications.asSharedFlow()
    
    private val _connectionState = MutableSharedFlow<ConnectionState>(replay = 1)
    val connectionState: SharedFlow<ConnectionState> = _connectionState.asSharedFlow()
    
    companion object {
        private const val TAG = "PusherManager"
        private const val CHANNEL_SESSION_NOTIFICATIONS = "session-notifications"
        private const val EVENT_SESSION_STARTED = "session-started"
    }
    
    fun connect() {
        if (pusher != null) {
            Log.d(TAG, "Pusher already connected")
            return
        }
        
        try {
            val options = PusherOptions().apply {
                setCluster(cluster)
            }
            
            pusher = Pusher(appKey, options).apply {
                // Connection state listener
                connection.bind(ConnectionState.ALL, object : ConnectionEventListener {
                    override fun onConnectionStateChange(change: ConnectionStateChange) {
                        Log.d(TAG, "Connection state changed: ${change.previousState} -> ${change.currentState}")
                        _connectionState.tryEmit(change.currentState)
                    }

                    override fun onError(message: String, code: String?, e: Exception?) {
                        Log.e(TAG, "Connection error: $message (code: $code)", e)
                    }
                })
                
                connect()
            }
            
            subscribeToSessionNotifications()
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Pusher", e)
        }
    }
    
    private fun subscribeToSessionNotifications() {
        try {
            sessionChannel = pusher?.subscribe(CHANNEL_SESSION_NOTIFICATIONS)
            
            sessionChannel?.bind(EVENT_SESSION_STARTED, SubscriptionEventListener { event ->
                Log.d(TAG, "Received session-started event: ${event.data}")
                
                try {
                    // Parse JSON using org.json first, then convert to our model
                    val jsonObject = JSONObject(event.data)
                    
                    val sessionNotification = SessionNotification(
                        sessionId = jsonObject.getInt("session_id"),
                        scenarioId = jsonObject.getInt("scenario_id"),
                        scenarioTitle = jsonObject.getString("scenario_title"),
                        joinCode = jsonObject.getString("join_code"),
                        joinUrl = jsonObject.getString("join_url"),
                        qrCode = jsonObject.getString("qr_code"),
                        expiresAt = jsonObject.getString("expires_at"),
                        createdAt = jsonObject.getString("created_at")
                    )
                    
                    Log.d(TAG, "Parsed session: ${sessionNotification.scenarioTitle} (Code: ${sessionNotification.joinCode})")
                    
                    // Emit to flow
                    _sessionNotifications.tryEmit(sessionNotification)
                    
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to parse session notification", e)
                }
            })
            
            Log.d(TAG, "Subscribed to $CHANNEL_SESSION_NOTIFICATIONS channel")
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to subscribe to session notifications", e)
        }
    }
    
    fun disconnect() {
        try {
            sessionChannel?.unbind(EVENT_SESSION_STARTED, null)
            pusher?.unsubscribe(CHANNEL_SESSION_NOTIFICATIONS)
            pusher?.disconnect()
            pusher = null
            sessionChannel = null
            Log.d(TAG, "Pusher disconnected")
        } catch (e: Exception) {
            Log.e(TAG, "Error disconnecting Pusher", e)
        }
    }
    
    fun isConnected(): Boolean {
        return pusher?.connection?.state == ConnectionState.CONNECTED
    }
}
