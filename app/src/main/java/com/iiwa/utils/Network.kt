package com.iiwa.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import javax.inject.Inject

class Network @Inject constructor(val context: Context) : NetworkConnectivity {
    override fun getNetworkInfo(): NetworkInfo? {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo
    }

    override fun isConnected(): Boolean {
        val info = getNetworkInfo()
        return info != null && info.isConnected
    }

    override fun getNetworkType(): NetworkType {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork
        if (network != null) {
            val capabilities = cm.getNetworkCapabilities(network)
            if (capabilities != null) {
                return when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkType.WIFI
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetworkType.MOBILE
                    else -> NetworkType.OTHER
                }
            }
        }
        return NetworkType.NONE
    }

    override fun isWifiConnected(): Boolean {
        return getNetworkType() == NetworkType.WIFI
    }

    override fun isMobileConnected(): Boolean {
        return getNetworkType() == NetworkType.MOBILE
    }
}

interface NetworkConnectivity {
    fun getNetworkInfo(): NetworkInfo?
    fun isConnected(): Boolean
    fun getNetworkType(): NetworkType
    fun isWifiConnected(): Boolean
    fun isMobileConnected(): Boolean
}
