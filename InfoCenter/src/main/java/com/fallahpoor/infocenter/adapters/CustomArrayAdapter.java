/*
    Copyright (C) 2014-2016 Masood Fallahpoor

    This file is part of Info Center.

    Info Center is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Info Center is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Info Center.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.fallahpoor.infocenter.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import java.util.List;

import de.halfbit.pinnedsection.PinnedSectionListView;

/**
 * This custom ArrayAdapter is used to populate the ListViews of the app.
 *
 * @author Masood Fallahpoor
 */
public class CustomArrayAdapter extends ArrayAdapter<ListItem> implements
        PinnedSectionListView.PinnedSectionListAdapter {

    private LayoutInflater mInflater;

    public CustomArrayAdapter(Context context, List<ListItem> items) {

        super(context, 0, items);
        mInflater = LayoutInflater.from(context);

    }

    @Override
    public int getViewTypeCount() {
        return ItemType.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getViewType();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return getItem(position).getView(mInflater, convertView, parent);
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return viewType == ItemType.HEADER_ITEM.ordinal();
    }

    public enum ItemType {
        ORDINARY_ITEM, HEADER_ITEM, COMPONENT_ITEM
    }

} // end class CustomArrayAdapter