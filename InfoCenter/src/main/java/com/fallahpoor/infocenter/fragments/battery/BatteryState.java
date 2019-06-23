package com.fallahpoor.infocenter.fragments.battery;

import android.content.IntentFilter;

import com.fallahpoor.infocenter.Utils;

public class BatteryState {

    private IntentFilter batteryFilter;
    private String status;
    private String level;
    private String health;
    private String plugged;
    private String technology;
    private String temperature;
    private String voltage;
    private Utils utils;

    public IntentFilter getBatteryFilter() {
        return batteryFilter;
    }

    public void setBatteryFilter(IntentFilter batteryFilter) {
        this.batteryFilter = batteryFilter;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getHealth() {
        return health;
    }

    public void setHealth(String health) {
        this.health = health;
    }

    public String getPlugged() {
        return plugged;
    }

    public void setPlugged(String plugged) {
        this.plugged = plugged;
    }

    public String getTechnology() {
        return technology;
    }

    public void setTechnology(String technology) {
        this.technology = technology;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getVoltage() {
        return voltage;
    }

    public void setVoltage(String voltage) {
        this.voltage = voltage;
    }

    public Utils getUtils() {
        return utils;
    }

    public void setUtils(Utils utils) {
        this.utils = utils;
    }

}
