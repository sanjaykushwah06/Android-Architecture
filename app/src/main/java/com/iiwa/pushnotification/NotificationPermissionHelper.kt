/**
 * author - gwl
 * create date - 1 Sept 2025
 * purpose - Helper for managing notification permissions on Android 13+
 */

package com.iiwa.pushnotification

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationPermissionHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    companion object {
        const val NOTIFICATION_PERMISSION = Manifest.permission.POST_NOTIFICATIONS
    }
    
    /**
     * Check if notification permission is granted
     */
    fun isNotificationPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                NOTIFICATION_PERMISSION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // For Android 12 and below, notifications are enabled by default
            true
        }
    }
    
    /**
     * Check if we should show rationale for notification permission
     */
    fun shouldShowNotificationPermissionRationale(activity: ComponentActivity): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            activity.shouldShowRequestPermissionRationale(NOTIFICATION_PERMISSION)
        } else {
            false
        }
    }
    
    /**
     * Request notification permission using activity result launcher
     */
    fun createNotificationPermissionLauncher(
        activity: ComponentActivity,
        onPermissionResult: (Boolean) -> Unit
    ) = activity.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        onPermissionResult(isGranted)
    }
}
