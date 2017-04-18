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

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fallahpoor.infocenter.R;

/**
 * AboutFragment displays some information about the app including
 * its version, description, programmer etc.
 *
 * @author Masood Fallahpoor
 */
public class AboutFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_about, container, false);

        TextView appVersionTextView = (TextView) view.findViewById(R.id.appVersionTextView);

        try {
            PackageInfo packageInfo = getActivity().getPackageManager().getPackageInfo(
                    getActivity().getPackageName(), 0);

            appVersionTextView.setText(getString(R.string.app_version_format,
                    packageInfo.versionName));

        } catch (NameNotFoundException e) {
            appVersionTextView.setText(getString(R.string.app_version_format,
                    "1.0"));
        }

        return view;

    }

}
