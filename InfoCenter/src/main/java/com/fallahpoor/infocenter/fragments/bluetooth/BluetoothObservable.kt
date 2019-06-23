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

package com.fallahpoor.infocenter.fragments.bluetooth

import android.annotation.TargetApi
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import com.fallahpoor.infocenter.R
import java.util.*

internal class BluetoothObservable(private val mContext: Context) : Observable() {

    private val mBluetoothAdapter: BluetoothAdapter?
    private val bluetoothIntentFilter: IntentFilter
    private val bluetoothReceiver: BluetoothReceiver
    var status: String? = null
        private set
    var address: String? = null
        private set
    var name: String? = null
        private set
    var pairedDevices: String? = null
        private set

    private val bluetoothAdapter: BluetoothAdapter
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        get() {

            return if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                BluetoothAdapter.getDefaultAdapter()
            } else {
                (mContext.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
            }

        }

    // Returns the names of paired devices, if any.
    private val bondedDevices: String
        get() {

            val deviceNames = StringBuilder()
            val pairedDevices: Set<BluetoothDevice>
            val iterator: Iterator<BluetoothDevice>

            if (mBluetoothAdapter == null || mBluetoothAdapter.state != BluetoothAdapter.STATE_ON) {
                deviceNames.append(mContext.getString(R.string.unknown))
            } else {
                pairedDevices = mBluetoothAdapter.bondedDevices
                if (pairedDevices.isEmpty()) {
                    deviceNames.append(mContext.getString(R.string.blu_sub_item_no_paired_devices))
                } else {
                    iterator = pairedDevices.iterator()
                    while (iterator.hasNext()) {
                        deviceNames.append(iterator.next().name).append("\n")
                    }
                }
            }

            return deviceNames.toString()

        }

    init {
        mBluetoothAdapter = bluetoothAdapter
        bluetoothReceiver = BluetoothReceiver()
        bluetoothIntentFilter = IntentFilter()
        bluetoothIntentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        bluetoothIntentFilter.addAction(BluetoothAdapter.ACTION_LOCAL_NAME_CHANGED)
        updateInstanceVariables()

    }

    fun enableBluetoothUpdates() {
        mContext.registerReceiver(bluetoothReceiver, bluetoothIntentFilter)
    }

    fun disableBluetoothUpdates() {
        mContext.unregisterReceiver(bluetoothReceiver)
    }

    private fun updateInstanceVariables() {

        val unknown: String

        if (mBluetoothAdapter!!.isEnabled) {
            status = mContext.getString(R.string.on)
            address = mBluetoothAdapter.address
            name = mBluetoothAdapter.name
            pairedDevices = bondedDevices
        } else {
            unknown = mContext.getString(R.string.unknown)
            status = mContext.getString(R.string.off)
            address = unknown
            name = unknown
            pairedDevices = unknown
        }

    }

    private inner class BluetoothReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            updateInstanceVariables()
            setChanged()
            notifyObservers()
        }

    }

} // end class BluetoothObservable
