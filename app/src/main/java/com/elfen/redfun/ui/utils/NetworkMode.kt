package com.elfen.redfun.ui.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

fun isWifiNetwork(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = cm.activeNetwork
    val caps = cm.getNetworkCapabilities(network)
    val isWifi = caps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true

    return isWifi
}
