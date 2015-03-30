/*
    Copyright (C) 2014 Masood Fallahpoor

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
import android.widget.TextView;

import com.fallahpoor.infocenter.R;
import com.fallahpoor.infocenter.adapters.CustomArrayAdapter.ItemType;

/**
 * This class is responsible for inflating an ordinary list item.
 *
 * @author Masood Fallahpoor
 */
public class OrdinaryListItem implements ListItem {

    private String mItemText;
    private String mSubItemText;

    public OrdinaryListItem(String itemText, String subItemText) {

        mItemText = itemText;
        mSubItemText = subItemText;

    }

    public void setSubItemText(String subItemText) {
        mSubItemText = subItemText;
    }

    @Override
    public int getViewType() {
        return ItemType.ORDINARY_ITEM.ordinal();
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView,
                        ViewGroup parent) {

        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.ordinary_list_item, parent,
                    false);

            viewHolder = new ViewHolder();
            viewHolder.itemTextView = (TextView) convertView.findViewById(
                    R.id.itemTextView);
            viewHolder.subItemTextView = (TextView) convertView.findViewById(
                    R.id.subItemTextView);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.itemTextView.setText(mItemText);
        viewHolder.subItemTextView.setText(mSubItemText);

        return convertView;

    }

    private static class ViewHolder {

        public TextView itemTextView;
        public TextView subItemTextView;

    }

} // end class OrdinaryListItem