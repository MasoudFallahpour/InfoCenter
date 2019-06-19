/*
    Copyright (C) 2014-2018 Masood Fallahpoor

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

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * GpsFragment displays the on/off status of device's GPS and the
 * location of the device if the GPS in on.
 *
 * @author Masood Fallahpoor
 */
public class GpsFragment extends Fragment implements Observer {

    private static final int REQUEST_CODE_ACCESS_FINE_LOCATION = 1003;
    @BindView(R.id.listView)
    ListView listView;
    @BindView(R.id.textView)
    TextView messageTextView;
    private GpsObservable gpsObservable;
    private Unbinder unbinder;
    private boolean hasGpsFeature;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_others, container,
                false);
        unbinder = ButterKnife.bind(this, view);

        messageTextView.setText(R.string.gps_sub_item_no_gps);
        hasGpsFeature = hasGpsFeature();
        gpsObservable = new GpsObservable(getActivity());

        if (isLocationPermissionGranted()) {
            populateListView();
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_ACCESS_FINE_LOCATION);
        }

        return view;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateListView();
            } else {
                listView.setEmptyView(messageTextView);
            }
        }
    }

    private boolean isLocationPermissionGranted() {
        return ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void populateListView() {
        if (hasGpsFeature) {
            updateListView();
        } else {
            listView.setEmptyView(messageTextView);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (hasGpsFeature && isLocationPermissionGranted()) {
            gpsObservable.addObserver(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (hasGpsFeature) {
            gpsObservable.deleteObserver(this);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private ArrayList<ListItem> getListItems() {

        ArrayList<ListItem> listItems = new ArrayList<>();

        listItems.add(new OrdinaryListItem(getString(R.string.item_status),
                gpsObservable.getStatus()));
        listItems.add(new OrdinaryListItem(getString(R.string.gps_item_location),
                gpsObservable.getLocation()));

        return listItems;

    }

    private void updateListView() {
        listView.setAdapter(new CustomArrayAdapter(getActivity(), getListItems()));
    }

    @Override
    public void update(Observable GpsObservable, Object o) {
        updateListView();
    }

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

    }

}