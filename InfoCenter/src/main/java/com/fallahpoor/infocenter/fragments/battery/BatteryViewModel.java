package com.fallahpoor.infocenter.fragments.battery;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import com.fallahpoor.infocenter.R;
import com.fallahpoor.infocenter.Utils;

import java.text.NumberFormat;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class BatteryViewModel extends AndroidViewModel {

    private Utils utils;
    private NumberFormat percentFormat;
    private Context context;
    private MutableLiveData<BatteryState> batteryStateMutableLiveData;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateBatteryStateLiveData(intent);
        }
    };

    private void updateBatteryStateLiveData(Intent intent) {

        int status;
        int level;
        int health;
        int plugged;
        int temperature;
        int voltage;
        String technology;

        status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
        level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);
        plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
        technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
        temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10;
        voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);

        BatteryState batteryState = new BatteryState();

        batteryState.setStatus(getStatus(status));
        batteryState.setLevel(getLevel(level));
        batteryState.setHealth(getHealth(health));
        batteryState.setPlugged(getPlugged(status, plugged));
        batteryState.setTechnology(getTechnology(technology));
        batteryState.setTemperature(getTemperature(temperature));
        batteryState.setVoltage(getVoltage(voltage));

        batteryStateMutableLiveData.setValue(batteryState);

    }

    public BatteryViewModel(@NonNull Application application) {
        super(application);
        context = application;
        batteryStateMutableLiveData = new MutableLiveData<>();
        utils = new Utils(context);
        percentFormat = NumberFormat.getPercentInstance(utils.getLocale());
    }

    public LiveData<BatteryState> getBatteryStateLiveData() {
        context.registerReceiver(broadcastReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        return batteryStateMutableLiveData;
    }

    private String getStatus(int intStatus) {

        String status;

        if (intStatus == BatteryManager.BATTERY_STATUS_CHARGING
                || intStatus == BatteryManager.BATTERY_STATUS_FULL) {
            status = context.getString(R.string.bat_sub_item_charging);
        } else {
            status = context.getString(R.string.bat_sub_item_discharging);
        }

        return status;

    }

    private String getLevel(int level) {
        return percentFormat.format((double) level / 100);
    }

    private String getHealth(int intHealth) {

        String health;

        switch (intHealth) {
            case BatteryManager.BATTERY_HEALTH_COLD:
            case BatteryManager.BATTERY_HEALTH_GOOD:
                health = context.getString(R.string.bat_sub_item_good);
                break;
            case BatteryManager.BATTERY_HEALTH_DEAD:
                health = context.getString(R.string.bat_sub_item_dead);
                break;
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                health = context.getString(R.string.bat_sub_item_over_voltage);
                break;
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                health = context.getString(R.string.bat_sub_item_overheat);
                break;
            default:
                health = context.getString(R.string.unknown);
                break;
        }

        return health;

    }

    private String getPlugged(int intStatus, int intPlugged) {

        String plugged;

        if (intStatus == BatteryManager.BATTERY_STATUS_CHARGING
                || intStatus == BatteryManager.BATTERY_STATUS_FULL) {
            switch (intPlugged) {
                case BatteryManager.BATTERY_PLUGGED_AC:
                    plugged = context.getString(R.string.bat_sub_item_yes_ac);
                    break;
                case BatteryManager.BATTERY_PLUGGED_USB:
                    plugged = context.getString(R.string.bat_sub_item_yes_usb);
                    break;
                case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                    plugged = context.getString(R.string.bat_sub_item_yes_wireless);
                    break;
                default:
                    plugged = context.getString(R.string.unknown);
            }
        } else {
            plugged = context.getString(R.string.no);
        }

        return plugged;

    }

    private String getTechnology(String technology) {

        String tech;

        switch (technology.toLowerCase(Locale.US)) {
            case "li-ion":
                tech = context.getString(R.string.bat_sub_item_lithium_ion);
                break;
            case "li-poly":
                tech = context.getString(R.string.bat_sub_item_lithium_polymer);
                break;
            default:
                tech = technology;
        }

        return tech;

    }

    private String getTemperature(int temperature) {
        return String.format(utils.getLocale(), "%d %s", temperature,
                context.getString(R.string.bat_sub_item_centigrade));
    }

    private String getVoltage(int voltage) {
        return String.format(utils.getLocale(), "%d %s", voltage,
                context.getString(R.string.bat_sub_item_milli_volt));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        context.unregisterReceiver(broadcastReceiver);
    }

}
