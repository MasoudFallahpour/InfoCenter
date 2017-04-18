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

package com.fallahpoor.infocenter.fragments.gps;

import android.content.Context;
import android.location.LocationManager;
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
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

/**
 * GpsFragment displays the on/off status of device's GPS and the
 * location of the device if the GPS in on.
 *
 * @author Masood Fallahpoor
 */
public class GpsFragment extends Fragment implements Observer {

    private GpsObservable mGpsObservable;
    private ListView mListView;
    private boolean mHasGpsFeature;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_others, container,
                false);
        mListView = (ListView) view.findViewById(R.id.listView);
        TextView msgTextView = (TextView) view.findViewById(R.id.textView);
        msgTextView.setText(R.string.gps_sub_item_no_gps);

        mHasGpsFeature = hasGpsFeature();

        populateListView(msgTextView);

        return view;

    }

    private void populateListView(TextView msgTextView) {

        if (mHasGpsFeature) {
            mGpsObservable = new GpsObservable(getActivity());
            updateListView();
        } else {
            mListView.setEmptyView(msgTextView);
            mListView = null;
        }

    }

    @Override
    public void onResume() {

        super.onResume();

        if (mHasGpsFeature) {
            mGpsObservable.addObserver(this);
        }

    }

    @Override
    public void onPause() {

        super.onPause();

        if (mHasGpsFeature) {
            mGpsObservable.deleteObserver(this);
        }

    }

    private ArrayList<ListItem> getListItems() {

        ArrayList<ListItem> listItems = new ArrayList<>();

        listItems.add(new OrdinaryListItem(getString(R.string.item_status),
                mGpsObservable.getStatus()));
        listItems.add(new OrdinaryListItem(getString(R.string.gps_item_location),
                mGpsObservable.getLocation()));

        return listItems;

    }

    private void updateListView() {
        mListView.setAdapter(new CustomArrayAdapter(getActivity(),
                getListItems()));
    }

    @Override
    public void update(Observable GpsObservable, Object o) {
        updateListView();
    }

    // Checks if the device has GPS or not.
    private boolean hasGpsFeature() {

        LocationManager locationManager = (LocationManager) getActivity().
                getSystemService(Context.LOCATION_SERVICE);

        if (locationManager == null) {
            return false;
        }

        Iterator<String> iterator = locationManager.getAllProviders().
                iterator();
        String provider;

        while (iterator.hasNext()) {
            provider = iterator.next();
            if (provider.equals(LocationManager.GPS_PROVIDER)) {
                return true;
            }
        }

        return false;

    } // end method hasGpsFeature

} // end class GpsFragment