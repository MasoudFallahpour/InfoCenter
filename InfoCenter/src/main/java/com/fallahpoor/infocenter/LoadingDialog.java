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

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup.LayoutParams;

import com.pnikosis.materialishprogress.ProgressWheel;

/**
 * This class creates a custom dialog to display when something is loading.
 *
 * @author Masood Fallahpoor
 */
public class LoadingDialog extends Dialog {

    public LoadingDialog(Context context) {
        super(context, R.style.LoadingDialogTheme);
    }

    public static LoadingDialog show(Context context) {

        ProgressWheel progressWheel = new ProgressWheel(context);
        progressWheel.setBarColor(Color.parseColor("#607d8b"));
        progressWheel.setCircleRadius(90);
        progressWheel.setBarWidth(8);
        progressWheel.spin();

        LoadingDialog dialog = new LoadingDialog(context);
        dialog.setCancelable(false);
        dialog.addContentView(progressWheel, new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        dialog.show();

        return dialog;

    }

} // end class LoadingDialog