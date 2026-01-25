package com.sspl.android

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.messaging.FirebaseMessaging
import com.sspl.App
import com.sspl.android.pusher.PusherManager
import com.sspl.core.repositories.NotificationRepository
import com.sspl.core.usecases.UpdateDeviceTokenUseCase
import com.sspl.session.UserSession
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    
    private val notificationRepository: NotificationRepository by inject()
    private val userSession: UserSession by inject()
    private val updateDeviceTokenUseCase: UpdateDeviceTokenUseCase by inject()
    private lateinit var pusherManager: PusherManager

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("MainActivity", "Notification permission granted")
        } else {
            Log.d("MainActivity", "Notification permission denied")
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                false
            }
        }
        
        // Request notification permission for Android 13+
        askNotificationPermission()
        
        // Fetch and save FCM token
        getFCMToken()
        
        // Initialize Pusher
        setupPusher()
        
        setContent {
            App()
        }
        
        // Handle notification intent AFTER setContent to ensure app is initialized
        // Post to main thread to ensure everything is ready
        window.decorView.post {
            handleNotificationIntent(intent)
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // Permission is already granted
            } else {
                // Request permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun getFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("MainActivity", "Fetching FCM token failed", task.exception)
                return@addOnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            Log.d("MainActivity", "FCM Token: $token")

            // Store token for later use when joining session
            saveTokenToUserSession(token)
        }
    }

    private fun saveTokenToUserSession(token: String) {
        lifecycleScope.launch {
            // Save token locally
            userSession.setDeviceToken(token)
            
            // Only send token to backend if user is logged in
            if (userSession.token.isNullOrEmpty()) {
                Log.d("MainActivity", "User not logged in, skipping device token registration to backend")
                return@launch
            }
            
            Log.d("MainActivity", "User is logged in, sending device token to backend...")
            
            // Send token to backend
            updateDeviceTokenUseCase(
                deviceToken = token,
                devicePlatform = "android"
            ).collect { state ->
                when (state) {
                    is com.sspl.core.ApiStates.Success -> {
                        Log.d("MainActivity", "Device token sent to backend: ${state.data?.message}")
                    }
                    is com.sspl.core.ApiStates.Failure -> {
                        Log.e("MainActivity", "Failed to send device token: ${state.error.message}")
                    }
                    is com.sspl.core.ApiStates.Loading -> {
                        Log.d("MainActivity", "Sending device token...")
                    }
                    is com.sspl.core.ApiStates.Idle -> {
                        // Do nothing
                    }
                }
            }
        }
    }

    private fun handleNotificationIntent(intent: Intent?) {
        if (intent == null) {
            Log.d("MainActivity", "Intent is null, nothing to handle")
            return
        }
        
        intent.extras?.let { extras ->
            // Log all extras for debugging
            Log.d("MainActivity", "--- Notification Extras ---")
            for (key in extras.keySet()) {
                Log.d("MainActivity", "  $key: ${extras.get(key)}")
            }
            Log.d("MainActivity", "---------------------------")

            // Support both keys (type from FCM OS delivery, notification_type from our manual showNotification)
            val notificationType = extras.getString("notification_type") ?: extras.getString("type")
            val joinCode = extras.getString("join_code")
            val joinUrl = extras.getString("join_url")
            val scenarioTitle = extras.getString("scenario_title")
            val sessionId = extras.getString("session_id")
            
            Log.d("MainActivity", "handleNotificationIntent extracted values:")
            Log.d("MainActivity", "  notificationType: $notificationType")
            Log.d("MainActivity", "  join_code: $joinCode")
            
            // Check if this is a session notification that should open SessionActivity
            if ((notificationType == "session_invite" || notificationType == "session_started") && joinCode != null) {
                Log.d("MainActivity", "Launching SessionActivity for session: $joinCode")
                
                try {
                    // Launch SessionActivity with a delay to ensure MainActivity is fully initialized
                    lifecycleScope.launch {
                        kotlinx.coroutines.delay(500) // Wait 500ms for app to be ready
                        
                        val sessionIntent = Intent(this@MainActivity, SessionActivity::class.java).apply {
                            putExtra(SessionActivity.EXTRA_JOIN_CODE, joinCode)
                            putExtra(SessionActivity.EXTRA_JOIN_URL, joinUrl)
                            putExtra(SessionActivity.EXTRA_SCENARIO_TITLE, scenarioTitle)
                        }
                        startActivity(sessionIntent)
                    }
                } catch (e: Exception) {
                    Log.e("MainActivity", "Error launching SessionActivity", e)
                }
            } else {
                Log.d("MainActivity", "Not a session notification or missing join code")
            }
        } ?: run {
            Log.d("MainActivity", "No extras in intent")
        }
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent) // Update activity intent
        Log.d("MainActivity", "onNewIntent called with intent: $intent")
        // Handle notification when app is already running
        handleNotificationIntent(intent)
    }
    
    private fun setupPusher() {
        // Pusher credentials for real-time notifications
        pusherManager = PusherManager(
            appKey = "36e8c7303e28cf81d297",
            cluster = "ap2"
        )
        
        // Connect to Pusher
        pusherManager.connect()
        
        // Listen for session notifications
        lifecycleScope.launch {
            pusherManager.sessionNotifications.collect { sessionNotification ->
                // Add notification to repository
                notificationRepository.addSessionNotification(sessionNotification)
                
                // TODO: Show local Android notification (optional)
                // showLocalNotification(sessionNotification)
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        pusherManager.disconnect()
    }
}

//@Composable
//fun GreetingView(text: String) {
//    Text(text = text)
//}
//
//@Preview
//@Composable
//fun DefaultPreview() {
//    MyApplicationTheme {
//        GreetingView("Hello, Android!")
//    }
//}
