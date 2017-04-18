/*
    Copyright (C) 2014-2016 Masood Fallahpoor

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

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.akexorcist.localizationactivity.LocalizationActivity;
import com.fallahpoor.infocenter.R;
import com.fallahpoor.infocenter.fragments.FragmentFactory;
import com.fallahpoor.infocenter.fragments.FragmentFactory.FragmentType;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * DetailsActivity displays a Fragment based on the type that is passed
 * to it from MainActivity.
 *
 * @author Masood Fallahpoor
 */
public class DetailsActivity extends LocalizationActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        int fragmentType = getIntent().getIntExtra(MainActivity.
                FRAGMENT_TO_DISPLAY, FragmentType.GENERAL);
        String[] titles = getResources().getStringArray(R.array.
                activity_titles);

        setTitle(titles[fragmentType]);

        // if the Activity is being restored, no need to recreate the GUI
        if (savedInstanceState == null) {
            displayFragment(fragmentType);
        }

    }

    private void displayFragment(int fragmentType) {

        FragmentTransaction transaction = getSupportFragmentManager().
                beginTransaction();
        transaction.add(R.id.detailsContainer,
                FragmentFactory.create(fragmentType));
        transaction.commit();

    }

} // end class DetailsActivity