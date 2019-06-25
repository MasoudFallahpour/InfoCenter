/*
    Copyright (C) 2014-2018 Masood Fallahpoor

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

package com.fallahpoor.infocenter.activities

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.onNavDestinationSelected
import com.fallahpoor.infocenter.R
import com.fallahpoor.infocenter.fragments.ComponentsFragment
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ComponentsFragment.ItemClickListener {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        NavigationUI.setupActionBarWithNavController(this, navHostFragment.findNavController())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navController = navHostFragment.findNavController()
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = navHostFragment.findNavController()
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        return (navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp())
    }

    override fun onItemClick(position: Int) {
        navigateToFragment(position)
    }

    private fun navigateToFragment(fragmentType: Int) {

        val actionId: Int = when (fragmentType) {
            0 -> R.id.action_componentsFragment_to_generalFragment
            1 -> R.id.action_componentsFragment_to_androidFragment
            2 -> R.id.action_componentsFragment_to_cpuFragment
            3 -> R.id.action_componentsFragment_to_screenFragment
            4 -> R.id.action_componentsFragment_to_ramFragment
            5 -> R.id.action_componentsFragment_to_ramFragment
            6 -> R.id.action_componentsFragment_to_cameraFragment
            7 -> R.id.action_componentsFragment_to_sensorsFragment
            8 -> R.id.action_componentsFragment_to_batteryFragment
            9 -> R.id.action_componentsFragment_to_wifiFragment
            10 -> R.id.action_componentsFragment_to_gpuFragment
            11 -> R.id.action_componentsFragment_to_bluetoothFragment
            12 -> R.id.action_componentsFragment_to_gpsFragment
            13 -> R.id.action_componentsFragment_to_simFragment
            else -> R.id.action_componentsFragment_to_generalFragment
        }

        navHostFragment.findNavController().navigate(actionId)

    }

}