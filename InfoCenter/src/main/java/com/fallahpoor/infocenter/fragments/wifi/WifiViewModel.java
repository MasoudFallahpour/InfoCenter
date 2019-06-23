package com.fallahpoor.infocenter.fragments.wifi;

import android.app.Application;
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

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class WifiViewModel extends AndroidViewModel {

    private static final int SIGNAL_LEVELS = 5;
    private MutableLiveData<WifiState> wifiStateLiveData;
    private Context context;
    private BroadcastReceiver wifiBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateWifiStateLiveData();
        }
    };

    public WifiViewModel(@NonNull Application application) {
        super(application);
        context = application;
        wifiStateLiveData = new MutableLiveData<>();
    }

    LiveData<WifiState> getWifiStateLiveData() {
        context.registerReceiver(wifiBroadcastReceiver, getWifiIntentFilter());
        return wifiStateLiveData;
    }

    private IntentFilter getWifiIntentFilter() {
        IntentFilter wifiIntentFilter = new IntentFilter();
        wifiIntentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        wifiIntentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);
        wifiIntentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        wifiIntentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        wifiIntentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        return wifiIntentFilter;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        context.unregisterReceiver(wifiBroadcastReceiver);
    }

    private void updateWifiStateLiveData() {

        String unknown = context.getString(R.string.unknown);
        WifiInfo wifiInfo;
        NetworkInfo netInfo;
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        WifiManager wifiMgr = (WifiManager) context.getApplicationContext().getSystemService(
                Context.WIFI_SERVICE);
        WifiState wifiState = new WifiState();

        wifiState.setStatus(context.getString(R.string.off));
        wifiState.setConnected(context.getString(R.string.no));
        wifiState.setSsid(unknown);
        wifiState.setIpAddress(unknown);
        wifiState.setMacAddress(unknown);
        wifiState.setSignalStrength(unknown);
        wifiState.setLinkSpeed(unknown);

        if (wifiMgr != null && isWifiEnabled(wifiMgr)) {
            wifiState.setStatus(context.getString(R.string.on));
            netInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (netInfo != null && netInfo.isConnected()) {
                wifiState.setConnected(context.getString(R.string.yes));
                wifiInfo = wifiMgr.getConnectionInfo();
                if (wifiInfo != null) {
                    wifiState.setSsid(getSsid(wifiInfo));
                    wifiState.setIpAddress(getIpAddress(wifiInfo));
                    wifiState.setMacAddress(wifiInfo.getMacAddress());
                    wifiState.setSignalStrength(getSignalStrength(wifiInfo));
                    wifiState.setLinkSpeed(getLinkSpeed(wifiInfo));
                }
            }
        }

        wifiStateLiveData.setValue(wifiState);

    }

    private boolean isWifiEnabled(WifiManager wifiMgr) {
        return (wifiMgr.getWifiState() != WifiManager.WIFI_STATE_DISABLED &&
                wifiMgr.getWifiState() != WifiManager.WIFI_STATE_DISABLING);
    }

    private String getSsid(WifiInfo wifiInfo) {

        String ssid = wifiInfo.getSSID();

        if (ssid.startsWith("\"")) {
            ssid = ssid.substring(1);
        }

        if (ssid.endsWith("\"")) {
            ssid = ssid.substring(0, ssid.length() - 1);
        }

        return ssid;

    }

    private String getSignalStrength(WifiInfo wifiInfo) {

        int intStrength = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), SIGNAL_LEVELS);

        switch (intStrength) {
            case 0:
                return context.getString(R.string.wifi_sub_item_poor);
            case 1:
                return context.getString(R.string.wifi_sub_item_not_bad);
            case 2:
                return context.getString(R.string.wifi_sub_item_good);
            case 3:
                return context.getString(R.string.wifi_sub_item_very_good);
            case 4:
                return context.getString(R.string.wifi_sub_item_excellent);
            default:
                return context.getString(R.string.unknown);
        }

    }

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
            strIpAddress = context.getString(R.string.unknown);
        }

        return strIpAddress;

    }

    private String getLinkSpeed(WifiInfo wifiInfo) {
        Utils utils = new Utils(context);
        return String.format(utils.getLocale(), "%d", wifiInfo.getLinkSpeed())
                + " " + context.getString(R.string.wifi_sub_item_mbps);
    }

}
