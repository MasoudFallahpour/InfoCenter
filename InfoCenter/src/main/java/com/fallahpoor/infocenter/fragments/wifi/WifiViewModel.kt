package com.fallahpoor.infocenter.fragments.wifi

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fallahpoor.infocenter.R
import java.math.BigInteger
import java.net.InetAddress
import java.net.UnknownHostException
import java.nio.ByteOrder
import java.util.*

class WifiViewModel(application: Application) : AndroidViewModel(application) {

    private val wifiStateLiveData: MutableLiveData<WifiState>
    private val context: Context
    private val wifiBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            updateWifiStateLiveData()
        }
    }
    private val wifiIntentFilter: IntentFilter
        get() {
            return IntentFilter()
                .apply {
                    addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
                    addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)
                    addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)
                    addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
                    addAction(WifiManager.RSSI_CHANGED_ACTION)
                }
        }

    init {
        context = application
        wifiStateLiveData = MutableLiveData()
    }

    internal fun getWifiStateLiveData(): LiveData<WifiState> {
        context.registerReceiver(wifiBroadcastReceiver, wifiIntentFilter)
        return wifiStateLiveData
    }

    override fun onCleared() {
        super.onCleared()
        context.unregisterReceiver(wifiBroadcastReceiver)
    }

    private fun updateWifiStateLiveData() {

        val wifiState = WifiState(
            context.getString(R.string.off),
            context.getString(R.string.no),
            context.getString(R.string.unknown),
            context.getString(R.string.unknown),
            context.getString(R.string.unknown),
            context.getString(R.string.unknown),
            context.getString(R.string.unknown)
        )

        val wifiMgr =
            context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        if (isWifiEnabled(wifiMgr)) {
            wifiState.status = context.getString(R.string.on)
            val connMgr =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo: NetworkInfo? = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
            if (netInfo != null && netInfo.isConnected) {
                wifiState.connected = context.getString(R.string.yes)
                val wifiInfo: WifiInfo? = wifiMgr.connectionInfo
                if (wifiInfo != null) {
                    wifiState.ssid = getSsid(wifiInfo)
                    wifiState.ipAddress = getIpAddress(wifiInfo)
                    wifiState.macAddress = wifiInfo.macAddress
                    wifiState.signalStrength = getSignalStrength(wifiInfo)
                    wifiState.linkSpeed = getLinkSpeed(wifiInfo)
                }
            }
        }

        wifiStateLiveData.value = wifiState

    }

    private fun isWifiEnabled(wifiMgr: WifiManager): Boolean {
        return wifiMgr.wifiState != WifiManager.WIFI_STATE_DISABLED && wifiMgr.wifiState != WifiManager.WIFI_STATE_DISABLING
    }

    private fun getSsid(wifiInfo: WifiInfo): String {

        var ssid = wifiInfo.ssid

        if (ssid.startsWith("\"")) {
            ssid = ssid.substring(1)
        }

        if (ssid.endsWith("\"")) {
            ssid = ssid.substring(0, ssid.length - 1)
        }

        return ssid

    }

    private fun getSignalStrength(wifiInfo: WifiInfo): String {

        return when (WifiManager.calculateSignalLevel(wifiInfo.rssi, SIGNAL_LEVELS)) {
            0 -> context.getString(R.string.wifi_sub_item_poor)
            1 -> context.getString(R.string.wifi_sub_item_not_bad)
            2 -> context.getString(R.string.wifi_sub_item_good)
            3 -> context.getString(R.string.wifi_sub_item_very_good)
            4 -> context.getString(R.string.wifi_sub_item_excellent)
            else -> context.getString(R.string.unknown)
        }

    }

    private fun getIpAddress(wifiInfo: WifiInfo): String {

        var intIpAddress = wifiInfo.ipAddress
        val strIpAddress: String
        val ipByteArray: ByteArray

        // Convert little-endian to big-endian if needed
        if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
            intIpAddress = Integer.reverseBytes(intIpAddress)
        }

        ipByteArray = BigInteger.valueOf(intIpAddress.toLong()).toByteArray()

        strIpAddress = try {
            InetAddress.getByAddress(ipByteArray).hostAddress
        } catch (ex: UnknownHostException) {
            context.getString(R.string.unknown)
        }

        return strIpAddress

    }

    private fun getLinkSpeed(wifiInfo: WifiInfo): String {
        val locale = Locale("fa", "IR")
        return (String.format(locale, "%d", wifiInfo.linkSpeed)
                + " " + context.getString(R.string.wifi_sub_item_mbps))
    }

    private companion object {
        private const val SIGNAL_LEVELS = 5
    }

}
