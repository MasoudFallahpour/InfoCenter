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

package com.fallahpoor.infocenter.fragments.wifi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.fallahpoor.infocenter.R
import com.fallahpoor.infocenter.adapters.CustomArrayAdapter
import com.fallahpoor.infocenter.adapters.ListItem
import com.fallahpoor.infocenter.adapters.OrdinaryListItem
import kotlinx.android.synthetic.main.fragment_others.*
import java.util.*

/**
 * WifiFragment displays some information about Wi-Fi connection of the device
 * including: Wi-Fi status, network SSID and so on.
 */
class WifiFragment : Fragment() {

    private lateinit var wifiViewModel: WifiViewModel

    private val itemsArrayList: ArrayList<String>
        get() = ArrayList(
            listOf(
                getString(R.string.item_status),
                getString(R.string.wifi_item_connected_to_access_point),
                getString(R.string.wifi_item_ssid),
                getString(R.string.wifi_item_ip_address),
                getString(R.string.wifi_item_mac_address),
                getString(R.string.wifi_item_signal_quality),
                getString(R.string.wifi_item_link_speed)
            )
        )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_others, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        wifiViewModel = ViewModelProviders.of(this).get(WifiViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        wifiViewModel.getWifiStateLiveData()
            .observe(viewLifecycleOwner, androidx.lifecycle.Observer { wifiState ->
                listView.adapter = CustomArrayAdapter(activity, getListItems(wifiState))
            })
    }

    override fun onPause() {
        super.onPause()
        wifiViewModel.getWifiStateLiveData().removeObservers(viewLifecycleOwner)
    }

    private fun getListItems(wifiState: WifiState): ArrayList<ListItem> {

        val items = ArrayList<ListItem>()
        val itemsArrayList = itemsArrayList
        val subItemsArrayList = getSubItemsArrayList(wifiState)

        for (i in itemsArrayList.indices) {
            items.add(OrdinaryListItem(itemsArrayList[i], subItemsArrayList[i]))
        }

        return items

    }

    private fun getSubItemsArrayList(wifiState: WifiState): ArrayList<String> {

        return ArrayList(
            listOf(
                wifiState.status,
                wifiState.connected,
                wifiState.ssid,
                wifiState.ipAddress,
                wifiState.macAddress,
                wifiState.signalStrength,
                wifiState.linkSpeed
            )
        )

    }

} // end class WifiFragment