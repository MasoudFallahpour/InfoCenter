/*
    Copyright (C) 2014-2018 Masood Fallahpoor

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

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fallahpoor.infocenter.R;
import com.fallahpoor.infocenter.Utils;
import com.fallahpoor.infocenter.adapters.CustomArrayAdapter;
import com.fallahpoor.infocenter.adapters.HeaderListItem;
import com.fallahpoor.infocenter.adapters.ListItem;
import com.fallahpoor.infocenter.adapters.OrdinaryListItem;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.halfbit.pinnedsection.PinnedSectionListView;

/**
 * StorageFragment displays some information about the size of internal and
 * external storage of the device.
 *
 * @author Masood Fallahpoor
 */
public class StorageFragment extends Fragment {

    private static final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 1004;
    @BindView(R.id.listView)
    PinnedSectionListView listView;
    @BindView(R.id.textView)
    TextView textView;
    private Unbinder unbinder;
    private Utils utils;
    private boolean isApiAtLeast18;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_storage, container, false);
        unbinder = ButterKnife.bind(this, view);

        utils = new Utils(getActivity());
        isApiAtLeast18 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
        listView.setShadowVisible(false);
        textView.setText(R.string.sto_sub_item_ext_storage_not_available);

        if (ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            populateListView();
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_READ_EXTERNAL_STORAGE);
        }

        return view;

    }

    private void populateListView() {
        listView.setAdapter(new CustomArrayAdapter(getActivity(), getListItems()));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateListView();
            } else {
                listView.setEmptyView(textView);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private ArrayList<ListItem> getListItems() {

        ArrayList<ListItem> items = new ArrayList<>();
        String freeSpace;
        String totalSpace;

        totalSpace = getString(R.string.sto_item_total_space);
        freeSpace = getString(R.string.sto_item_free_space);

        items.add(new HeaderListItem(getString(R.string.sto_item_int_storage)));
        items.add(new OrdinaryListItem(totalSpace, getInternalTotal()));
        items.add(new OrdinaryListItem(freeSpace, getInternalFree()));
        items.add(new HeaderListItem(getString(R.string.
                sto_item_ext_storage)));
        items.add(new OrdinaryListItem(totalSpace, getExternalTotal()));
        items.add(new OrdinaryListItem(freeSpace, getExternalFree()));

        return items;

    }

    // Returns the total size of internal storage
    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private String getInternalTotal() {

        long size;
        StatFs internalStatFs = new StatFs(Environment.getDataDirectory().getAbsolutePath());

        if (isApiAtLeast18) {
            size = internalStatFs.getTotalBytes();
        } else {
            size = (long) internalStatFs.getBlockCount()
                    * (long) internalStatFs.getBlockSize();
        }

        return utils.getFormattedSize(size);

    }

    // Returns the free size of internal storage
    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private String getInternalFree() {

        long size;
        StatFs internalStatFs = new StatFs(Environment.getDataDirectory().getAbsolutePath());

        if (isApiAtLeast18) {
            size = internalStatFs.getAvailableBytes();
        } else {
            size = (long) internalStatFs.getAvailableBlocks()
                    * (long) internalStatFs.getBlockSize();
        }

        return utils.getFormattedSize(size);

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

        extStorageStatFs = new StatFs(new File(extStoragePath).getAbsolutePath());

        if (isApiAtLeast18) {
            size = extStorageStatFs.getTotalBytes();
        } else {
            size = (long) extStorageStatFs.getBlockCount()
                    * (long) extStorageStatFs.getBlockSize();
        }

        return utils.getFormattedSize(size);

    }

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

        extStorageStatFs = new StatFs(new File(extStoragePath).getAbsolutePath());

        if (isApiAtLeast18) {
            size = extStorageStatFs.getAvailableBytes();
        } else {
            size = (long) extStorageStatFs.getAvailableBlocks()
                    * (long) extStorageStatFs.getBlockSize();
        }

        return utils.getFormattedSize(size);

    }

    private String getExternalStoragePath() {

        if (!Environment.isExternalStorageEmulated()
                && Environment.getExternalStorageState().
                equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            return System.getenv("SECONDARY_STORAGE");
        }

    }

}