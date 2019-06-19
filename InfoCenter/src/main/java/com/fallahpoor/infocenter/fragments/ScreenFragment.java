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

import android.graphics.Point;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.fallahpoor.infocenter.R;
import com.fallahpoor.infocenter.Utils;
import com.fallahpoor.infocenter.adapters.CustomArrayAdapter;
import com.fallahpoor.infocenter.adapters.ListItem;
import com.fallahpoor.infocenter.adapters.OrdinaryListItem;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * ScreenFragment shows some information about the screen of the device
 * including its resolution, DPI and so on.
 *
 * @author Masood Fallahpoor
 */
public class ScreenFragment extends Fragment {

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

        return new ArrayList<>(Arrays.asList(getString(R.string.scr_item_resolution),
                getString(R.string.scr_item_orientation),
                getString(R.string.scr_item_refresh_rate),
                getString(R.string.scr_item_dots_per_inch),
                getString(R.string.scr_item_horizontal_dpi),
                getString(R.string.scr_item_vertical_dpi)));

    }

    private ArrayList<String> getSubItemsArrayList() {

        ArrayList<String> subItems = new ArrayList<>();
        Point displaySize = new Point();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        Display display = getActivity().getWindowManager().
                getDefaultDisplay();

        display.getSize(displaySize);
        display.getMetrics(displayMetrics);

        subItems.add(getDisplaySize(displaySize));
        subItems.add(getOrientation(displaySize));
        subItems.add(getRefreshRate(display));
        subItems.add(getDpi(displayMetrics));
        subItems.add(getHorizontalDpi(displayMetrics));
        subItems.add(getVerticalDpi(displayMetrics));

        return subItems;

    }

    private String getDisplaySize(Point displaySize) {

        return String.format(mUtils.getLocale(), "%d x %d", displaySize.x,
                displaySize.y);

    }

    private String getOrientation(Point displaySize) {

        if (displaySize.x == displaySize.y) {
            return getString(R.string.scr_sub_item_square);
        } else if (displaySize.x < displaySize.y) {
            return getString(R.string.scr_sub_item_portrait);
        } else {
            return getString(R.string.scr_sub_item_landscape);
        }

    }

    private String getRefreshRate(Display display) {

        return String.format(mUtils.getLocale(), "%.0f %s", display.
                getRefreshRate(), getString(R.string.scr_sub_item_hertz));

    }

    private String getDpi(DisplayMetrics displayMetrics) {

        return String.format(mUtils.getLocale(), "%d", displayMetrics.
                densityDpi);

    }

    private String getHorizontalDpi(DisplayMetrics displayMetrics) {
        return String.format(mUtils.getLocale(), "%.0f", displayMetrics.xdpi);
    }

    private String getVerticalDpi(DisplayMetrics displayMetrics) {
        return String.format(mUtils.getLocale(), "%.0f", displayMetrics.ydpi);
    }

} // end class ScreenFragment