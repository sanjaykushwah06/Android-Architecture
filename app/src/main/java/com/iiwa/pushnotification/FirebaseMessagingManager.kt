package com.iiwa.pushnotification

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * author - gwl
 * create date - 1 Sept 2025
 * purpose - Firebase Cloud Messaging manager for token management and topic subscriptions
 */
@Singleton
class FirebaseMessagingManager @Inject constructor() {

    companion object {
        private const val TAG = "FirebaseMessagingManager"
    }

    suspend fun getToken(): String? {
        return try {
            val token = FirebaseMessaging.getInstance().token.await()
            Log.d(TAG, "FCM Token: $token")
            token
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get FCM token", e)
            null
        }
    }

    suspend fun subscribeToTopic(topic: String) {
        try {
            FirebaseMessaging.getInstance().subscribeToTopic(topic).await()
            Log.d(TAG, "Successfully subscribed to topic: $topic")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to subscribe to topic: $topic", e)
        }
    }

    suspend fun unsubscribeFromTopic(topic: String) {
        try {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(topic).await()
            Log.d(TAG, "Successfully unsubscribed from topic: $topic")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to unsubscribe from topic: $topic", e)
        }
    }

    fun deleteToken() {
        try {
            FirebaseMessaging.getInstance().deleteToken()
            Log.d(TAG, "FCM token deleted")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete FCM token", e)
        }
    }
}