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

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.fallahpoor.infocenter.R
import com.fallahpoor.infocenter.fragments.ComponentsFragment
import com.fallahpoor.infocenter.fragments.FragmentFactory
import com.fallahpoor.infocenter.fragments.FragmentFactory.FragmentType
import io.github.inflationx.viewpump.ViewPumpContextWrapper

class MainActivity : AppCompatActivity(), ComponentsFragment.ComponentsListener {

    private val CURRENT_FRAGMENT = "current_fragment"
    private var currentFragment = FragmentType.UNKNOWN
    private var isDualPane: Boolean = false

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    public override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*
         * When detailsContainer is present in the layout, then app is
         * running in dual pane mode.
         */
        isDualPane = findViewById<View>(R.id.detailsContainer) != null

        if (savedInstanceState != null) {
            currentFragment = savedInstanceState.getInt(CURRENT_FRAGMENT)
            return
        }

        if (isDualPane) {
            displayFragment(FragmentType.GENERAL)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_rate_app -> startBazaarIntent()
            R.id.action_about_app -> {
                displayDetails(FragmentType.ABOUT)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }

        return super.onOptionsItemSelected(item)

    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(CURRENT_FRAGMENT, currentFragment)
    }

    override fun onComponentClick(position: Int) {
        displayDetails(position)
    }

    private fun displayDetails(fragmentType: Int) {
        if (isDualPane) {
            displayFragment(fragmentType)
        } else {
            displayDetailsActivity(fragmentType)
        }
    }

    private fun displayFragment(fragmentType: Int) {

        if (fragmentType == currentFragment) {
            return
        }

        currentFragment = fragmentType

        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.fragment_enter_from_left,
                R.anim.fragment_exit_to_right, R.anim.fragment_enter_from_right,
                R.anim.fragment_exit_to_left
            )
            .replace(
                R.id.detailsContainer,
                FragmentFactory.create(fragmentType)
            )
            .commit()

    }

    private fun displayDetailsActivity(fragmentType: Int) {
        val intent = Intent(this, DetailsActivity::class.java)
            .putExtra(FRAGMENT_TO_DISPLAY, fragmentType)
        startActivity(intent)
    }

    private fun startBazaarIntent() {

        val intent = Intent(Intent.ACTION_EDIT).apply {
            data = Uri.parse("bazaar://details?id=" + applicationContext.packageName)
            setPackage("com.farsitel.bazaar")
        }
        try {
            startActivity(intent)
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(this, R.string.bazaar_not_installed, Toast.LENGTH_SHORT).show()
        }

    }

    companion object {
        const val FRAGMENT_TO_DISPLAY = "fragment_to_display"
    }

} // end class MainActivity