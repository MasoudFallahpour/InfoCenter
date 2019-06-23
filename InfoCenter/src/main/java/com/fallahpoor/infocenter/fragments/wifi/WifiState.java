package com.fallahpoor.infocenter.fragments.wifi;

class WifiState {

    private String status;
    private String connected;
    private String ssid;
    private String ipAddress;
    private String macAddress;
    private String signalStrength;
    private String linkSpeed;

    String getStatus() {
        return status;
    }

    void setStatus(String status) {
        this.status = status;
    }

    String getConnected() {
        return connected;
    }

    void setConnected(String connected) {
        this.connected = connected;
    }

    String getSsid() {
        return ssid;
    }

    void setSsid(String ssid) {
        this.ssid = ssid;
    }

    String getIpAddress() {
        return ipAddress;
    }

    void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    String getMacAddress() {
        return macAddress;
    }

    void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    String getSignalStrength() {
        return signalStrength;
    }

    void setSignalStrength(String signalStrength) {
        this.signalStrength = signalStrength;
    }

    String getLinkSpeed() {
        return linkSpeed;
    }

    void setLinkSpeed(String linkSpeed) {
        this.linkSpeed = linkSpeed;
    }

}
