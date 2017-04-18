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

package com.fallahpoor.infocenter.fragments.gps;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.fallahpoor.infocenter.R;
import com.fallahpoor.infocenter.Utils;

import java.io.IOException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * This class provides the information needed by GpsFragment.
 *
 * @author Masood Fallahpoor
 */
class GpsObservable extends Observable {

    private final int MIN_UPDATE_INTERVAL = 30000;
    private final float MIN_DISTANCE = 50;
    private String mStatus;
    private String mLocation;
    private Context mContext;
    private LocationManager mLocationManager;
    private GpsLocationListener mGpsLocationListener;

    GpsObservable(Context context) {

        mContext = context;
        mLocationManager = (LocationManager) context.getSystemService(
                Context.LOCATION_SERVICE);
        mGpsLocationListener = new GpsLocationListener();

        if (isGpsEnabled()) {
            mStatus = mContext.getString(R.string.on);
            mLocation = mContext.getString(R.string.gps_sub_item_obtaining_location);
        } else {
            mStatus = mContext.getString(R.string.off);
            mLocation = mContext.getString(R.string.unknown);
        }

    }

    String getStatus() {
        return mStatus;
    }

    String getLocation() {
        return mLocation;
    }

    private void enableLocationUpdates() {

        try {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    MIN_UPDATE_INTERVAL, MIN_DISTANCE, mGpsLocationListener);
        } catch (SecurityException exception) {
            /* App is running on a device with Android Marshmallow and user hasn't granted the
               ACCESS_FINE_LOCATION permission. */
            mLocation = mContext.getString(R.string.unknown);
            setChanged();
            notifyObservers();
        }

    }

    private void disableLocationUpdates() {

        try {
            mLocationManager.removeUpdates(mGpsLocationListener);
        } catch (SecurityException exception) {
            /* App is running on a device with Android Marshmallow and user hasn't granted the
               ACCESS_FINE_LOCATION permission. */
        }

    }

    private boolean isGpsEnabled() {
        return mLocationManager.isProviderEnabled(LocationManager.
                GPS_PROVIDER);
    }

    @Override
    public void addObserver(Observer observer) {

        super.addObserver(observer);
        enableLocationUpdates();

    }

    @Override
    public synchronized void deleteObserver(Observer observer) {

        disableLocationUpdates();
        super.deleteObserver(observer);

    }

    private class GpsLocationListener implements LocationListener {

        private String getLatitude(Location location) {
            return String.format(Utils.getLocale(), "%f", location.
                    getLatitude());
        }

        private String getLongitude(Location location) {
            return String.format(Utils.getLocale(), "%f", location.
                    getLongitude());
        }

        @Override
        public void onLocationChanged(Location location) {

            String cityName = mContext.getString(R.string.unknown);
            String countryName = mContext.getString(R.string.unknown);
            String country = mContext.getString(R.string.gps_sub_item_country);
            String city = mContext.getString(R.string.gps_sub_item_city);
            String latitude = mContext.getString(R.string.gps_sub_item_latitude);
            String longitude = mContext.getString(R.string.gps_sub_item_longitude);
            Geocoder geocoder = new Geocoder(mContext, Utils.getLocale());
            List<Address> addresses;

            try {
                addresses = geocoder.getFromLocation(location.getLatitude(),
                        location.getLongitude(), 1);
                if (addresses != null && addresses.size() > 0) {
                    Address address = addresses.get(0);
                    if (address.getLocality() != null) {
                        cityName = address.getLocality();
                    }
                    if (address.getCountryName() != null) {
                        countryName = address.getCountryName();
                    }

                }

                mLocation = country + ": " + countryName + " / " +
                        city + ": " + cityName + " / " + latitude + ": " +
                        getLatitude(location) + " / " + longitude + ": " +
                        getLongitude(location);

            } catch (IOException ignored) {
                mLocation = mContext.getString(R.string.unknown);
            }

            setChanged();
            notifyObservers();

        } // end method onLocationChanged

        @Override
        public void onProviderDisabled(String provider) {
            if (provider.equals(LocationManager.GPS_PROVIDER)) {
                mStatus = mContext.getString(R.string.off);
                mLocation = mContext.getString(R.string.unknown);
                setChanged();
                notifyObservers();
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
            if (provider.equals(LocationManager.GPS_PROVIDER)) {
                mStatus = mContext.getString(R.string.on);
                mLocation = mContext.getString(R.string.gps_sub_item_obtaining_location);
                setChanged();
                notifyObservers();
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

    } // end class GpsLocationListener

} // end class GpsObservable
