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

package com.fallahpoor.infocenter.activities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.fallahpoor.infocenter.R;
import com.fallahpoor.infocenter.fragments.ComponentsFragment;
import com.fallahpoor.infocenter.fragments.FragmentFactory;
import com.fallahpoor.infocenter.fragments.FragmentFactory.FragmentType;

import de.cketti.library.changelog.ChangeLog;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity implements
        ComponentsFragment.ComponentsListener {

    public static final String FRAGMENT_TO_DISPLAY = "fragment_to_display";
    private final String CURRENT_FRAGMENT = "current_fragment";
    private int mCurrentFragment = FragmentType.UNKNOWN;
    private boolean mIsDualPane;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
         * When detailsContainer is present in the layout, then app is
         * running in dual pane mode.
         */
        mIsDualPane = (findViewById(R.id.detailsContainer) != null);

        if (savedInstanceState != null) {
            mCurrentFragment = savedInstanceState.
                    getInt(CURRENT_FRAGMENT);
            return;
        }

        if (mIsDualPane) {
            displayFragment(FragmentType.GENERAL);
        }

    } // end method onCreate

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_latest_changes:
                displayChangeLogDialog();
                return true;
            case R.id.action_rate_app:
                startBazaarIntent();
                break;
            case R.id.action_about_app:
                displayDetails(FragmentType.ABOUT);
                return true;
            case R.id.action_exit:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_FRAGMENT, mCurrentFragment);
    }

    @Override
    public void onComponentClick(int position) {
        displayDetails(position);
    }

    private void displayDetails(int fragmentType) {
        if (mIsDualPane) {
            displayFragment(fragmentType);
        } else {
            displayDetailsActivity(fragmentType);
        }
    }

    private void displayFragment(int fragmentType) {

        if (fragmentType == mCurrentFragment) {
            return;
        }

        mCurrentFragment = fragmentType;

        FragmentTransaction transaction = getSupportFragmentManager().
                beginTransaction();
        transaction.setCustomAnimations(R.anim.fragment_enter_from_left,
                R.anim.fragment_exit_to_right, R.anim.fragment_enter_from_right,
                R.anim.fragment_exit_to_left);
        transaction.replace(R.id.detailsContainer,
                FragmentFactory.create(fragmentType));
        transaction.commit();

    }

    private void displayDetailsActivity(int fragmentType) {
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra(FRAGMENT_TO_DISPLAY, fragmentType);
        startActivity(intent);
    }

    private void displayChangeLogDialog() {
        ChangeLog changeLog = new ChangeLog(this);
        changeLog.setDirection(ChangeLog.Direction.RTL);
        changeLog.getFullLogDialog().show();
    }

    private void startBazaarIntent() {

        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setData(Uri.parse("bazaar://details?id=" + getApplicationContext().getPackageName()));
        intent.setPackage("com.farsitel.bazaar");
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(this, R.string.bazaar_not_installed, Toast.LENGTH_SHORT).show();
        }

    }

} // end class MainActivity