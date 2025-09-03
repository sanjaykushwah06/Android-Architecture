/**
 * author - gwl
 * create date - 1 Sept 2025
 * purpose - Network repository for handling network state and connectivity monitoring
 */

package com.iiwa.data.repository

import android.net.NetworkInfo
import com.iiwa.utils.NetworkConnectivity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkRepository @Inject constructor(
    private val networkConnectivity: NetworkConnectivity
) {

    fun isNetworkAvailable(): Boolean = networkConnectivity.isConnected()

    fun getNetworkInfo(): NetworkInfo? = networkConnectivity.getNetworkInfo()

    fun checkNetworkStatus(): String {
        return if (networkConnectivity.isConnected()) {
            "Network Connected"
        } else {
            "No Network Available"
        }
    }
}
