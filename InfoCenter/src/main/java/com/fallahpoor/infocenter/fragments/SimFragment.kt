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

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.fallahpoor.infocenter.R
import com.fallahpoor.infocenter.adapters.CustomArrayAdapter
import com.fallahpoor.infocenter.adapters.ListItem
import com.fallahpoor.infocenter.adapters.OrdinaryListItem
import kotlinx.android.synthetic.main.fragment_others.*
import java.util.*

/**
 * SimFragment displays some information about the SIM card and telephony
 * network of the device.
 */
class SimFragment : Fragment() {

    private val listItems: ArrayList<ListItem>
        get() {

            val telephonyManager = activity!!
                .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

            return ArrayList<ListItem>().apply {
                add(
                    OrdinaryListItem(
                        getString(R.string.sim_item_sim_card_serial_number),
                        getSimSerialNumber(telephonyManager)
                    )
                )
                add(
                    OrdinaryListItem(
                        getString(R.string.sim_item_phone_type),
                        getPhoneType(telephonyManager)
                    )
                )
                add(
                    OrdinaryListItem(
                        getString(R.string.sim_item_network_type),
                        getNetworkType(telephonyManager)
                    )
                )
                add(
                    OrdinaryListItem(
                        getString(R.string.sim_item_roaming),
                        getRoamingState(telephonyManager)
                    )
                )
                add(
                    OrdinaryListItem(
                        getString(R.string.sim_item_operator_name),
                        getNetworkOperatorName(telephonyManager)
                    )
                )
                add(
                    OrdinaryListItem(
                        getString(R.string.sim_item_data_network),
                        getDataConnectionState(telephonyManager)
                    )
                )
            }

        }

    // Returns true if the device supports telephony
    private val isTelephonySupported: Boolean
        get() {
            return if (activity == null) {
                false
            } else {
                val pm = activity!!.packageManager
                pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)
            }
        }

    // Returns true if the SIM is present
    private val isSimPresent: Boolean
        get() {
            return if (activity == null) {
                false
            } else {
                val telephonyManager = activity!!
                    .getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                telephonyManager.phoneType == TelephonyManager.PHONE_TYPE_GSM && telephonyManager.simState != TelephonyManager.SIM_STATE_ABSENT
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_others, container, false)

        textView.setText(R.string.sim_sub_item_no_sim)

        if (ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.READ_PHONE_STATE) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            populateListView()
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.READ_PHONE_STATE),
                REQUEST_CODE_READ_PHONE_STATE
            )
        }

        return view

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_READ_PHONE_STATE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateListView()
            } else {
                listView.emptyView = textView
            }
        }
    }

    private fun populateListView() {
        if (isTelephonySupported && isSimPresent) {
            listView.adapter = CustomArrayAdapter(activity, listItems)
        } else {
            listView.emptyView = textView
        }
    }

    @SuppressLint("MissingPermission")
    private fun getSimSerialNumber(telephonyManager: TelephonyManager): String {

        val simSerialNumber = telephonyManager.simSerialNumber

        return if (TextUtils.isEmpty(simSerialNumber)) {
            getString(R.string.unknown)
        } else {
            simSerialNumber
        }

    }

    private fun getPhoneType(telephonyManager: TelephonyManager): String {

        val phoneTypeInt = telephonyManager.phoneType
        val phoneTypeStr: String

        phoneTypeStr = when (phoneTypeInt) {
            TelephonyManager.PHONE_TYPE_CDMA -> getString(R.string.sim_sub_item_cdma)
            TelephonyManager.PHONE_TYPE_GSM -> getString(R.string.sim_sub_item_gsm)
            TelephonyManager.PHONE_TYPE_SIP -> getString(R.string.sim_sub_item_sip)
            else -> getString(R.string.unknown)
        }

        return phoneTypeStr

    }

    private fun getRoamingState(telephonyManager: TelephonyManager): String {
        return if (telephonyManager.isNetworkRoaming) {
            getString(R.string.sim_sub_item_roaming_enabled)
        } else {
            getString(R.string.sim_sub_item_roaming_disabled)
        }
    }

    private fun getNetworkOperatorName(telephonyManager: TelephonyManager): String {

        val netOpName = telephonyManager.networkOperatorName

        return if (TextUtils.isEmpty(netOpName)) {
            getString(R.string.unknown)
        } else {
            when (netOpName.toLowerCase()) {
                "ir-mci" -> getString(R.string.sim_sub_item_irmci)
                "irancell" -> getString(R.string.sim_sub_item_irancell)
                "rightel" -> getString(R.string.sim_sub_item_rightel)
                else -> getString(R.string.unknown)
            }
        }

    }

    private fun getDataConnectionState(telephonyManager: TelephonyManager): String {
        return when (telephonyManager.dataState) {
            TelephonyManager.DATA_CONNECTED -> getString(R.string.sim_sub_item_data_connected)
            TelephonyManager.DATA_CONNECTING -> getString(R.string.sim_sub_item_data_connecting)
            TelephonyManager.DATA_DISCONNECTED -> getString(R.string.sim_sub_item_data_disconnected)
            TelephonyManager.DATA_SUSPENDED -> getString(R.string.sim_sub_item_data_suspended)
            else -> getString(R.string.unknown)
        }
    }

    // Returns the type of mobile network.
    private fun getNetworkType(telephonyManager: TelephonyManager): String {
        return when (telephonyManager.networkType) {
            TelephonyManager.NETWORK_TYPE_1xRTT -> getString(R.string.sim_sub_item_1xrtt)
            TelephonyManager.NETWORK_TYPE_CDMA -> getString(R.string.sim_sub_item_cdma)
            TelephonyManager.NETWORK_TYPE_EDGE -> getString(R.string.sim_sub_item_edge)
            TelephonyManager.NETWORK_TYPE_EHRPD -> getString(R.string.sim_sub_item_ehrdp)
            TelephonyManager.NETWORK_TYPE_EVDO_0,
            TelephonyManager.NETWORK_TYPE_EVDO_A,
            TelephonyManager.NETWORK_TYPE_EVDO_B -> getString(R.string.sim_sub_item_evdo)
            TelephonyManager.NETWORK_TYPE_GPRS -> getString(R.string.sim_sub_item_gprs)
            TelephonyManager.NETWORK_TYPE_HSDPA -> getString(R.string.sim_sub_item_hsdpa)
            TelephonyManager.NETWORK_TYPE_HSPA -> getString(R.string.sim_sub_item_hspa)
            TelephonyManager.NETWORK_TYPE_HSPAP -> getString(R.string.sim_sub_item_hspap)
            TelephonyManager.NETWORK_TYPE_HSUPA -> getString(R.string.sim_sub_item_hsupa)
            TelephonyManager.NETWORK_TYPE_IDEN -> getString(R.string.sim_sub_item_iden)
            TelephonyManager.NETWORK_TYPE_LTE -> getString(R.string.sim_sub_item_lte)
            TelephonyManager.NETWORK_TYPE_UMTS -> getString(R.string.sim_sub_item_umts)
            else -> getString(R.string.unknown)
        }

    }

    companion object {
        private const val REQUEST_CODE_READ_PHONE_STATE = 1000
    }

}