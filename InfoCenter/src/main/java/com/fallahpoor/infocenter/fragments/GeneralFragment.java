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

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
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
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * GeneralFragment displays some general information about the device including
 * its manufacturer, brand, model and so on.
 *
 * @author Masood Fallahpoor
 */
public class GeneralFragment extends Fragment {

    private final int UPTIME_UPDATE_INTERVAL = 1000;
    private CustomArrayAdapter customArrayAdapter;
    private Handler handler;
    private Runnable uptimeRunnable;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_others, container, false);

        customArrayAdapter = new CustomArrayAdapter(getActivity(), getListItems());

        ListView listView = view.findViewById(R.id.listView);
        listView.setAdapter(customArrayAdapter);

        handler = new Handler();

        uptimeRunnable = new Runnable() {
            @Override
            public void run() {
                updateUptime();
                handler.postDelayed(this, UPTIME_UPDATE_INTERVAL);
            }
        };

        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        handler.post(uptimeRunnable);
    }

    @Override
    public void onStop() {
        super.onStop();
        handler.removeCallbacks(uptimeRunnable);
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

        return new ArrayList<>(Arrays.asList(getString(R.string.gen_item_manufacturer),
                getString(R.string.gen_item_brand),
                getString(R.string.gen_item_model),
                getString(R.string.gen_item_bootloader_version),
                getString(R.string.gen_item_build_number),
                getString(R.string.gen_item_fingerprint),
                getString(R.string.gen_item_imei),
                getString(R.string.gen_item_serial_number),
                getString(R.string.gen_item_radio_firmware_version),
                getString(R.string.gen_item_uptime)));

    }

    private ArrayList<String> getSubItemsArrayList() {

        return new ArrayList<>(Arrays.asList(Build.MANUFACTURER,
                Build.BRAND,
                Build.DEVICE,
                Build.BOOTLOADER,
                Build.DISPLAY,
                Build.FINGERPRINT,
                getImei(),
                Build.SERIAL,
                getRadioFirmwareVersion(),
                getFormattedUptime()));

    }


    /*
     * Updates the 'Up Time' item and notifies the ListView of this Fragment to
     * display the updated value
     */
    private void updateUptime() {

        OrdinaryListItem upTimeItem;

        upTimeItem = (OrdinaryListItem) getUpTimeItem();
        upTimeItem.setSubItemText(getFormattedUptime());

        customArrayAdapter.notifyDataSetChanged();

    }

    private ListItem getUpTimeItem() {
        return customArrayAdapter.getItem(customArrayAdapter.getCount() - 1);
    }

    // Returns the IMEI/MEID of the device or "unknown" if it's not available.
    private String getImei() {

        TelephonyManager telMgr = (TelephonyManager) getActivity().
                getSystemService(Context.TELEPHONY_SERVICE);
        String imei = getString(R.string.unknown);

        try {
            if (telMgr != null) {
                imei = telMgr.getDeviceId();
            }
        } catch (SecurityException exception) {
            /* App is running on a device with Android Marshmallow and user hasn't granted the
               READ_PHONE_STATE permission. */
        }

        return imei;

    }

    /*
     * Returns the device's radio firmware version or "unknown" if it's not
     * available.
     */
    private String getRadioFirmwareVersion() {

        String radioVersion = Build.getRadioVersion();

        if (TextUtils.isEmpty(radioVersion)) {
            radioVersion = getString(R.string.unknown);
        }

        return radioVersion;

    }

    // Returns the 'Up Time' of the device formatted in the form of hh:mm:ss.
    private String getFormattedUptime() {

        String uptimeStr;
        long uptimeLong = SystemClock.elapsedRealtime();
        Utils utils = new Utils(getActivity());

        uptimeStr = String.format(utils.getLocale(), "%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(uptimeLong),
                TimeUnit.MILLISECONDS.toMinutes(uptimeLong)
                        - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
                        .toHours(uptimeLong)),
                TimeUnit.MILLISECONDS.toSeconds(uptimeLong)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                        .toMinutes(uptimeLong)));

        return uptimeStr;

    }

} // end class GeneralFragment