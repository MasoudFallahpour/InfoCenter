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

package com.fallahpoor.infocenter.fragments;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
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
import java.util.Iterator;
import java.util.List;

/**
 * SensorsFragment displays a list of sensors and determines whether the device
 * supports each sensor or not.
 *
 * @author Masood Fallahpoor
 */
public class SensorsFragment extends Fragment {

    private boolean mIsApiAtLeast18;
    private boolean mIsApiAtLeast19;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_others, container,
                false);

        mIsApiAtLeast18 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.
                JELLY_BEAN_MR2;
        mIsApiAtLeast19 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.
                KITKAT;

        ListView listView = view.findViewById(R.id.listView);
        listView.setAdapter(new CustomArrayAdapter(getActivity(),
                getListItems()));

        return view;

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

        ArrayList<String> items = new ArrayList<>(Arrays.asList(
                getString(R.string.sen_item_accelerometer),
                getString(R.string.sen_item_ambient_temperature),
                getString(R.string.sen_item_gravity),
                getString(R.string.sen_item_gyroscope),
                getString(R.string.sen_item_light),
                getString(R.string.sen_item_linear_acceleration),
                getString(R.string.sen_item_magnetic_field),
                getString(R.string.sen_item_pressure),
                getString(R.string.sen_item_proximity),
                getString(R.string.sen_item_relative_humidity),
                getString(R.string.sen_item_rotation_vector)));

        if (mIsApiAtLeast18) {
            items.add(getString(R.string.sen_item_game_rotation_vector));
            items.add(getString(R.string.sen_item_significant_motion));
        }

        if (mIsApiAtLeast19) {
            items.add(getString(R.string.sen_item_geomagnetic_rotation_vector));
            items.add(getString(R.string.sen_item_step_counter));
            items.add(getString(R.string.sen_item_step_detector));
        }

        return items;

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private ArrayList<String> getSubItemsArrayList() {

        SparseArray<String> sensorsArray = new SparseArray<>();
        ArrayList<String> subItems = new ArrayList<>();
        Iterator<Integer> iterator;
        String supported = getString(R.string.supported);
        String unsupported = getString(R.string.unsupported);
        ArrayList<Integer> sensorTypes = new ArrayList<>(
                Arrays.asList(Sensor.TYPE_ACCELEROMETER,
                        Sensor.TYPE_AMBIENT_TEMPERATURE,
                        Sensor.TYPE_GRAVITY, Sensor.TYPE_GYROSCOPE,
                        Sensor.TYPE_LIGHT, Sensor.TYPE_LINEAR_ACCELERATION,
                        Sensor.TYPE_MAGNETIC_FIELD, Sensor.TYPE_PRESSURE,
                        Sensor.TYPE_PROXIMITY, Sensor.TYPE_RELATIVE_HUMIDITY,
                        Sensor.TYPE_ROTATION_VECTOR));

        if (mIsApiAtLeast18) {
            sensorTypes.add(Sensor.TYPE_GAME_ROTATION_VECTOR);
            sensorTypes.add(Sensor.TYPE_SIGNIFICANT_MOTION);
        }

        if (mIsApiAtLeast19) {
            sensorTypes.add(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);
            sensorTypes.add(Sensor.TYPE_STEP_COUNTER);
            sensorTypes.add(Sensor.TYPE_STEP_DETECTOR);
        }

        SensorManager sensorManager = (SensorManager) getActivity().
                getSystemService(Context.SENSOR_SERVICE);

        // Get the list of all available sensors of the device
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

        iterator = sensorTypes.iterator();

        // Assume all sensor types are unsupported
        while (iterator.hasNext()) {
            sensorsArray.put(iterator.next(), unsupported);
        }

        /*
         * For each sensor type that is in sensors change its status from
         * "unsupported" to "supported".
         */
        for (Sensor sensor : sensors) {
            sensorsArray.put(sensor.getType(), supported);
        }

        iterator = sensorTypes.iterator();

        while (iterator.hasNext()) {
            subItems.add(sensorsArray.get(iterator.next()));
        }

        return subItems;

    } // end method getSubItemsArrayList

} // end class SensorsFragment