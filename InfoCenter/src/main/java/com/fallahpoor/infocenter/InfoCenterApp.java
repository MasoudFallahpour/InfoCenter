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

package com.fallahpoor.infocenter;

import android.app.Application;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;

/**
 * This class does some initializations on app start.
 *
 * @author Masood Fallahpoor
 */
@ReportsCrashes(
        formUri = "https://mfxpert.cloudant.com/acra-infocenter/_design/acra-storage/_update/report",
        reportType = HttpSender.Type.JSON,
        httpMethod = HttpSender.Method.POST,
        formUriBasicAuthLogin = "ghtleadvasifisteardsters",
        formUriBasicAuthPassword = "UndcmjgHamlXQjWkoaLhifLk",
        customReportContent = {
                ReportField.REPORT_ID, ReportField.APP_VERSION_CODE,
                ReportField.APP_VERSION_NAME, ReportField.ANDROID_VERSION,
                ReportField.BUILD, ReportField.STACK_TRACE
        },
        mode = ReportingInteractionMode.DIALOG,
        resToastText = R.string.crash_toast_text,
        resDialogTitle = R.string.app_name,
        resDialogIcon = android.R.drawable.ic_dialog_info,
        resDialogText = R.string.crash_dialog_text,
        resDialogPositiveButtonText = R.string.crash_dialog_positive_button_text,
        resDialogNegativeButtonText = R.string.crash_dialog_negative_button_text)
public class InfoCenterApp extends Application {

    @Override
    public void onCreate() {

        super.onCreate();

        // Initialize ACRA library
        ACRA.init(this);

        // Initialize Utils class
        Utils.initialize(this);

    }

} // end class InfoCenterApp