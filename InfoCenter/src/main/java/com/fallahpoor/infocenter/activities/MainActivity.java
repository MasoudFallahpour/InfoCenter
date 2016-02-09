/*
    Copyright (C) 2014 Masood Fallahpoor

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

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.fallahpoor.infocenter.R;
import com.fallahpoor.infocenter.Utils;
import com.fallahpoor.infocenter.fragments.ComponentsFragment;
import com.fallahpoor.infocenter.fragments.FragmentFactory;
import com.fallahpoor.infocenter.fragments.FragmentFactory.FragmentType;

import de.cketti.library.changelog.ChangeLog;

/**
 * MainActivity is the main Activity of the app.
 *
 * @author Masood Fallahpoor
 */
public class MainActivity extends LocalizationActivity implements
        ComponentsFragment.ComponentsListener {

    public static final String FRAGMENT_TO_DISPLAY = "fragment_to_display";
    private final String CURRENT_FRAGMENT = "current_fragment";
    private int mCurrentFragment = FragmentType.UNKNOWN;
    private boolean mIsDualPane;
    private AlertDialog changelogDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        setDefaultLanguage(Utils.LANGUAGE_FA);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Utils.setLocale(getLanguage());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) !=
                    PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
            }
        }

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

        ChangeLog changeLog = new ChangeLog(this);
        /*
         * Show the changelog dialog if this is the first time
         * the app has been run after being upgraded.
        */
        if (changeLog.isFirstRun()) {
            changelogDialog = changeLog.getLogDialog();
            changelogDialog.show();
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
            case R.id.action_about_app:
                displayDetails(FragmentType.ABOUT);
                return true;
            case R.id.action_settings:
                displayDetails(FragmentType.SETTINGS);
                return true;
            case R.id.action_exit:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_FRAGMENT, mCurrentFragment);

    }

    @Override
    protected void onPause() {

        super.onPause();

        if (changelogDialog != null && changelogDialog.isShowing()) {
            changelogDialog.dismiss();
        }

    }

    @Override
    public void onComponentClick(int position) {
        displayDetails(position);
    }

    public void displayDetails(int fragmentType) {

        if (mIsDualPane) {
            displayFragment(fragmentType);
        } else {
            displayDetailsActivity(fragmentType);
        }

    }

    public void displayFragment(int fragmentType) {

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

} // end class MainActivity