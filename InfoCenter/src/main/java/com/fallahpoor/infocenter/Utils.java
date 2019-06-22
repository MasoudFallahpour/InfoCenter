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

package com.fallahpoor.infocenter;

import android.content.Context;

import java.util.Locale;

/**
 * This class provides utility methods for other classes.
 *
 * @author Masood Fallahpoor
 */
public class Utils {

    private Locale locale;
    private Context context;

    public Utils(Context context) {
        this.context = context;
        locale = new Locale("fa", "IR");
    }

    public Locale getLocale() {
        return locale;
    }

    public static boolean isEmpty(String string) {
        return (string == null || string.trim().length() == 0);
    }

    // Converts and formats the given number of bytes to MB or GB
    public String getFormattedSize(long size) {

        if (size < 0) {
            return context.getString(R.string.unknown);
        }

        long mb = size / 1048576L;
        double gb;
        String MB = context.getString(R.string.sub_item_mb);
        String GB = context.getString(R.string.sub_item_gb);
        String fmtSize;

        if (mb < 1024) {
            fmtSize = String.format(locale, "%d", mb) + " " + MB;
        } else {
            gb = (double) mb / 1024;
            fmtSize = String.format(locale, "%.2f", gb) + " " + GB;
        }

        return fmtSize;

    } // end method getFormattedSize

    // Converts and formats the given frequency from Hz to MHz or GHz
    public String getFormattedFrequency(String frequency) {

        if (isEmpty(frequency)) {
            return context.getString(R.string.unknown);
        }

        double frequencyDbl = (double) Long.valueOf(frequency) / 1000;
        String MHz = context.getString(R.string.cpu_sub_item_mhz);
        String GHz = context.getString(R.string.cpu_sub_item_ghz);
        String fmtFrequency;

        if (frequencyDbl < 1000) {
            fmtFrequency = String.format(locale, "%.0f %s", frequencyDbl, MHz);
        } else {
            fmtFrequency = String.format(locale, "%.1f %s", frequencyDbl / 1000, GHz);
        }

        return fmtFrequency;

    }

} // end class Utils
