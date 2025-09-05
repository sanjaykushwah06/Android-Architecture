/**
 * author - gwl
 * create date - 1 Sept 2025
 * purpose - Network utility functions for connectivity checks and monitoring
 */

package com.iiwa.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkUtils @Inject constructor(
    @ApplicationContext private val context: Context
) {

    /**
     * Check if device has internet connectivity
     */
    fun isInternetAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork
        val networkCapabilities = network?.let { connectivityManager.getNetworkCapabilities(it) }

        // Check if network has internet capability (less strict than VALIDATED)
        return network != null && networkCapabilities != null &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    /**
     * Check if device is connected to WiFi
     */
    fun isWifiConnected(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork
        val networkCapabilities = network?.let { connectivityManager.getNetworkCapabilities(it) }

        return network != null && networkCapabilities != null &&
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }

    /**
     * Check if device is connected to mobile data
     */
    fun isMobileDataConnected(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork
        val networkCapabilities = network?.let { connectivityManager.getNetworkCapabilities(it) }

        return network != null && networkCapabilities != null &&
                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    }

    /**
     * Get current network type as string
     */
    fun getNetworkType(): String {
        return when {
            isWifiConnected() -> "WiFi"
            isMobileDataConnected() -> "Mobile Data"
            isInternetAvailable() -> "Other Network"
            else -> "No Internet"
        }
    }

    /**
     * Get detailed network information for debugging
     */
    fun getNetworkDebugInfo(): String {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork
        val networkCapabilities = network?.let { connectivityManager.getNetworkCapabilities(it) }

        return buildString {
            appendLine("Active Network: ${network != null}")
            appendLine("Network Capabilities: ${networkCapabilities != null}")
            if (networkCapabilities != null) {
                appendLine(
                    "Has Internet: ${
                        networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    }"
                )
                appendLine(
                    "Has Validated: ${
                        networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                    }"
                )
                appendLine(
                    "Is WiFi: ${
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    }"
                )
                appendLine(
                    "Is Cellular: ${
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    }"
                )
            }
        }
    }
}
