//package com.iwaa.utils
//
//import android.content.Context
//import android.net.ConnectivityManager
//import android.net.NetworkInfo
//import dagger.hilt.android.qualifiers.ApplicationContext
//import javax.inject.Inject
//
//interface NetworkConnectivity {
//    fun getNetworkInfo(): NetworkInfo?
//    fun isConnected(): Boolean
//}
//
//class Network @Inject constructor(@ApplicationContext val context: Context) : NetworkConnectivity {
//    override fun getNetworkInfo(): NetworkInfo? {
//        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        return cm.activeNetworkInfo
//    }
//
//    override fun isConnected(): Boolean {
//        val info = getNetworkInfo()
//        return info != null && info.isConnected
//    }
//}
