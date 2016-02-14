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

package com.fallahpoor.infocenter.fragments;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.fallahpoor.infocenter.R;
import com.fallahpoor.infocenter.Utils;
import com.fallahpoor.infocenter.adapters.CustomArrayAdapter;
import com.fallahpoor.infocenter.adapters.ListItem;
import com.fallahpoor.infocenter.adapters.OrdinaryListItem;

import java.util.ArrayList;

/**
 * SimFragment displays some information about the SIM card and telephony
 * network of the device.
 *
 * @author Masood Fallahpoor
 */
public class SimFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_others, container,
                false);

        ListView listView = (ListView) view.findViewById(R.id.listView);

        TextView msgTextView = (TextView) view.findViewById(R.id.textView);
        msgTextView.setText(R.string.sim_sub_item_no_sim);

        populateListView(listView, msgTextView);

        return view;

    }

    private void populateListView(ListView listView, TextView msgTextView) {

        if (isTelephonySupported() && isSimPresent()) {
            listView.setAdapter(new CustomArrayAdapter(getActivity(),
                    getListItems()));
        } else {
            listView.setEmptyView(msgTextView);
        }

    }

    private ArrayList<ListItem> getListItems() {

        ArrayList<ListItem> items = new ArrayList<>();
        TelephonyManager telephonyManager = (TelephonyManager) getActivity()
                .getSystemService(Context.TELEPHONY_SERVICE);

        items.add(new OrdinaryListItem(
                getString(R.string.sim_item_sim_card_serial_number),
                getSimSerialNumber(telephonyManager)));
        items.add(new OrdinaryListItem(
                getString(R.string.sim_item_phone_type),
                getPhoneType(telephonyManager)));
        items.add(new OrdinaryListItem(
                getString(R.string.sim_item_network_type),
                getNetworkType(telephonyManager)));
        items.add(new OrdinaryListItem(
                getString(R.string.sim_item_roaming),
                getRoamingState(telephonyManager)));
        items.add(new OrdinaryListItem(
                getString(R.string.sim_item_operator_name),
                getNetworkOperatorName(telephonyManager)));
        items.add(new OrdinaryListItem(
                getString(R.string.sim_item_data_network),
                getDataConnectionState(telephonyManager)));

        return items;

    }

    // Returns true if the device supports telephony
    private boolean isTelephonySupported() {

        PackageManager pm = getActivity().getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY);

    }

    // Returns true if the SIM is present
    private boolean isSimPresent() {

        TelephonyManager telephonyManager = (TelephonyManager) getActivity()
                .getSystemService(Context.TELEPHONY_SERVICE);

        return (telephonyManager.getPhoneType() == TelephonyManager.
                PHONE_TYPE_GSM
                && telephonyManager.getSimState() != TelephonyManager.
                SIM_STATE_ABSENT);

    }

    private String getSimSerialNumber(TelephonyManager telephonyManager) {

        String simSerialNumber = telephonyManager.getSimSerialNumber();

        if (Utils.isEmpty(simSerialNumber)) {
            return getString(R.string.unknown);
        } else {
            return simSerialNumber;
        }

    }

    private String getPhoneType(TelephonyManager telephonyManager) {

        int phoneTypeInt = telephonyManager.getPhoneType();
        String phoneTypeStr;

        switch (phoneTypeInt) {
            case TelephonyManager.PHONE_TYPE_CDMA:
                phoneTypeStr = getString(R.string.sim_sub_item_cdma);
                break;
            case TelephonyManager.PHONE_TYPE_GSM:
                phoneTypeStr = getString(R.string.sim_sub_item_gsm);
                break;
            case TelephonyManager.PHONE_TYPE_SIP:
                phoneTypeStr = getString(R.string.sim_sub_item_sip);
                break;
            default:
                phoneTypeStr = getString(R.string.unknown);
        }

        return phoneTypeStr;

    }

    private String getRoamingState(TelephonyManager telephonyManager) {

        boolean isRoaming = telephonyManager.isNetworkRoaming();

        if (isRoaming) {
            return getString(R.string.sim_sub_item_roaming_enabled);
        } else {
            return getString(R.string.sim_sub_item_roaming_disabled);
        }

    }

    private String getNetworkOperatorName(TelephonyManager telephonyManager) {

        String netOpName = telephonyManager.getNetworkOperatorName();

        if (Utils.isEmpty(netOpName)) {
            netOpName = getString(R.string.unknown);
        } else {
            switch (netOpName.toLowerCase()) {
                case "ir-mci":
                    netOpName = getString(R.string.sim_sub_item_irmci);
                    break;
                case "irancell":
                    netOpName = getString(R.string.sim_sub_item_irancell);
                    break;
                case "rightel":
                    netOpName = getString(R.string.sim_sub_item_rightel);
                    break;
            }
        }

        return netOpName;

    }

    private String getDataConnectionState(TelephonyManager telephonyManager) {

        int dataStateInt = telephonyManager.getDataState();
        String dataStateStr;

        switch (dataStateInt) {
            case TelephonyManager.DATA_CONNECTED:
                dataStateStr = getString(R.string.sim_sub_item_data_connected);
                break;
            case TelephonyManager.DATA_CONNECTING:
                dataStateStr = getString(R.string.sim_sub_item_data_connecting);
                break;
            case TelephonyManager.DATA_DISCONNECTED:
                dataStateStr = getString(R.string.sim_sub_item_data_disconnected);
                break;
            case TelephonyManager.DATA_SUSPENDED:
                dataStateStr = getString(R.string.sim_sub_item_data_suspended);
                break;
            default:
                dataStateStr = getString(R.string.unknown);
        }

        return dataStateStr;

    }

    // Returns the type of mobile network.
    private String getNetworkType(TelephonyManager telephonyManager) {

        int intNetType = telephonyManager.getNetworkType();
        String netType;

        switch (intNetType) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                netType = getString(R.string.sim_sub_item_1xrtt);
                break;
            case TelephonyManager.NETWORK_TYPE_CDMA:
                netType = getString(R.string.sim_sub_item_cdma);
                break;
            case TelephonyManager.NETWORK_TYPE_EDGE:
                netType = getString(R.string.sim_sub_item_edge);
                break;
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                netType = getString(R.string.sim_sub_item_ehrdp);
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                netType = getString(R.string.sim_sub_item_evdo);
                break;
            case TelephonyManager.NETWORK_TYPE_GPRS:
                netType = getString(R.string.sim_sub_item_gprs);
                break;
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                netType = getString(R.string.sim_sub_item_hsdpa);
                break;
            case TelephonyManager.NETWORK_TYPE_HSPA:
                netType = getString(R.string.sim_sub_item_hspa);
                break;
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                netType = getString(R.string.sim_sub_item_hspap);
                break;
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                netType = getString(R.string.sim_sub_item_hsupa);
                break;
            case TelephonyManager.NETWORK_TYPE_IDEN:
                netType = getString(R.string.sim_sub_item_iden);
                break;
            case TelephonyManager.NETWORK_TYPE_LTE:
                netType = getString(R.string.sim_sub_item_lte);
                break;
            case TelephonyManager.NETWORK_TYPE_UMTS:
                netType = getString(R.string.sim_sub_item_umts);
                break;
            default:
                netType = getString(R.string.unknown);
        }

        return netType;

    } // end method getNetworkType

} // end class SimFragment