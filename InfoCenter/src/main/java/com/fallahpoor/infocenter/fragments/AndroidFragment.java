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

import android.os.Build;
import android.os.Bundle;
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
import java.io.IOException;
import java.util.ArrayList;

/**
 * AndroidFragment displays some information about device's Android version.
 *
 * @author Masood Fallahpoor
 */
public class AndroidFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_others, container,
                false);

        ListView listView = (ListView) view.findViewById(R.id.listView);
        listView.setAdapter(new CustomArrayAdapter(getActivity(),
                getListItems()));

        return view;

    }

    private ArrayList<ListItem> getListItems() {

        ArrayList<ListItem> items = new ArrayList<>();

        items.add(new OrdinaryListItem(
                getString(R.string.and_item_version),
                Build.VERSION.RELEASE));
        items.add(new OrdinaryListItem(getString(R.string.and_item_name),
                getVersionName()));
        items.add(new OrdinaryListItem(getString(R.string.and_item_sdk_number),
                getSdkNumber()));
        items.add(new OrdinaryListItem(getString(R.string.and_item_kernel_version),
                getKernelInfo()));

        return items;

    }

    /*
     * Returns some information about the Android kernel including its version,
     * build date and so forth.
     */
    private String getKernelInfo() {

        BufferedReader buffReader;
        String kernelInfo;

        try {
            buffReader = new BufferedReader(new FileReader("/proc/version"));
            kernelInfo = buffReader.readLine();
            buffReader.close();
        } catch (IOException ex) {
            kernelInfo = getString(R.string.unknown);
        }

        return kernelInfo;

    }

    private String getSdkNumber() {
        return String.format(Utils.getLocale(), "%d", Build.VERSION.SDK_INT);
    }

    private String getVersionName() {

        int sdkNumber = Build.VERSION.SDK_INT;

        switch (sdkNumber) {
            case Build.VERSION_CODES.CUPCAKE:
                return getString(R.string.and_sub_item_cupcake);
            case Build.VERSION_CODES.DONUT:
                return getString(R.string.and_sub_item_donut);
            case Build.VERSION_CODES.ECLAIR:
            case Build.VERSION_CODES.ECLAIR_0_1:
            case Build.VERSION_CODES.ECLAIR_MR1:
                return getString(R.string.and_sub_item_eclair);
            case Build.VERSION_CODES.FROYO:
                return getString(R.string.and_sub_item_froyo);
            case Build.VERSION_CODES.GINGERBREAD:
            case Build.VERSION_CODES.GINGERBREAD_MR1:
                return getString(R.string.and_sub_item_gb);
            case Build.VERSION_CODES.HONEYCOMB:
            case Build.VERSION_CODES.HONEYCOMB_MR1:
            case Build.VERSION_CODES.HONEYCOMB_MR2:
                return getString(R.string.and_sub_item_hc);
            case Build.VERSION_CODES.ICE_CREAM_SANDWICH:
            case Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1:
                return getString(R.string.and_sub_item_ics);
            case Build.VERSION_CODES.JELLY_BEAN:
            case Build.VERSION_CODES.JELLY_BEAN_MR1:
            case Build.VERSION_CODES.JELLY_BEAN_MR2:
                return getString(R.string.and_sub_item_jb);
            case Build.VERSION_CODES.KITKAT:
                return getString(R.string.and_sub_item_kk);
            case Build.VERSION_CODES.LOLLIPOP:
                return getString(R.string.and_sub_item_lollipop);
            default:
                return getString(R.string.unknown);
        }

    } // end method getVersionName

} // end class AndroidFragment