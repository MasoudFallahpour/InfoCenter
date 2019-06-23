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

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.fallahpoor.infocenter.R
import com.fallahpoor.infocenter.adapters.CustomArrayAdapter
import com.fallahpoor.infocenter.adapters.ListItem
import com.fallahpoor.infocenter.adapters.OrdinaryListItem
import kotlinx.android.synthetic.main.fragment_others.*
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.util.*

/**
 * AndroidFragment displays some information about device's Android version.
 */
class AndroidFragment : Fragment() {

    private val listItems: ArrayList<ListItem>
        get() {

            return ArrayList<ListItem>().apply {
                add(OrdinaryListItem(getString(R.string.and_item_version), Build.VERSION.RELEASE))
                add(OrdinaryListItem(getString(R.string.and_item_name), versionName))
                add(OrdinaryListItem(getString(R.string.and_item_sdk_number), sdkNumber))
                add(OrdinaryListItem(getString(R.string.and_item_kernel_version), kernelInfo))
            }

        }

    /*
     * Returns some information about the Android kernel including its version,
     * build date and so forth.
     */
    private val kernelInfo: String
        get() {

            var kernelInfo: String

            try {
                val buffReader = BufferedReader(FileReader("/proc/version"))
                kernelInfo = buffReader.readLine()
                buffReader.close()
            } catch (ex: IOException) {
                kernelInfo = getString(R.string.unknown)
            }

            return kernelInfo

        }

    private val sdkNumber: String
        get() {
            return String.format(Locale("fa", "IR"), "%d", Build.VERSION.SDK_INT)
        }

    private val versionName: String
        get() {

            when (Build.VERSION.SDK_INT) {
                Build.VERSION_CODES.CUPCAKE -> return getString(R.string.and_sub_item_cupcake)
                Build.VERSION_CODES.DONUT -> return getString(R.string.and_sub_item_donut)
                Build.VERSION_CODES.ECLAIR, Build.VERSION_CODES.ECLAIR_0_1, Build.VERSION_CODES.ECLAIR_MR1 -> return getString(
                    R.string.and_sub_item_eclair
                )
                Build.VERSION_CODES.FROYO -> return getString(R.string.and_sub_item_froyo)
                Build.VERSION_CODES.GINGERBREAD, Build.VERSION_CODES.GINGERBREAD_MR1 -> return getString(
                    R.string.and_sub_item_gb
                )
                Build.VERSION_CODES.HONEYCOMB, Build.VERSION_CODES.HONEYCOMB_MR1, Build.VERSION_CODES.HONEYCOMB_MR2 -> return getString(
                    R.string.and_sub_item_hc
                )
                Build.VERSION_CODES.ICE_CREAM_SANDWICH, Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1 -> return getString(
                    R.string.and_sub_item_ics
                )
                Build.VERSION_CODES.JELLY_BEAN, Build.VERSION_CODES.JELLY_BEAN_MR1, Build.VERSION_CODES.JELLY_BEAN_MR2 -> return getString(
                    R.string.and_sub_item_jb
                )
                Build.VERSION_CODES.KITKAT -> return getString(R.string.and_sub_item_kk)
                Build.VERSION_CODES.LOLLIPOP, Build.VERSION_CODES.LOLLIPOP_MR1 -> return getString(R.string.and_sub_item_lollipop)
                Build.VERSION_CODES.M -> return getString(R.string.and_sub_item_marshmallow)
                Build.VERSION_CODES.N, Build.VERSION_CODES.N_MR1 -> return getString(R.string.and_sub_item_nougat)
                Build.VERSION_CODES.O, Build.VERSION_CODES.O_MR1 -> return getString(R.string.and_sub_item_oreo)
                Build.VERSION_CODES.P -> return getString(R.string.and_sub_item_pie)
                else -> return getString(R.string.unknown)
            }

        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_others, container, false)
        listView.adapter = CustomArrayAdapter(activity, listItems)
        return view
    }

}