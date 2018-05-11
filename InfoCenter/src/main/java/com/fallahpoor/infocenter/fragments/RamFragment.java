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

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.fallahpoor.infocenter.R;
import com.fallahpoor.infocenter.Utils;
import com.fallahpoor.infocenter.adapters.CustomArrayAdapter;
import com.fallahpoor.infocenter.adapters.ListItem;
import com.fallahpoor.infocenter.adapters.OrdinaryListItem;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * RamFragment displays the total and free RAM of the device.
 *
 * @author Masood Fallahpoor
 */
public class RamFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_others, container,
                false);

        ListView listView = view.findViewById(R.id.listView);
        listView.setAdapter(new CustomArrayAdapter(getActivity(),
                getListItems()));

        return view;

    }

    private ArrayList<ListItem> getListItems() {

        ArrayList<ListItem> items = new ArrayList<>();

        items.add(new OrdinaryListItem(
                getString(R.string.ram_item_total_ram),
                getTotalRam()));
        items.add(new OrdinaryListItem(
                getString(R.string.ram_item_free_ram),
                getFreeRam()));

        return items;

    }

    // Returns the total RAM of the device
    private String getTotalRam() {

        BufferedReader buffReader;
        String aLine;
        long lngTotalRam;
        String totalRam;

        try {
            buffReader = new BufferedReader(new FileReader(
                    "/proc/meminfo"));
            aLine = buffReader.readLine();
            buffReader.close();
            String[] tokens = aLine.split("\\s+");
            lngTotalRam = Long.parseLong(tokens[1]) * 1024;
            totalRam = Utils.getFormattedSize(lngTotalRam);

        } catch (Exception ex) {
            totalRam = getString(R.string.unknown);
        }

        return totalRam;

    } // end method getTotalRam

    // Returns the free RAM of the device
    private String getFreeRam() {

        MemoryInfo memoryInfo;
        ActivityManager activityManager;
        long lngFreeRam;
        String freeRam;

        activityManager = (ActivityManager) getActivity().
                getSystemService(Context.ACTIVITY_SERVICE);

        if (activityManager != null) {
            memoryInfo = new MemoryInfo();
            activityManager.getMemoryInfo(memoryInfo);
            lngFreeRam = memoryInfo.availMem;
            freeRam = Utils.getFormattedSize(lngFreeRam);
        } else {
            freeRam = getString(R.string.unknown);
        }

        return freeRam;

    } // end method getFreeRam

} // end class RamFragment