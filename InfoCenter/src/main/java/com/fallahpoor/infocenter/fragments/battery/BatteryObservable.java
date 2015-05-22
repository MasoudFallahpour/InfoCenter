package com.fallahpoor.infocenter.fragments.battery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import com.fallahpoor.infocenter.R;
import com.fallahpoor.infocenter.Utils;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Observable;

/**
 * @author Masood Fallahpoor
 */
public class BatteryObservable extends Observable {

    private Context mContext;
    private NumberFormat mPercentFormat = NumberFormat.
            getPercentInstance(Utils.getLocale());
    private BatteryReceiver mBatteryReceiver;
    private IntentFilter mBatteryFilter;
    private String mStatus;
    private String mLevel;
    private String mHealth;
    private String mPlugged;
    private String mTechnology;
    private String mTemperature;
    private String mVoltage;

    public BatteryObservable(Context context) {

        mContext = context;
        mBatteryReceiver = new BatteryReceiver();
        mBatteryFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

    }

    public String getStatus() {
        return mStatus;
    }

    public String getLevel() {
        return mLevel;
    }

    public String getHealth() {
        return mHealth;
    }

    public String getPlugged() {
        return mPlugged;
    }

    public String getTechnology() {
        return mTechnology;
    }

    public String getTemperature() {
        return mTemperature;
    }

    public String getVoltage() {
        return mVoltage;
    }

    public void enableBatteryUpdates() {
        mContext.registerReceiver(mBatteryReceiver, mBatteryFilter);
    }

    public void disableBatteryUpdates() {
        mContext.unregisterReceiver(mBatteryReceiver);
    }

    /*
     * Returns a description about the current status of the battery (charging
     * or discharging).
     */
    private String getStatus(int intStatus) {

        String status;

        if (intStatus == BatteryManager.BATTERY_STATUS_CHARGING
                || intStatus == BatteryManager.BATTERY_STATUS_FULL) {
            status = mContext.getString(R.string.bat_sub_item_charging);
        } else {
            status = mContext.getString(R.string.bat_sub_item_discharging);
        }

        return status;

    }

    private String getLevel(int level) {
        return mPercentFormat.format((double) level / 100);
    }

    /*
     * Returns a user-friendly description about the current health status of
     * battery.
     */
    private String getHealth(int intHealth) {

        String health;

        switch (intHealth) {
            case BatteryManager.BATTERY_HEALTH_COLD:
            case BatteryManager.BATTERY_HEALTH_GOOD:
                health = mContext.getString(R.string.bat_sub_item_good);
                break;
            case BatteryManager.BATTERY_HEALTH_DEAD:
                health = mContext.getString(R.string.bat_sub_item_dead);
                break;
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                health = mContext.getString(R.string.bat_sub_item_over_voltage);
                break;
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                health = mContext.getString(R.string.bat_sub_item_overheat);
                break;
            default:
                health = mContext.getString(R.string.unknown);
                break;
        }

        return health;

    } // end method getHealth

    /*
     * Returns whether the battery is charging and if so determines the type of
     * power source.
     */
    private String getPlugged(int intStatus, int intPlugged) {

        String plugged;

        if (intStatus == BatteryManager.BATTERY_STATUS_CHARGING
                || intStatus == BatteryManager.BATTERY_STATUS_FULL) {
            switch (intPlugged) {
                case BatteryManager.BATTERY_PLUGGED_AC:
                    plugged = mContext.getString(R.string.bat_sub_item_yes_ac);
                    break;
                case BatteryManager.BATTERY_PLUGGED_USB:
                    plugged = mContext.getString(R.string.bat_sub_item_yes_usb);
                    break;
                case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                    plugged = mContext.getString(R.string.bat_sub_item_yes_wireless);
                    break;
                default:
                    plugged = mContext.getString(R.string.unknown);
            }
        } else {
            plugged = mContext.getString(R.string.no);
        }

        return plugged;

    } // end method getPlugged

    private String getTechnology(String technology) {

        String tech;

        switch (technology.toLowerCase(Locale.US)) {
            case "li-ion":
                tech = mContext.getString(R.string.bat_sub_item_lithium_ion);
                break;
            case "li-poly":
                tech = mContext.getString(R.string.bat_sub_item_lithium_polymer);
                break;
            default:
                tech = technology;
        }

        return tech;

    }

    private String getTemperature(int temperature) {
        return String.format(Utils.getLocale(), "%d %s", temperature,
                mContext.getString(R.string.bat_sub_item_centigrade));
    }

    private String getVoltage(int voltage) {
        return String.format(Utils.getLocale(), "%d %s", voltage,
                mContext.getString(R.string.bat_sub_item_milli_volt));
    }

    private class BatteryReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent batteryIntent) {

            int status, level, health, plugged, temperature, voltage;
            String technology;

            status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
            level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            health = batteryIntent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);
            plugged = batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
            technology = batteryIntent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
            temperature = batteryIntent.getIntExtra(BatteryManager.
                    EXTRA_TEMPERATURE, 0) / 10;
            voltage = batteryIntent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);

            mStatus = getStatus(status);
            mLevel = getLevel(level);
            mHealth = getHealth(health);
            mPlugged = getPlugged(status, plugged);
            mTechnology = getTechnology(technology);
            mTemperature = getTemperature(temperature);
            mVoltage = getVoltage(voltage);

            setChanged();
            notifyObservers();

        } // end method onReceive

    } // end class BatteryReceiver

} // end class BatteryObservable