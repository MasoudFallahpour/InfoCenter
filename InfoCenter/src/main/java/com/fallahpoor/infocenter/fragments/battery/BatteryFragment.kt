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

package com.fallahpoor.infocenter.fragments.battery

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
 * BatteryFragment displays some information about the battery of the device
 * including its status, charge level, build technology etc.
 */
class BatteryFragment : Fragment() {

    private lateinit var batteryViewModel: BatteryViewModel

    private val itemsArrayList: ArrayList<String>
        get() = ArrayList(
            listOf(
                getString(R.string.item_status),
                getString(R.string.bat_item_charge_level),
                getString(R.string.bat_item_health),
                getString(R.string.bat_item_plugged),
                getString(R.string.bat_item_technology),
                getString(R.string.bat_item_temperature),
                getString(R.string.bat_item_voltage)
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
        batteryViewModel = ViewModelProviders.of(this).get(BatteryViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        batteryViewModel.batteryStateLiveData
            .observe(viewLifecycleOwner,
                androidx.lifecycle.Observer { batteryState ->
                    listView.adapter = CustomArrayAdapter(activity, getListItems(batteryState))
                })
    }

    override fun onPause() {
        super.onPause()
        batteryViewModel.batteryStateLiveData.removeObservers(viewLifecycleOwner)
    }

    private fun getListItems(batteryState: BatteryState): ArrayList<ListItem> {

        val listItems = ArrayList<ListItem>()
        val itemsArrayList = itemsArrayList
        val subItemsArrayList = getSubItemsArrayList(batteryState)

        for (i in itemsArrayList.indices) {
            listItems.add(OrdinaryListItem(itemsArrayList[i], subItemsArrayList[i]))
        }

        return listItems

    }

    private fun getSubItemsArrayList(batteryState: BatteryState): ArrayList<String> {
        return ArrayList(
            listOf(
                batteryState.status,
                batteryState.level,
                batteryState.health,
                batteryState.plugged,
                batteryState.technology,
                batteryState.temperature,
                batteryState.voltage
            )
        )
    }

}