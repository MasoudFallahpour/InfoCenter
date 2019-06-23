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

package com.fallahpoor.infocenter

import android.content.Context
import android.text.TextUtils
import java.util.*

/**
 * This class provides utility methods for other classes.
 */
class Utils(private val context: Context) {

    val locale: Locale = Locale("fa", "IR")

    // Converts and formats the given number of bytes to MB or GB
    fun getFormattedSize(size: Long): String {

        if (size < 0) {
            return context.getString(R.string.unknown)
        }

        val mb = size / 1048576L
        val gb: Double
        val MB = context.getString(R.string.sub_item_mb)
        val GB = context.getString(R.string.sub_item_gb)
        val fmtSize: String

        if (mb < 1024) {
            fmtSize = String.format(locale, "%d", mb) + " " + MB
        } else {
            gb = mb.toDouble() / 1024
            fmtSize = String.format(locale, "%.2f", gb) + " " + GB
        }

        return fmtSize

    } // end method getFormattedSize

    // Converts and formats the given frequency from Hz to MHz or GHz
    fun getFormattedFrequency(frequency: String): String {

        if (TextUtils.isEmpty(frequency)) {
            return context.getString(R.string.unknown)
        }

        val frequencyDbl = java.lang.Long.valueOf(frequency).toDouble() / 1000
        val MHz = context.getString(R.string.cpu_sub_item_mhz)
        val GHz = context.getString(R.string.cpu_sub_item_ghz)
        val fmtFrequency: String

        fmtFrequency = if (frequencyDbl < 1000) {
            String.format(locale, "%.0f %s", frequencyDbl, MHz)
        } else {
            String.format(locale, "%.1f %s", frequencyDbl / 1000, GHz)
        }

        return fmtFrequency

    }

} // end class Utils
