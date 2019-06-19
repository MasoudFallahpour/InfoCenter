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

import androidx.fragment.app.Fragment;

import com.fallahpoor.infocenter.fragments.battery.BatteryFragment;
import com.fallahpoor.infocenter.fragments.bluetooth.BluetoothFragment;
import com.fallahpoor.infocenter.fragments.gps.GpsFragment;
import com.fallahpoor.infocenter.fragments.wifi.WifiFragment;

/**
 * A factory class to create different Fragments.
 *
 * @author Masood Fallahpoor
 */
public class FragmentFactory {

    public static Fragment create(int fragmentType) {

        switch (fragmentType) {
            case FragmentType.GENERAL:
                return new GeneralFragment();
            case FragmentType.ANDROID:
                return new AndroidFragment();
            case FragmentType.CPU:
                return new CpuFragment();
            case FragmentType.SCREEN:
                return new ScreenFragment();
            case FragmentType.RAM:
                return new RamFragment();
            case FragmentType.STORAGE:
                return new StorageFragment();
            case FragmentType.CAMERA:
                return new CameraFragment();
            case FragmentType.SENSORS:
                return new SensorsFragment();
            case FragmentType.BATTERY:
                return new BatteryFragment();
            case FragmentType.WIFI:
                return new WifiFragment();
            case FragmentType.GPU:
                return new GpuFragment();
            case FragmentType.BLUETOOTH:
                return new BluetoothFragment();
            case FragmentType.GPS:
                return new GpsFragment();
            case FragmentType.SIM:
                return new SimFragment();
            case FragmentType.ABOUT:
                return new AboutFragment();
            default:
                return new GeneralFragment();
        } // end switch

    } // end method create

    public static class FragmentType {

        public final static int UNKNOWN = -1;
        public final static int GENERAL = 0;
        final static int ANDROID = 1;
        final static int CPU = 2;
        final static int SCREEN = 3;
        final static int RAM = 4;
        final static int STORAGE = 5;
        final static int CAMERA = 6;
        final static int SENSORS = 7;
        final static int BATTERY = 8;
        final static int WIFI = 9;
        final static int GPU = 10;
        final static int BLUETOOTH = 11;
        final static int GPS = 12;
        final static int SIM = 13;
        public final static int ABOUT = 14;

    }

} // end class FragmentFactory
