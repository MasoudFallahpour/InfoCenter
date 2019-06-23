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

import android.graphics.Point
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Display
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
import java.util.*

/**
 * ScreenFragment shows some information about the screen of the device
 * including its resolution, DPI and so on.
 */
class ScreenFragment : Fragment() {

    private lateinit var utils: Utils

    private val listItems: ArrayList<ListItem>
        get() {

            val listItems = ArrayList<ListItem>()
            val itemsArrayList = itemsArrayList
            val subItemsArrayList = subItemsArrayList

            for (i in itemsArrayList.indices) {
                listItems.add(OrdinaryListItem(itemsArrayList[i], subItemsArrayList[i]))
            }

            return listItems

        }

    private val itemsArrayList: ArrayList<String>
        get() = ArrayList(
            listOf(
                getString(R.string.scr_item_resolution),
                getString(R.string.scr_item_orientation),
                getString(R.string.scr_item_refresh_rate),
                getString(R.string.scr_item_dots_per_inch),
                getString(R.string.scr_item_horizontal_dpi),
                getString(R.string.scr_item_vertical_dpi)
            )
        )

    private val subItemsArrayList: ArrayList<String>
        get() {

            val displaySize = Point()
            val displayMetrics = DisplayMetrics()
            val display = activity!!.windowManager.defaultDisplay.apply {
                getSize(displaySize)
                getMetrics(displayMetrics)
            }

            return ArrayList<String>()
                .apply {
                    add(getDisplaySize(displaySize))
                    add(getOrientation(displaySize))
                    add(getRefreshRate(display))
                    add(getDpi(displayMetrics))
                    add(getHorizontalDpi(displayMetrics))
                    add(getVerticalDpi(displayMetrics))
                }

        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_others, container, false)

        utils = Utils(activity!!)

        listView.adapter = CustomArrayAdapter(activity, listItems)

        return view

    }

    private fun getDisplaySize(displaySize: Point): String {
        return String.format(utils.locale, "%d x %d", displaySize.x, displaySize.y)
    }

    private fun getOrientation(displaySize: Point): String {
        return when {
            displaySize.x == displaySize.y -> getString(R.string.scr_sub_item_square)
            displaySize.x < displaySize.y -> getString(R.string.scr_sub_item_portrait)
            else -> getString(R.string.scr_sub_item_landscape)
        }
    }

    private fun getRefreshRate(display: Display): String {
        return String.format(
            utils.locale,
            "%.0f %s",
            display.refreshRate,
            getString(R.string.scr_sub_item_hertz)
        )
    }

    private fun getDpi(displayMetrics: DisplayMetrics): String {
        return String.format(utils.locale, "%d", displayMetrics.densityDpi)
    }

    private fun getHorizontalDpi(displayMetrics: DisplayMetrics): String {
        return String.format(utils.locale, "%.0f", displayMetrics.xdpi)
    }

    private fun getVerticalDpi(displayMetrics: DisplayMetrics): String {
        return String.format(utils.locale, "%.0f", displayMetrics.ydpi)
    }

}