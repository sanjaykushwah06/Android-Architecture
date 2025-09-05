/**
 * author - gwl
 * create date - 1 Sept 2025
 * purpose - Firebase Cloud Messaging service for handling incoming push notifications
 */

package com.iiwa.pushnotification

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class IwaaFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var notificationHelper: NotificationHelper

    companion object {
        private const val TAG = "IwaaFCMService"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.e(TAG, "From: ${remoteMessage.from}")

        // Check if message contains a data payload
        if (remoteMessage.data.isNotEmpty()) {
            Log.e(TAG, "Message data payload: ${remoteMessage.data}")
            handleDataMessage(remoteMessage)
        }

        // Check if message contains a notification payload
        remoteMessage.notification?.let {
            Log.e(TAG, "Message Notification Body: ${it.body}")
            handleNotificationMessage(remoteMessage)
        }
    }

    private fun handleDataMessage(remoteMessage: RemoteMessage) {
        val data = remoteMessage.data
        val title = data["title"] ?: "Iwaa Notification"
        val message = data["message"] ?: "You have a new notification"

        // Show notification with data payload
        notificationHelper.showNotification(title, message, data)

        // Log analytics event
        Log.e(TAG, "Data message received: title=$title, message=$message")
    }

    private fun handleNotificationMessage(remoteMessage: RemoteMessage) {
        val notification = remoteMessage.notification
        val title = notification?.title ?: "Iwaa Notification"
        val message = notification?.body ?: "You have a new notification"

        // Show notification
        notificationHelper.showNotification(title, message)

        // Log analytics event
        Log.e(TAG, "Notification message received: title=$title, message=$message")
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Log.e(TAG, "Refreshed token: $token")

        // Send token to your server
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token: String) {
        // Note: Implement this method to send token to your app server
        // This is where you would typically make an API call to your backend
        // to store the FCM token for sending targeted notifications

        Log.e(TAG, "FCM Token: $token")

        // Example of what you might do:
        // 1. Store token locally (SharedPreferences, DataStore, etc.)
        // 2. Send token to your backend API
        // 3. Update user profile with new token

        // For now, we'll just log it
        Log.i(TAG, "Token should be sent to server: $token")
    }
}
