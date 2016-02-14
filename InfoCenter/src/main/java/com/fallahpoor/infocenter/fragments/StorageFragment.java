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

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.fallahpoor.infocenter.R;
import com.fallahpoor.infocenter.Utils;
import com.fallahpoor.infocenter.adapters.CustomArrayAdapter;
import com.fallahpoor.infocenter.adapters.HeaderListItem;
import com.fallahpoor.infocenter.adapters.ListItem;
import com.fallahpoor.infocenter.adapters.OrdinaryListItem;

import java.io.File;
import java.util.ArrayList;

/**
 * StorageFragment displays some information about the size of internal and
 * external storage of the device.
 *
 * @author Masood Fallahpoor
 */
public class StorageFragment extends Fragment {

    private boolean mIsApiAtLeast18;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_storage, container,
                false);

        mIsApiAtLeast18 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.
                JELLY_BEAN_MR2;

        ListView listView = (ListView) view.findViewById(R.id.listView);
        listView.setAdapter(new CustomArrayAdapter(getActivity(),
                getListItems()));

        return view;

    }

    private ArrayList<ListItem> getListItems() {

        ArrayList<ListItem> items = new ArrayList<>();
        String freeSpace;
        String totalSpace;

        totalSpace = getString(R.string.sto_item_total_space);
        freeSpace = getString(R.string.sto_item_free_space);

        items.add(new HeaderListItem(
                getString(R.string.sto_item_int_storage)));
        items.add(new OrdinaryListItem(totalSpace, getInternalTotal()));
        items.add(new OrdinaryListItem(freeSpace, getInternalFree()));
        items.add(new HeaderListItem(getString(R.string.
                sto_item_ext_storage)));
        items.add(new OrdinaryListItem(totalSpace,
                getExternalTotal()));
        items.add(new OrdinaryListItem(freeSpace,
                getExternalFree()));

        return items;

    }

    // Returns the total size of internal storage
    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private String getInternalTotal() {

        long size;
        StatFs internalStatFs = new StatFs(Environment.
                getDataDirectory().getAbsolutePath());

        if (mIsApiAtLeast18) {
            size = internalStatFs.getTotalBytes();
        } else {
            size = (long) internalStatFs.getBlockCount()
                    * (long) internalStatFs.getBlockSize();
        }

        return Utils.getFormattedSize(size);

    }

    // Returns the free size of internal storage
    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private String getInternalFree() {

        long size;
        StatFs internalStatFs = new StatFs(Environment.
                getDataDirectory().getAbsolutePath());

        if (mIsApiAtLeast18) {
            size = internalStatFs.getAvailableBytes();
        } else {
            size = (long) internalStatFs.getAvailableBlocks()
                    * (long) internalStatFs.getBlockSize();
        }

        return Utils.getFormattedSize(size);

    }

    // Returns the total size of external storage
    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private String getExternalTotal() {

        long size;
        StatFs extStorageStatFs;
        String extStoragePath = getExternalStoragePath();

        if (extStoragePath == null || !(new File(extStoragePath).exists())) {
            return getString(R.string.sto_sub_item_ext_storage_not_available);
        }

        extStorageStatFs = new StatFs(new File(extStoragePath).
                getAbsolutePath());

        if (mIsApiAtLeast18) {
            size = extStorageStatFs.getTotalBytes();
        } else {
            size = (long) extStorageStatFs.getBlockCount()
                    * (long) extStorageStatFs.getBlockSize();
        }

        return Utils.getFormattedSize(size);

    } // end method getExternalStorage

    // Returns the free size of external storage
    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private String getExternalFree() {

        long size;
        StatFs extStorageStatFs;
        String extStoragePath = getExternalStoragePath();

        if (extStoragePath == null || !(new File(extStoragePath).exists())) {
            return getString(R.string.sto_sub_item_ext_storage_not_available);
        }

        extStorageStatFs = new StatFs(new File(extStoragePath).
                getAbsolutePath());

        if (mIsApiAtLeast18) {
            size = extStorageStatFs.getAvailableBytes();
        } else {
            size = (long) extStorageStatFs.getAvailableBlocks()
                    * (long) extStorageStatFs.getBlockSize();
        }

        return Utils.getFormattedSize(size);

    }

    private String getExternalStoragePath() {

        if (!Environment.isExternalStorageEmulated()
                && Environment.getExternalStorageState().
                equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().
                    getAbsolutePath();
        } else {
            return System.getenv("SECONDARY_STORAGE");
        }

    }

} // end class StorageFragment