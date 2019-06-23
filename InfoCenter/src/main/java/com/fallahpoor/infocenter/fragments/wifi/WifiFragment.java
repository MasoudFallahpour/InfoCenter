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

package com.fallahpoor.infocenter.fragments.wifi;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.fallahpoor.infocenter.R;
import com.fallahpoor.infocenter.adapters.CustomArrayAdapter;
import com.fallahpoor.infocenter.adapters.ListItem;
import com.fallahpoor.infocenter.adapters.OrdinaryListItem;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

/**
 * WifiFragment displays some information about Wi-Fi connection of the device
 * including: Wi-Fi status, network SSID and so on.
 *
 * @author Masood Fallahpoor
 */
public class WifiFragment extends Fragment {

    private ListView listView;
    private WifiViewModel wifiViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_others, container, false);
        listView = view.findViewById(R.id.listView);
        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        wifiViewModel = ViewModelProviders.of(this).get(WifiViewModel.class);
    }

    @Override
    public void onResume() {
        super.onResume();
        wifiViewModel.getWifiStateLiveData()
                .observe(getViewLifecycleOwner(),
                        wifiState -> listView.setAdapter(new CustomArrayAdapter(getActivity(), getListItems(wifiState))));
    }

    @Override
    public void onPause() {
        super.onPause();
        wifiViewModel.getWifiStateLiveData().removeObservers(getViewLifecycleOwner());
    }

    private ArrayList<ListItem> getListItems(final WifiState wifiState) {

        ArrayList<ListItem> items = new ArrayList<>();
        ArrayList<String> itemsArrayList = getItemsArrayList();
        ArrayList<String> subItemsArrayList = getSubItemsArrayList(wifiState);

        for (int i = 0; i < itemsArrayList.size(); i++) {
            items.add(new OrdinaryListItem(itemsArrayList.get(i), subItemsArrayList.get(i)));
        }

        return items;

    }

    private ArrayList<String> getItemsArrayList() {

        return new ArrayList<>(
                Arrays.asList(
                        getString(R.string.item_status),
                        getString(R.string.wifi_item_connected_to_access_point),
                        getString(R.string.wifi_item_ssid),
                        getString(R.string.wifi_item_ip_address),
                        getString(R.string.wifi_item_mac_address),
                        getString(R.string.wifi_item_signal_quality),
                        getString(R.string.wifi_item_link_speed)
                )
        );

    }

    private ArrayList<String> getSubItemsArrayList(WifiState wifiState) {

        return new ArrayList<>(
                Arrays.asList(
                        wifiState.getStatus(),
                        wifiState.getConnected(),
                        wifiState.getSsid(),
                        wifiState.getIpAddress(),
                        wifiState.getMacAddress(),
                        wifiState.getSignalStrength(),
                        wifiState.getLinkSpeed()
                )
        );

    }

} // end class WifiFragment