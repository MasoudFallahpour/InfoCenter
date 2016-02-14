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

package com.fallahpoor.infocenter.fragments.bluetooth;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

import com.fallahpoor.infocenter.R;

import java.util.Iterator;
import java.util.Observable;
import java.util.Set;

/**
 * @author Masood Fallahpoor
 */
public class BluetoothObservable extends Observable {

    private Context mContext;
    private BluetoothAdapter mBluetoothAdapter;
    private IntentFilter mBluetoothIntentFilter;
    private BluetoothReceiver mBluetoothReceiver;
    private String mStatus;
    private String mAddress;
    private String mName;
    private String mPairedDevices;

    public BluetoothObservable(Context context) {

        mContext = context;
        mBluetoothAdapter = getBluetoothAdapter();
        mBluetoothReceiver = new BluetoothReceiver();
        mBluetoothIntentFilter = new IntentFilter();
        mBluetoothIntentFilter.addAction(BluetoothAdapter.
                ACTION_STATE_CHANGED);
        mBluetoothIntentFilter.addAction(BluetoothAdapter.
                ACTION_LOCAL_NAME_CHANGED);
        updateInstanceVariables();

    }

    public String getStatus() {
        return mStatus;
    }

    public String getAddress() {
        return mAddress;
    }

    public String getName() {
        return mName;
    }

    public String getPairedDevices() {
        return mPairedDevices;
    }

    public void enableBluetoothUpdates() {
        mContext.registerReceiver(mBluetoothReceiver, mBluetoothIntentFilter);
    }

    public void disableBluetoothUpdates() {
        mContext.unregisterReceiver(mBluetoothReceiver);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private BluetoothAdapter getBluetoothAdapter() {

        BluetoothAdapter bluetoothAdapter;

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        } else {
            bluetoothAdapter = ((BluetoothManager) mContext.
                    getSystemService(Context.BLUETOOTH_SERVICE)).
                    getAdapter();
        }

        return bluetoothAdapter;

    }

    // Returns the names of paired devices, if any.
    private String getBondedDevices() {

        StringBuilder deviceNames = new StringBuilder();
        Set<BluetoothDevice> pairedDevices;
        Iterator<BluetoothDevice> iterator;

        if (mBluetoothAdapter == null
                || mBluetoothAdapter.getState() != BluetoothAdapter.STATE_ON) {
            deviceNames.append(mContext.getString(R.string.unknown));
        } else {
            pairedDevices = mBluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() == 0) {
                deviceNames.append(mContext.getString(R.string.
                        blu_sub_item_no_paired_devices));
            } else {
                iterator = pairedDevices.iterator();
                while (iterator.hasNext()) {
                    deviceNames.append(iterator.next().getName()).append("\n");
                }
            }
        }

        return deviceNames.toString();

    } // end method getBondedDevices

    private void updateInstanceVariables() {

        String unknown;

        if (mBluetoothAdapter.isEnabled()) {
            mStatus = mContext.getString(R.string.on);
            mAddress = mBluetoothAdapter.getAddress();
            mName = mBluetoothAdapter.getName();
            mPairedDevices = getBondedDevices();
        } else {
            unknown = mContext.getString(R.string.unknown);
            mStatus = mContext.getString(R.string.off);
            mAddress = unknown;
            mName = unknown;
            mPairedDevices = unknown;
        }

    }

    private class BluetoothReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            updateInstanceVariables();
            setChanged();
            notifyObservers();

        }

    }

} // end class BluetoothObservable
