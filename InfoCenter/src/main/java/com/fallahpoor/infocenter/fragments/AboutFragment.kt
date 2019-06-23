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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.fallahpoor.infocenter.BuildConfig
import com.fallahpoor.infocenter.R
import kotlinx.android.synthetic.main.fragment_about.*
import java.util.*

/**
 * AboutFragment displays some information about the app including
 * its version, description, programmer etc.
 */
class AboutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        super.onCreateView(inflater, container, savedInstanceState)

        val view = inflater.inflate(R.layout.fragment_about, container, false)

        appVersionTextView.text = String.format(
            Locale("fa", "IR"),
            getString(R.string.app_version_format),
            BuildConfig.VERSION_NAME
        )

        return view

    }

}
