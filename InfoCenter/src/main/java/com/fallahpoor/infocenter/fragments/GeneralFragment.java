/*
    Copyright (C) 2014 Masood Fallahpoor

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
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
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

/**
 * GeneralFragment displays some general information about the device including
 * its manufacturer, brand, model and so on.
 *
 * @author Masood Fallahpoor
 */
public class GeneralFragment extends Fragment implements Handler.Callback {

    private final int UPTIME_UPDATE_INTERVAL = 1000;
    private final int UPDATE_UPTIME_MSG = 0;
    private boolean mUpdateUptime;
    private Handler mHandler;
    private CustomArrayAdapter mArrayAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_others, container,
                false);

        mHandler = new Handler(getActivity().getMainLooper(), this);

        mArrayAdapter = new CustomArrayAdapter(getActivity(),
                getListItems());

        ListView listView = (ListView) view.findViewById(R.id.listView);
        listView.setAdapter(mArrayAdapter);

        return view;

    }

    @Override
    public void onResume() {

        super.onResume();
        mUpdateUptime = true;
        mHandler.sendEmptyMessage(UPDATE_UPTIME_MSG);

    }

    @Override
    public void onPause() {

        super.onPause();
        mUpdateUptime = false;
        mHandler.removeMessages(UPDATE_UPTIME_MSG);

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

        return new ArrayList<>(Arrays.asList(new String[]{
                getString(R.string.gen_item_manufacturer),
                getString(R.string.gen_item_brand),
                getString(R.string.gen_item_model),
                getString(R.string.gen_item_bootloader_version),
                getString(R.string.gen_item_build_number),
                getString(R.string.gen_item_fingerprint),
                getString(R.string.gen_item_imei),
                getString(R.string.gen_item_serial_number),
                getString(R.string.gen_item_radio_firmware_version),
                getString(R.string.gen_item_uptime),
        }));

    }

    private ArrayList<String> getSubItemsArrayList() {

        return new ArrayList<>(Arrays.asList(new String[]{
                Build.MANUFACTURER,
                Build.BRAND,
                Build.DEVICE,
                Build.BOOTLOADER,
                Build.DISPLAY,
                Build.FINGERPRINT,
                getImei(),
                Build.SERIAL,
                getRadioFirmwareVersion(),
                getFormattedUptime()
        }));

    }


    /*
     * Updates the 'Up Time' item and notifies the ListView of this Fragment to
     * display the updated value
     */
    private void updateUptime() {

        OrdinaryListItem upTimeItem;

        upTimeItem = (OrdinaryListItem) getUpTimeItem();
        upTimeItem.setSubItemText(getFormattedUptime());

        mArrayAdapter.notifyDataSetChanged();

        if (mUpdateUptime) {
            mHandler.sendEmptyMessageDelayed(UPDATE_UPTIME_MSG,
                    UPTIME_UPDATE_INTERVAL);
        }

    }

    private ListItem getUpTimeItem() {

        return mArrayAdapter
                .getItem(mArrayAdapter.getCount() - 1);

    }

    // Returns the IMEI/MEID of the device or "unknown" if it's not available.
    private String getImei() {

        TelephonyManager telMgr = (TelephonyManager) getActivity().
                getSystemService(Context.TELEPHONY_SERVICE);
        String imei = null;

        try {
            if (telMgr != null) {
                imei = telMgr.getDeviceId();
            }
        } catch (SecurityException exception) {
            /* App is running on a device with Android Marshmallow and user hasn't granted the
               READ_PHONE_STATE permission. */
        }

        if (Utils.isEmpty(imei)) {
            imei = getString(R.string.unknown);
        }

        return imei;

    }

    /*
     * Returns the device's radio firmware version or "unknown" if it's not
     * available.
     */
    private String getRadioFirmwareVersion() {

        String radioVersion = Build.getRadioVersion();

        if (Utils.isEmpty(radioVersion)) {
            radioVersion = getString(R.string.unknown);
        }

        return radioVersion;

    }

    // Returns the 'Up Time' of the device formatted in the form of hh:mm:ss.
    private String getFormattedUptime() {

        String uptimeStr;
        long uptimeLong = SystemClock.elapsedRealtime();

        uptimeStr = String.format(Utils.getLocale(), "%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(uptimeLong),
                TimeUnit.MILLISECONDS.toMinutes(uptimeLong)
                        - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
                        .toHours(uptimeLong)),
                TimeUnit.MILLISECONDS.toSeconds(uptimeLong)
                        - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                        .toMinutes(uptimeLong)));

        return uptimeStr;

    }

    @Override
    public boolean handleMessage(Message msg) {

        if (msg.what == UPDATE_UPTIME_MSG) {
            updateUptime();
        }

        return true;

    }

} // end class GeneralFragment