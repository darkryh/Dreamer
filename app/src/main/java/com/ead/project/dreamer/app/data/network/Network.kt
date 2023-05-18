package com.ead.project.dreamer.app.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiManager
import android.os.Build
import android.text.format.Formatter
import com.ead.project.dreamer.app.App
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object Network {

    private val context : Context by lazy { App.Instance }
    private val connectivityManager: ConnectivityManager = context.getSystemService(ConnectivityManager::class.java)
    private val wifiManager: WifiManager = context.applicationContext.getSystemService(WifiManager::class.java)

    private val networkCallback: NetworkCallback =
        object : NetworkCallback() {

            override fun onCapabilitiesChanged(
                network: android.net.Network,
                networkCapabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)

                if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    setState(NetworkType.Wifi)
                } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    setState(NetworkType.Cellular)
                } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    setState(NetworkType.Ethernet)
                } else {
                    setState(NetworkType.None)
                }

            }
        }

    private val _networkState : MutableStateFlow<NetworkType> = MutableStateFlow(NetworkType.None)
    val networkState : StateFlow<NetworkType> = _networkState

    fun setState(networkType: NetworkType) {
        _networkState.value = networkType
    }

    fun registerCallBack(request : NetworkRequest) {
        connectivityManager.registerNetworkCallback(request,networkCallback)
    }

    fun unregisterCallback() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    @Suppress("DEPRECATION")
    fun getIpAddress() : String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val properties = connectivityManager.getLinkProperties(connectivityManager.activeNetwork)
            properties?.let { linkProperties -> linkProperties.linkAddresses[1].address?.hostAddress }?:"localhost"
        }
        else {
            val dhcp = wifiManager.dhcpInfo
            Formatter.formatIpAddress(dhcp.ipAddress)
        }
    }
}