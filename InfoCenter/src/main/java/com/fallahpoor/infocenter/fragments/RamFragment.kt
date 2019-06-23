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

package com.fallahpoor.infocenter.fragments

import android.app.ActivityManager
import android.app.ActivityManager.MemoryInfo
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.fallahpoor.infocenter.R
import com.fallahpoor.infocenter.Utils
import com.fallahpoor.infocenter.adapters.CustomArrayAdapter
import com.fallahpoor.infocenter.adapters.ListItem
import com.fallahpoor.infocenter.adapters.OrdinaryListItem
import kotlinx.android.synthetic.main.fragment_others.*
import java.io.BufferedReader
import java.io.FileReader
import java.util.*

/**
 * RamFragment displays the total and free RAM of the device.
 */
class RamFragment : Fragment() {

    private var utils: Utils? = null

    private val listItems: ArrayList<ListItem>
        get() {
            return ArrayList<ListItem>().apply {
                add(OrdinaryListItem(getString(R.string.ram_item_total_ram), totalRam))
                add(OrdinaryListItem(getString(R.string.ram_item_free_ram), freeRam))
            }
        }

    // Returns the total RAM of the device
    private val totalRam: String
        get() {

            val buffReader: BufferedReader
            val aLine: String
            val lngTotalRam: Long
            var totalRam: String

            try {
                buffReader = BufferedReader(FileReader("/proc/meminfo"))
                aLine = buffReader.readLine()
                buffReader.close()
                val tokens =
                    aLine.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                lngTotalRam = java.lang.Long.parseLong(tokens[1]) * 1024
                totalRam = utils!!.getFormattedSize(lngTotalRam)

            } catch (ex: Exception) {
                totalRam = getString(R.string.unknown)
            }

            return totalRam

        } // end method getTotalRam

    // Returns the free RAM of the device
    private val freeRam: String
        get() {

            val memoryInfo = MemoryInfo()
            val activityManager: ActivityManager?
            val lngFreeRam: Long
            val freeRam: String

            activityManager =
                activity!!.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

            activityManager.getMemoryInfo(memoryInfo)
            lngFreeRam = memoryInfo.availMem
            freeRam = utils!!.getFormattedSize(lngFreeRam)

            return freeRam

        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_others, container, false)

        utils = Utils(activity!!)

        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listView.adapter = CustomArrayAdapter(activity, listItems)
    }

}