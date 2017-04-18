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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fallahpoor.infocenter.R;
import com.fallahpoor.infocenter.adapters.CustomArrayAdapter.ItemType;

/**
 * This class is responsible for inflating items of ComponentsFragment's ListView.
 *
 * @author Masood Fallahpoor
 */
public class ComponentListItem implements ListItem {

    private String mItemText;
    private int mItemImageId;

    public ComponentListItem(String itemText, int itemImageId) {

        mItemText = itemText;
        mItemImageId = itemImageId;

    }

    @Override
    public int getViewType() {
        return ItemType.COMPONENT_ITEM.ordinal();
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView,
                        ViewGroup parent) {

        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.component_list_item, parent,
                    false);

            viewHolder = new ViewHolder();
            viewHolder.componentTextView = (TextView) convertView.findViewById(
                    R.id.componentTextView);
            viewHolder.componentImageView = (ImageView) convertView.findViewById(
                    R.id.componentImageView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.componentTextView.setText(mItemText);
        viewHolder.componentImageView.setImageResource(mItemImageId);

        return convertView;

    }

    private static class ViewHolder {

        TextView componentTextView;
        ImageView componentImageView;

    }

} // end class ComponentListItem