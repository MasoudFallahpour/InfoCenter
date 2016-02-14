/*
    Copyright (C) 2014-2016 Masood Fallahpoor

    This file is part of Info Center.

    Info Center is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Info Center is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Info Center. If not, see <http://www.gnu.org/licenses/>.
 */

package com.fallahpoor.infocenter.fragments.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.fallahpoor.infocenter.R;
import com.fallahpoor.infocenter.Utils;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.Observable;
import java.util.Observer;

/**
 * This class provides the information needed by WifiFragment.
 *
 * @author Masood Fallahpoor
 */
public class WifiObservable extends Observable {

    private static final int SIGNAL_LEVELS = 5;
    private WifiBroadcastReceiver mWifiReceiver;
    private IntentFilter mWifiIntentFilter;
    private Context mContext;
    private String mStatus;
    private String mConnected;
    private String mSSID;
    private String mIpAddress;
    private String mMacAddress;
    private String mSignalStrength;
    private String mLinkSpeed;

    public WifiObservable(Context context) {

        mContext = context;
        mWifiReceiver = new WifiBroadcastReceiver();
        mWifiIntentFilter = new IntentFilter();
        mWifiIntentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        mWifiIntentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        mWifiIntentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        mWifiIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        mWifiIntentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        updateInstanceVariables();

    }

    public String getStatus() {
        return mStatus;
    }

    public String getConnected() {
        return mConnected;
    }

    public String getSSID() {
        return mSSID;
    }

    public String getIpAddress() {
        return mIpAddress;
    }

    public String getMacAddress() {
        return mMacAddress;
    }

    public String getSignalStrength() {
        return mSignalStrength;
    }

    public String getLinkSpeed() {
        return mLinkSpeed;
    }

    private void enableWifiUpdates() {
        mContext.registerReceiver(mWifiReceiver, mWifiIntentFilter);
    }

    private void disableWifiUpdates() {
        mContext.unregisterReceiver(mWifiReceiver);
    }

    @Override
    public void addObserver(Observer observer) {

        super.addObserver(observer);
        enableWifiUpdates();

    }

    @Override
    public synchronized void deleteObserver(Observer observer) {

        disableWifiUpdates();
        super.deleteObserver(observer);

    }

    private boolean isWifiEnabled(WifiManager wifiMgr) {
        return (wifiMgr.getWifiState() != WifiManager.WIFI_STATE_DISABLED &&
                wifiMgr.getWifiState() != WifiManager.WIFI_STATE_DISABLING);
    }

    private String getSSID(WifiInfo wifiInfo) {

        String ssid = wifiInfo.getSSID();

        if (ssid.startsWith("\"")) {
            ssid = ssid.substring(1);
        }

        if (ssid.endsWith("\"")) {
            ssid = ssid.substring(0, ssid.length() - 1);
        }

        return ssid;

    }

    //Returns a descriptive signal strength like "poor", "good" etc.
    private String getSignalStrength(WifiInfo wifiInfo) {

        String strength;
        int intStrength = WifiManager.calculateSignalLevel(
                wifiInfo.getRssi(), SIGNAL_LEVELS);

        switch (intStrength) {
            case 0:
                strength = mContext.getString(R.string.wifi_sub_item_poor);
                break;
            case 1:
                strength = mContext.getString(R.string.wifi_sub_item_not_bad);
                break;
            case 2:
                strength = mContext.getString(R.string.wifi_sub_item_good);
                break;
            case 3:
                strength = mContext.getString(R.string.wifi_sub_item_very_good);
                break;
            case 4:
                strength = mContext.getString(R.string.wifi_sub_item_excellent);
                break;
            default:
                strength = mContext.getString(R.string.unknown);
        }

        return strength;

    } // end method getSignalStrength

    /*
     * Receives an IP address as an integer and returns its equivalent string
     * representation.
     */
    private String getIpAddress(WifiInfo wifiInfo) {

        int intIpAddress = wifiInfo.getIpAddress();
        String strIpAddress;
        byte[] ipByteArray;

        // Convert little-endian to big-endian if needed
        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            intIpAddress = Integer.reverseBytes(intIpAddress);
        }

        ipByteArray = BigInteger.valueOf(intIpAddress).toByteArray();

        try {
            strIpAddress = InetAddress.getByAddress(ipByteArray).
                    getHostAddress();
        } catch (UnknownHostException ex) {
            strIpAddress = mContext.getString(R.string.unknown);
        }

        return strIpAddress;

    } // end method getIpAddress

    private String getLinkSpeed(WifiInfo wifiInfo) {

        return String.format(Utils.getLocale(), "%d", wifiInfo.getLinkSpeed())
                + " " + mContext.getString(R.string.wifi_sub_item_mbps);

    }

    private void updateInstanceVariables() {

        String unknown = mContext.getString(R.string.unknown);
        WifiInfo wifiInfo;
        NetworkInfo netInfo;
        ConnectivityManager connMgr = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        WifiManager wifiMgr = (WifiManager) mContext.getSystemService(
                Context.WIFI_SERVICE);

        mStatus = mContext.getString(R.string.off);
        mConnected = mContext.getString(R.string.no);
        mSSID = unknown;
        mIpAddress = unknown;
        mMacAddress = unknown;
        mSignalStrength = unknown;
        mLinkSpeed = unknown;

        if (wifiMgr != null) {
            if (isWifiEnabled(wifiMgr)) {
                mStatus = mContext.getString(R.string.on);
                netInfo = connMgr.getNetworkInfo(ConnectivityManager.
                        TYPE_WIFI);
                if (netInfo != null && netInfo.isConnected()) {
                    mConnected = mContext.getString(R.string.yes);
                    wifiInfo = wifiMgr.getConnectionInfo();
                    if (wifiInfo != null) {
                        mSSID = getSSID(wifiInfo);
                        mIpAddress = getIpAddress(wifiInfo);
                        mMacAddress = wifiInfo.getMacAddress();
                        mSignalStrength = getSignalStrength(wifiInfo);
                        mLinkSpeed = getLinkSpeed(wifiInfo);
                    }
                }
            }
        }

    } // end method updateInstanceVariables

    private class WifiBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            updateInstanceVariables();
            setChanged();
            notifyObservers();

        } // end method onReceive

    } // end class WifiBroadcastReceiver

} // end class WifiObservable
