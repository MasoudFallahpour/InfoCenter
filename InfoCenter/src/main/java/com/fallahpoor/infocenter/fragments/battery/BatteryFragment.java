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

package com.fallahpoor.infocenter.fragments.battery;

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
 * BatteryFragment displays some information about the battery of the device
 * including its status, charge level, build technology etc.
 */
public class BatteryFragment extends Fragment {

    private BatteryViewModel batteryViewModel;
    private ListView listView;

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
        batteryViewModel = ViewModelProviders.of(this).get(BatteryViewModel.class);
    }

    @Override
    public void onResume() {
        super.onResume();
        batteryViewModel.getBatteryStateLiveData()
                .observe(getViewLifecycleOwner(),
                        batteryState -> listView.setAdapter(new CustomArrayAdapter(getActivity(), getListItems(batteryState))));
    }

    @Override
    public void onPause() {
        super.onPause();
        batteryViewModel.getBatteryStateLiveData().removeObservers(getViewLifecycleOwner());
    }

    private ArrayList<ListItem> getListItems(BatteryState batteryState) {

        ArrayList<ListItem> listItems = new ArrayList<>();
        ArrayList<String> itemsArrayList = getItemsArrayList();
        ArrayList<String> subItemsArrayList = getSubItemsArrayList(batteryState);

        for (int i = 0; i < itemsArrayList.size(); i++) {
            listItems.add(new OrdinaryListItem(itemsArrayList.get(i), subItemsArrayList.get(i)));
        }

        return listItems;

    }

    private ArrayList<String> getItemsArrayList() {

        return new ArrayList<>(Arrays.asList(getString(R.string.item_status),
                getString(R.string.bat_item_charge_level),
                getString(R.string.bat_item_health),
                getString(R.string.bat_item_plugged),
                getString(R.string.bat_item_technology),
                getString(R.string.bat_item_temperature),
                getString(R.string.bat_item_voltage)));

    }

    private ArrayList<String> getSubItemsArrayList(BatteryState batteryState) {
        return new ArrayList<>(
                Arrays.asList(
                        batteryState.getStatus(),
                        batteryState.getLevel(),
                        batteryState.getHealth(),
                        batteryState.getPlugged(),
                        batteryState.getTechnology(),
                        batteryState.getTemperature(),
                        batteryState.getVoltage()
                )
        );
    }

}