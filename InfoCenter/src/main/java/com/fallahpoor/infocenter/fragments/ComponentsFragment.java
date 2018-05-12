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

import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.fallahpoor.infocenter.R;
import com.fallahpoor.infocenter.adapters.ComponentListItem;
import com.fallahpoor.infocenter.adapters.CustomArrayAdapter;
import com.fallahpoor.infocenter.adapters.ListItem;

import java.util.ArrayList;

/**
 * This Fragment displays a list of components. The components are: General,
 * Android, CPU, Screen, RAM, Storage, Camera, Sensors, Battery, Wi-Fi, GPU
 * Bluetooth, GPS and Sim card. A user can tap a component to see some
 * information about that component.
 *
 * @author Masood Fallahpoor
 */
public class ComponentsFragment extends Fragment {

    public interface ComponentsListener {

        /**
         * Called when user clicks on an item of the ListView of
         * ComponentsFragment
         *
         * @param position the position of the clicked item
         */
        void onComponentClick(int position);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_components, container,
                false);

        ListView listView = view.findViewById(R.id.listView);
        listView.setOnItemClickListener(new ComponentClickListener());
        listView.setAdapter(new CustomArrayAdapter(getActivity(), getListItems()));

        return view;

    }

    private ArrayList<ListItem> getListItems() {

        ArrayList<ListItem> items = new ArrayList<>();
        String[] listItems = getResources().getStringArray(R.array.components);
        TypedArray listImageIds = getResources().obtainTypedArray(R.array.components_icons);

        for (int i = 0; i < listItems.length; i++) {
            items.add(new ComponentListItem(listItems[i],
                    listImageIds.getResourceId(i, 0)));
        }

        listImageIds.recycle();

        return items;

    }

    private class ComponentClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ((ComponentsListener) getActivity()).onComponentClick(position);
        }

    }

} // end class ComponentsFragment