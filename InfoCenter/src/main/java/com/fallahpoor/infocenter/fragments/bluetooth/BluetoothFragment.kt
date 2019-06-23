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
import android.bluetooth.BluetoothManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.fallahpoor.infocenter.R
import com.fallahpoor.infocenter.adapters.CustomArrayAdapter
import com.fallahpoor.infocenter.adapters.ListItem
import com.fallahpoor.infocenter.adapters.OrdinaryListItem
import kotlinx.android.synthetic.main.fragment_others.*
import java.util.*

/**
 * BluetoothFragment displays some information about the Bluetooth of the
 * device including its status, name, address and so on.
 */
class BluetoothFragment : Fragment(), Observer {

    private lateinit var bluetoothObservable: BluetoothObservable
    private var hasBluetooth: Boolean = false

    private val listItems: ArrayList<ListItem>
        get() {

            return ArrayList<ListItem>().apply {
                add(
                    OrdinaryListItem(
                        getString(R.string.item_status),
                        bluetoothObservable.status
                    )
                )
                add(
                    OrdinaryListItem(
                        getString(R.string.blu_item_address),
                        bluetoothObservable.address
                    )
                )
                add(
                    OrdinaryListItem(
                        getString(R.string.blu_item_name),
                        bluetoothObservable.name
                    )
                )
                add(
                    OrdinaryListItem(
                        getString(R.string.blu_item_paired_devices),
                        bluetoothObservable.pairedDevices
                    )
                )
            }

        }

    private val bluetoothAdapter: BluetoothAdapter?
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        get() {
            return if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                BluetoothAdapter.getDefaultAdapter()
            } else {
                (activity!!.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_others, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        hasBluetooth = hasBluetoothFeature()
        populateListView(textView)
    }

    private fun populateListView(msgTextView: TextView) {
        if (hasBluetooth) {
            bluetoothObservable = BluetoothObservable(activity!!)
        } else {
            listView.emptyView = msgTextView
        }
    }

    override fun onResume() {

        super.onResume()

        if (hasBluetooth) {
            bluetoothObservable.addObserver(this)
            bluetoothObservable.enableBluetoothUpdates()
            updateListView()
        }

    }

    override fun onPause() {

        super.onPause()

        if (hasBluetooth) {
            bluetoothObservable.disableBluetoothUpdates()
            bluetoothObservable.deleteObserver(this)
        }

    }

    private fun hasBluetoothFeature(): Boolean {
        return bluetoothAdapter != null
    }

    private fun updateListView() {
        listView.adapter = CustomArrayAdapter(activity, listItems)
    }

    override fun update(observable: Observable, o: Any) {
        updateListView()
    }

}
