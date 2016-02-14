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

package com.fallahpoor.infocenter.fragments.bluetooth;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.fallahpoor.infocenter.R;
import com.fallahpoor.infocenter.adapters.CustomArrayAdapter;
import com.fallahpoor.infocenter.adapters.ListItem;
import com.fallahpoor.infocenter.adapters.OrdinaryListItem;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * BluetoothFragment displays some information about the Bluetooth of the
 * device including its status, name, address and so on.
 *
 * @author Masood Fallahpoor
 */
public class BluetoothFragment extends Fragment implements Observer {

    private BluetoothObservable mBluetoothObservable;
    private ListView mListView;
    private boolean mHasBluetooth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_others, container,
                false);
        mListView = (ListView) view.findViewById(R.id.listView);
        TextView msgTextView = (TextView) view.findViewById(R.id.textView);
        msgTextView.setText(R.string.blu_sub_item_no_bluetooth);

        mHasBluetooth = hasBluetoothFeature();

        populateListView(msgTextView);

        return view;

    } // end method onCreateView

    private void populateListView(TextView msgTextView) {

        if (mHasBluetooth) {
            mBluetoothObservable = new BluetoothObservable(getActivity());
        } else {
            mListView.setEmptyView(msgTextView);
            mListView = null;
        }

    }

    @Override
    public void onResume() {

        super.onResume();

        if (mHasBluetooth) {
            mBluetoothObservable.addObserver(this);
            mBluetoothObservable.enableBluetoothUpdates();
            updateListView();
        }

    }

    @Override
    public void onPause() {

        super.onPause();

        if (mHasBluetooth) {
            mBluetoothObservable.disableBluetoothUpdates();
            mBluetoothObservable.deleteObserver(this);
        }

    }

    private ArrayList<ListItem> getListItems() {

        ArrayList<ListItem> items = new ArrayList<>();

        items.add(new OrdinaryListItem(getString(R.string.item_status),
                mBluetoothObservable.getStatus()));
        items.add(new OrdinaryListItem(getString(R.string.blu_item_address),
                mBluetoothObservable.getAddress()));
        items.add(new OrdinaryListItem(getString(R.string.blu_item_name),
                mBluetoothObservable.getName()));
        items.add(new OrdinaryListItem(
                getString(R.string.blu_item_paired_devices),
                mBluetoothObservable.getPairedDevices()));

        return items;

    }

    // Checks whether the device supports Bluetooth or not.
    private boolean hasBluetoothFeature() {

        BluetoothAdapter bluetoothAdapter = getBluetoothAdapter();
        return (bluetoothAdapter != null);

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private BluetoothAdapter getBluetoothAdapter() {

        BluetoothAdapter bluetoothAdapter;

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        } else {
            bluetoothAdapter = ((BluetoothManager) getActivity().
                    getSystemService(Context.BLUETOOTH_SERVICE)).
                    getAdapter();
        }

        return bluetoothAdapter;

    }


    private void updateListView() {
        mListView.setAdapter(new CustomArrayAdapter(getActivity(),
                getListItems()));
    }

    @Override
    public void update(Observable observable, Object o) {
        updateListView();
    }

} // end class BluetoothFragment
