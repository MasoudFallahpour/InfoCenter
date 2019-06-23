package com.fallahpoor.infocenter.fragments.battery

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.fallahpoor.infocenter.R
import com.fallahpoor.infocenter.Utils
import java.text.NumberFormat
import java.util.*

class BatteryViewModel(application: Application) : AndroidViewModel(application) {

    private val utils: Utils
    private val percentFormat: NumberFormat
    private val context: Context
    private val batteryStateMutableLiveData: MutableLiveData<BatteryState>
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            updateBatteryStateLiveData(intent)
        }
    }

    val batteryStateLiveData: LiveData<BatteryState>
        get() {
            context.registerReceiver(broadcastReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            return batteryStateMutableLiveData
        }

    private fun updateBatteryStateLiveData(intent: Intent) {

        val status: Int = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0)
        val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
        val health: Int = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0)
        val plugged: Int = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)
        val temperature: Int = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10
        val voltage: Int = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)
        val technology: String = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY)

        val batteryState = BatteryState(
            getStatus(status),
            getLevel(level),
            getHealth(health),
            getPlugged(status, plugged),
            getTechnology(technology),
            getTemperature(temperature),
            getVoltage(voltage)
        )

        batteryStateMutableLiveData.value = batteryState

    }

    init {
        context = application
        batteryStateMutableLiveData = MutableLiveData()
        utils = Utils(context)
        percentFormat = NumberFormat.getPercentInstance(utils.locale)
    }

    private fun getStatus(intStatus: Int): String {

        return if (intStatus == BatteryManager.BATTERY_STATUS_CHARGING ||
            intStatus == BatteryManager.BATTERY_STATUS_FULL
        ) {
            context.getString(R.string.bat_sub_item_charging)
        } else {
            context.getString(R.string.bat_sub_item_discharging)
        }

    }

    private fun getLevel(level: Int): String {
        return percentFormat.format(level.toDouble() / 100)
    }

    private fun getHealth(intHealth: Int): String {
        return when (intHealth) {
            BatteryManager.BATTERY_HEALTH_COLD,
            BatteryManager.BATTERY_HEALTH_GOOD -> context.getString(R.string.bat_sub_item_good)
            BatteryManager.BATTERY_HEALTH_DEAD -> context.getString(R.string.bat_sub_item_dead)
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> context.getString(R.string.bat_sub_item_over_voltage)
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> context.getString(R.string.bat_sub_item_overheat)
            else -> context.getString(R.string.unknown)
        }
    }

    private fun getPlugged(status: Int, plugged: Int): String {

        return if (status == BatteryManager.BATTERY_STATUS_CHARGING ||
            status == BatteryManager.BATTERY_STATUS_FULL
        ) {
            when (plugged) {
                BatteryManager.BATTERY_PLUGGED_AC -> context.getString(R.string.bat_sub_item_yes_ac)
                BatteryManager.BATTERY_PLUGGED_USB -> context.getString(R.string.bat_sub_item_yes_usb)
                BatteryManager.BATTERY_PLUGGED_WIRELESS -> context.getString(R.string.bat_sub_item_yes_wireless)
                else -> context.getString(R.string.unknown)
            }
        } else {
            context.getString(R.string.no)
        }

    }

    private fun getTechnology(technology: String): String {
        return when (technology.toLowerCase(Locale.US)) {
            "li-ion" -> context.getString(R.string.bat_sub_item_lithium_ion)
            "li-poly" -> context.getString(R.string.bat_sub_item_lithium_polymer)
            else -> technology
        }
    }

    private fun getTemperature(temperature: Int): String {
        return String.format(
            utils.locale, "%d %s", temperature,
            context.getString(R.string.bat_sub_item_centigrade)
        )
    }

    private fun getVoltage(voltage: Int): String {
        return String.format(
            utils.locale, "%d %s", voltage,
            context.getString(R.string.bat_sub_item_milli_volt)
        )
    }

    override fun onCleared() {
        super.onCleared()
        context.unregisterReceiver(broadcastReceiver)
    }

}
