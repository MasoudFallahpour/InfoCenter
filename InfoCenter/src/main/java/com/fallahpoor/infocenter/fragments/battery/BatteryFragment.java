/*
    Copyright (C) 2014 Masood Fallahpoor

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
 * BatteryFragment displays some information about the battery of the device
 * including its status, charge level, build technology etc.
 *
 * @author Masood Fallahpoor
 */
public class BatteryFragment extends Fragment implements Observer {

    private BatteryObservable mBatteryObservable;
    private ListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_others, container,
                false);
        mListView = (ListView) view.findViewById(R.id.listView);

        mBatteryObservable = new BatteryObservable(getActivity());

        return view;

    }

    @Override
    public void onResume() {

        super.onResume();
        mBatteryObservable.addObserver(this);
        mBatteryObservable.enableBatteryUpdates();

    }

    @Override
    public void onPause() {

        super.onPause();
        mBatteryObservable.disableBatteryUpdates();
        mBatteryObservable.deleteObserver(this);

    }

    private ArrayList<ListItem> getListItems() {

        ArrayList<ListItem> listItems = new ArrayList<>();
        ArrayList<String> itemsArrayList = getItemsArrayList();
        ArrayList<String> subItemsArrayList = getSubItemsArrayList();

        for (int i = 0; i < itemsArrayList.size(); i++) {
            listItems.add(new OrdinaryListItem(itemsArrayList.get(i),
                    subItemsArrayList.get(i)));
        }

        return listItems;

    }

    private ArrayList<String> getItemsArrayList() {

        return new ArrayList<>(Arrays.asList(new String[]{
                getString(R.string.item_status),
                getString(R.string.bat_item_charge_level),
                getString(R.string.bat_item_health),
                getString(R.string.bat_item_plugged),
                getString(R.string.bat_item_technology),
                getString(R.string.bat_item_temperature),
                getString(R.string.bat_item_voltage)
        }));

    }

    private ArrayList<String> getSubItemsArrayList() {

        ArrayList<String> subItemsArrayList = new ArrayList<>();

        subItemsArrayList.add(mBatteryObservable.getStatus());
        subItemsArrayList.add(mBatteryObservable.getLevel());
        subItemsArrayList.add(mBatteryObservable.getHealth());
        subItemsArrayList.add(mBatteryObservable.getPlugged());
        subItemsArrayList.add(mBatteryObservable.getTechnology());
        subItemsArrayList.add(mBatteryObservable.getTemperature());
        subItemsArrayList.add(mBatteryObservable.getVoltage());

        return subItemsArrayList;

    }

    private void updateListView() {
        mListView.setAdapter(new CustomArrayAdapter(getActivity(),
                getListItems()));
    }

    @Override
    public void update(Observable observable, Object o) {
        updateListView();
    }
    
} // end class BatteryFragment