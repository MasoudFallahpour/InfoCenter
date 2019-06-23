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

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
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
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * CpuFragment displays some information about the CPU of the device including
 * its manufacturer, number of cores, features and so forth.
 *
 * @author Masood Fallahpoor
 */
public class CpuFragment extends Fragment {

    private final String MANUFACTURER = "Manufacturer";
    private final String HARDWARE = "Hardware";
    private final String PROCESSOR = "Processor";
    private final String CORES = "Cores";
    private final String FREQUENCY = "Frequency";
    private final String FEATURES = "Features";
    private Utils mUtils;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_others, container,
                false);

        mUtils = new Utils(getActivity());

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

        return new ArrayList<>(Arrays.asList(getString(R.string.cpu_item_manufacturer),
                getString(R.string.cpu_item_hardware),
                getString(R.string.cpu_item_processor),
                getString(R.string.cpu_item_number_of_cores),
                getString(R.string.cpu_item_frequency),
                getString(R.string.cpu_item_features)));

    }

    private ArrayList<String> getSubItemsArrayList() {

        ArrayList<String> subItems = new ArrayList<>();
        String unknown = getString(R.string.unknown);
        String value;
        HashMap<String, String> cpuInfo = getCpuInfoHashMap();
        String[] keys = {MANUFACTURER, HARDWARE, PROCESSOR,
                CORES, FREQUENCY, FEATURES};

        for (String key : keys) {
            value = cpuInfo.get(key);
            if (TextUtils.isEmpty(value)) {
                subItems.add(unknown);
            } else {
                subItems.add(value);
            }
        }

        return subItems;

    }

    private HashMap<String, String> getCpuInfoHashMap() {

        HashMap<String, String> cpuInfo = new HashMap<>();
        BufferedReader buffReader;
        String tokens[];
        String key;
        String value;

        cpuInfo.put(MANUFACTURER, getManufacturer());
        cpuInfo.put(FREQUENCY, String.format(mUtils.getLocale(),
                "%s: %s / %s: %s",
                getString(R.string.cpu_sub_item_min),
                getCpuFrequency(MinOrMax.MIN),
                getString(R.string.cpu_sub_item_max),
                getCpuFrequency(MinOrMax.MAX)));
        cpuInfo.put(CORES, getNumberOfCores());

        try {
            buffReader = new BufferedReader(new FileReader(
                    "/proc/cpuinfo"));
            String aLine;
            while ((aLine = buffReader.readLine()) != null) {
                if (!aLine.isEmpty()) {
                    value = "";
                    tokens = aLine.split(":");
                    key = tokens[0].trim();
                    if (tokens.length >= 2) {
                        value = tokens[1].trim();
                    }
                    cpuInfo.put(key, value);
                }
            }

            buffReader.close();
        } catch (IOException ignored) {
        }

        return cpuInfo;

    } // end method getCpuInfoHashMap

    // Returns the maximum or minimum frequency of the CPU in MHz or GHz
    private String getCpuFrequency(MinOrMax minOrMax) {

        BufferedReader buffReader;
        String frequency = null;
        File frequencyFile;

        if (minOrMax == MinOrMax.MAX) {
            frequencyFile = new File(
                    "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq");
        } else {
            frequencyFile = new File(
                    "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq");
        }

        try {
            buffReader = new BufferedReader(new FileReader(frequencyFile));
            frequency = buffReader.readLine();
            buffReader.close();
        } catch (Exception ignored) {
        }

        return mUtils.getFormattedFrequency(frequency);

    } // end method getCpuFrequency

    // Returns the manufacturer of the CPU
    private String getManufacturer() {

        String manufacturer;
        String hardware = Build.HARDWARE;

        switch (hardware.toLowerCase(Locale.US)) {
            case "qcom":
                manufacturer = getString(R.string.cpu_sub_item_qualcomm);
                break;
            default:
                manufacturer = hardware;
        }

        return manufacturer;

    }

    // Returns the number of cores
    private String getNumberOfCores() {

        String cores;

        try {
            File dir = new File("/sys/devices/system/cpu/");
            File[] files = dir.listFiles(new CpuFilter());
            cores = String.format(mUtils.getLocale(), "%d", files.length);

        } catch (Exception e) {
            cores = getString(R.string.unknown);
        }

        return cores;

    } // end method getNumberOfCores

    private enum MinOrMax {
        MIN, MAX
    }

    // Inner class to filter CPU devices in directory listing
    private class CpuFilter implements FileFilter {

        @Override
        public boolean accept(File pathname) {
            return Pattern.matches("cpu[0-9]+", pathname.getName());
        }

    }

} // end class CpuFragment