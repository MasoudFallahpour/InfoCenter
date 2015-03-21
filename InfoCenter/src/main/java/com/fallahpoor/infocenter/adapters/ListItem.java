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

/**
 * This interface is implemented by all types of list items.
 *
 * @author Masood Fallahpoor
 */
public interface ListItem {

    int getViewType();

    View getView(LayoutInflater inflater, View convertView, ViewGroup parent);

}
