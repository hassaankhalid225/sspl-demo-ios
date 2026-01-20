package com.sspl.android

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.sspl.core.ApiStates
import com.sspl.core.usecases.JoinSessionUseCase
import com.sspl.session.UserSession
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class SessionActivity : ComponentActivity() {
    
    private val joinSessionUseCase: JoinSessionUseCase by inject()
    private val userSession: UserSession by inject()
    private lateinit var webView: WebView
    
    companion object {
        const val EXTRA_JOIN_CODE = "join_code"
        const val EXTRA_JOIN_URL = "join_url"
        const val EXTRA_SCENARIO_TITLE = "scenario_title"
        const val EXTRA_NAME = "name"
        const val EXTRA_EMAIL = "email"
    }
    
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val joinCode = intent.getStringExtra(EXTRA_JOIN_CODE)
        val joinUrl = intent.getStringExtra(EXTRA_JOIN_URL)
        val name = intent.getStringExtra(EXTRA_NAME) ?: "Guest"
        val email = intent.getStringExtra(EXTRA_EMAIL) ?: "guest@example.com"
        
        Log.d("SessionActivity", "onCreate - joinCode: $joinCode, joinUrl: $joinUrl")
        
        if (joinCode == null) {
            Log.e("SessionActivity", "No join code provided, finishing activity")
            finish()
            return
        }
        
        // Create and configure WebView
        webView = WebView(this).apply {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
                builtInZoomControls = false
                displayZoomControls = false
            }
            
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()
        }
        
        setContentView(webView)
        
        // If we have a join URL, load it directly
        if (joinUrl != null) {
            Log.d("SessionActivity", "Loading join URL: $joinUrl")
            webView.loadUrl(joinUrl)
        } else {
            // Otherwise, join the session first
            autoJoinSession(joinCode, name, email)
        }
    }
    
    private fun autoJoinSession(joinCode: String, name: String, email: String) {
        lifecycleScope.launch {
            try {
                Log.d("SessionActivity", "Auto-joining session with code: $joinCode")
                
                // Get device token from user session
                val deviceToken = userSession.deviceToken
                Log.d("SessionActivity", "Device token: $deviceToken")
                
                joinSessionUseCase(
                    joinCode = joinCode,
                    name = name,
                    email = email,
                    deviceToken = deviceToken,
                    devicePlatform = "android"
                ).collect { state ->
                    when (state) {
                        is ApiStates.Success -> {
                            val joinUrl = state.data?.joinUrl
                            Log.d("SessionActivity", "Session joined successfully, URL: $joinUrl")
                            
                            if (joinUrl != null) {
                                // Load the join URL in the WebView
                                webView.loadUrl(joinUrl)
                            } else {
                                Log.e("SessionActivity", "Join URL is null")
                                finish()
                            }
                        }
                        is ApiStates.Failure -> {
                            Log.e("SessionActivity", "Failed to join session: ${state.error.message}")
                            // TODO: Show error message to user
                            finish()
                        }
                        is ApiStates.Loading -> {
                            Log.d("SessionActivity", "Joining session...")
                            // TODO: Show loading indicator
                        }
                        is ApiStates.Idle -> {
                            // Do nothing
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("SessionActivity", "Error auto-joining session", e)
                finish()
            }
        }
    }
    
    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
