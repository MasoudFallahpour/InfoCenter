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
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
import java.util.Observable;
import java.util.Observer;

/**
 * WifiFragment displays some information about Wi-Fi connection of the device
 * including: Wi-Fi status, network SSID and so on.
 *
 * @author Masood Fallahpoor
 */
public class WifiFragment extends Fragment implements Observer {

    private ListView mListView;
    private WifiObservable wifiObservable;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_others, container,
                false);
        mListView = view.findViewById(R.id.listView);
        wifiObservable = new WifiObservable(getActivity());
        return view;

    }

    @Override
    public void onResume() {

        super.onResume();
        wifiObservable.addObserver(this);
        updateListView();

    }

    @Override
    public void onPause() {

        super.onPause();
        wifiObservable.deleteObserver(this);

    }

    private ArrayList<ListItem> getListItems() {

        ArrayList<ListItem> items = new ArrayList<>();
        ArrayList<String> itemsArrayList = getItemsArrayList();
        ArrayList<String> subItemsArrayList = getSubItemsArrayList();

        for (int i = 0; i < itemsArrayList.size(); i++) {
            items.add(new OrdinaryListItem(itemsArrayList.get(i),
                    subItemsArrayList.get(i)));
        }

        return items;

    }

    private ArrayList<String> getItemsArrayList() {

        return new ArrayList<>(Arrays.asList(
                getString(R.string.item_status),
                getString(R.string.wifi_item_connected_to_access_point),
                getString(R.string.wifi_item_ssid),
                getString(R.string.wifi_item_ip_address),
                getString(R.string.wifi_item_mac_address),
                getString(R.string.wifi_item_signal_quality),
                getString(R.string.wifi_item_link_speed)));

    }

    private ArrayList<String> getSubItemsArrayList() {

        ArrayList<String> subItems = new ArrayList<>();

        subItems.add(wifiObservable.getStatus());
        subItems.add(wifiObservable.getConnected());
        subItems.add(wifiObservable.getSSID());
        subItems.add(wifiObservable.getIpAddress());
        subItems.add(wifiObservable.getMacAddress());
        subItems.add(wifiObservable.getSignalStrength());
        subItems.add(wifiObservable.getLinkSpeed());

        return subItems;

    }

    private void updateListView() {
        mListView.setAdapter(new CustomArrayAdapter(getActivity(),
                getListItems()));
    }

    @Override
    public void update(Observable observable, Object o) {
        updateListView();
    }

} // end class WifiFragment