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

package com.fallahpoor.infocenter.fragments

import android.annotation.TargetApi
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.fallahpoor.infocenter.R
import com.fallahpoor.infocenter.adapters.CustomArrayAdapter
import com.fallahpoor.infocenter.adapters.ListItem
import com.fallahpoor.infocenter.adapters.OrdinaryListItem
import kotlinx.android.synthetic.main.fragment_others.*
import java.util.*

/**
 * SensorsFragment displays a list of sensors and determines whether the device
 * supports each sensor or not.
 */
class SensorsFragment : Fragment() {

    private var isApiAtLeast18: Boolean = false
    private var isApiAtLeast19: Boolean = false

    private val listItems: ArrayList<ListItem>
        get() {

            val listItems = ArrayList<ListItem>()
            val itemsArrayList = itemsArrayList
            val subItemsArrayList = subItemsArrayList

            for (i in itemsArrayList.indices) {
                listItems.add(OrdinaryListItem(itemsArrayList[i], subItemsArrayList[i]))
            }

            return listItems

        }

    private val itemsArrayList: ArrayList<String>
        get() {

            val items = ArrayList(
                listOf(
                    getString(R.string.sen_item_accelerometer),
                    getString(R.string.sen_item_ambient_temperature),
                    getString(R.string.sen_item_gravity),
                    getString(R.string.sen_item_gyroscope),
                    getString(R.string.sen_item_light),
                    getString(R.string.sen_item_linear_acceleration),
                    getString(R.string.sen_item_magnetic_field),
                    getString(R.string.sen_item_pressure),
                    getString(R.string.sen_item_proximity),
                    getString(R.string.sen_item_relative_humidity),
                    getString(R.string.sen_item_rotation_vector)
                )
            )

            if (isApiAtLeast18) {
                items.add(getString(R.string.sen_item_game_rotation_vector))
                items.add(getString(R.string.sen_item_significant_motion))
            }

            if (isApiAtLeast19) {
                items.add(getString(R.string.sen_item_geomagnetic_rotation_vector))
                items.add(getString(R.string.sen_item_step_counter))
                items.add(getString(R.string.sen_item_step_detector))
            }

            return items

        }

    private val subItemsArrayList: ArrayList<String>
        @TargetApi(Build.VERSION_CODES.KITKAT)
        get() {

            val sensorsArray = SparseArray<String>()
            val subItems = ArrayList<String>()
            var iterator: Iterator<Int>
            val supported = getString(R.string.supported)
            val unsupported = getString(R.string.unsupported)
            val sensorTypes = ArrayList(
                listOf(
                    Sensor.TYPE_ACCELEROMETER,
                    Sensor.TYPE_AMBIENT_TEMPERATURE,
                    Sensor.TYPE_GRAVITY,
                    Sensor.TYPE_GYROSCOPE,
                    Sensor.TYPE_LIGHT,
                    Sensor.TYPE_LINEAR_ACCELERATION,
                    Sensor.TYPE_MAGNETIC_FIELD,
                    Sensor.TYPE_PRESSURE,
                    Sensor.TYPE_PROXIMITY,
                    Sensor.TYPE_RELATIVE_HUMIDITY,
                    Sensor.TYPE_ROTATION_VECTOR
                )
            )

            if (isApiAtLeast18) {
                sensorTypes.add(Sensor.TYPE_GAME_ROTATION_VECTOR)
                sensorTypes.add(Sensor.TYPE_SIGNIFICANT_MOTION)
            }

            if (isApiAtLeast19) {
                sensorTypes.add(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR)
                sensorTypes.add(Sensor.TYPE_STEP_COUNTER)
                sensorTypes.add(Sensor.TYPE_STEP_DETECTOR)
            }

            val sensorManager = activity!!.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            val sensors = sensorManager.getSensorList(Sensor.TYPE_ALL)

            iterator = sensorTypes.iterator()
            while (iterator.hasNext()) {
                sensorsArray.put(iterator.next(), unsupported)
            }

            for (sensor in sensors) {
                sensorsArray.put(sensor.type, supported)
            }

            iterator = sensorTypes.iterator()

            while (iterator.hasNext()) {
                subItems.add(sensorsArray.get(iterator.next()))
            }

            return subItems

        } // end method getSubItemsArrayList

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        super.onCreateView(inflater, container, savedInstanceState)

        val view = inflater.inflate(R.layout.fragment_others, container, false)

        isApiAtLeast18 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2
        isApiAtLeast19 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        listView.adapter = CustomArrayAdapter(activity, listItems)

        return view

    }

}