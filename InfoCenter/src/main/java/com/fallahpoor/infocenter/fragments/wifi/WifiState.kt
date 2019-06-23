package com.fallahpoor.infocenter.fragments.wifi

internal data class WifiState constructor(
    var status: String,
    var connected: String,
    var ssid: String,
    var ipAddress: String,
    var macAddress: String,
    var signalStrength: String,
    var linkSpeed: String
)
