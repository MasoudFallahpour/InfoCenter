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

package com.fallahpoor.infocenter.fragments

import androidx.fragment.app.Fragment

import com.fallahpoor.infocenter.fragments.battery.BatteryFragment
import com.fallahpoor.infocenter.fragments.bluetooth.BluetoothFragment
import com.fallahpoor.infocenter.fragments.gps.GpsFragment
import com.fallahpoor.infocenter.fragments.wifi.WifiFragment

/**
 * A factory class to create different Fragments.
 */
object FragmentFactory {

    fun create(fragmentType: Int): Fragment {

        return when (fragmentType) {
            FragmentType.GENERAL -> GeneralFragment()
            FragmentType.ANDROID -> AndroidFragment()
            FragmentType.CPU -> CpuFragment()
            FragmentType.SCREEN -> ScreenFragment()
            FragmentType.RAM -> RamFragment()
            FragmentType.STORAGE -> StorageFragment()
            FragmentType.CAMERA -> CameraFragment()
            FragmentType.SENSORS -> SensorsFragment()
            FragmentType.BATTERY -> BatteryFragment()
            FragmentType.WIFI -> WifiFragment()
            FragmentType.GPU -> GpuFragment()
            FragmentType.BLUETOOTH -> BluetoothFragment()
            FragmentType.GPS -> GpsFragment()
            FragmentType.SIM -> SimFragment()
            FragmentType.ABOUT -> AboutFragment()
            else -> GeneralFragment()
        }

    }

    object FragmentType {

        const val UNKNOWN = -1
        const val GENERAL = 0
        internal const val ANDROID = 1
        internal const val CPU = 2
        internal const val SCREEN = 3
        internal const val RAM = 4
        internal const val STORAGE = 5
        internal const val CAMERA = 6
        internal const val SENSORS = 7
        internal const val BATTERY = 8
        internal const val WIFI = 9
        internal const val GPU = 10
        internal const val BLUETOOTH = 11
        internal const val GPS = 12
        internal const val SIM = 13
        const val ABOUT = 14

    }

} // end class FragmentFactory
