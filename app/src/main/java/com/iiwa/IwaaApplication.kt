/**
 * author - gwl
 * create date - 1 Sept 2025
 * purpose - Application class for dependency injection and Firebase initialization
 */

package com.iiwa

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.iiwa.pushnotification.FirebaseMessagingManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class IwaaApplication : Application() {
    
    @Inject
    lateinit var firebaseMessagingManager: FirebaseMessagingManager
    
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    override fun onCreate() {
        super.onCreate()
        
        try {
            // Initialize Firebase
            FirebaseApp.initializeApp(this)
            
            // Initialize Firebase Cloud Messaging
            initializeFirebaseMessaging()
            
            Log.d(TAG, "IwaaApplication initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize IwaaApplication", e)
        }
    }

    private fun initializeFirebaseMessaging() {
        try {
            applicationScope.launch {
                // Get FCM token
                val token = firebaseMessagingManager.getToken()
                if (token != null) {
                    Log.d(TAG, "FCM Token obtained: $token")
                    // TODO: Send token to your server here
                    // You can store it locally or send it to your backend API
                }
                
                // Subscribe to general topics if needed
                // firebaseMessagingManager.subscribeToTopic("general")
            }
            
            Log.d(TAG, "Firebase Cloud Messaging initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Firebase Cloud Messaging", e)
        }
    }
    
    companion object {
        private const val TAG = "IwaaApplication"
    }
}

