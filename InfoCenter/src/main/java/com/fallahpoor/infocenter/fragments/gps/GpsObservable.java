package com.fallahpoor.infocenter.fragments.gps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

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
public class GpsObservable extends Observable {

    private final int MIN_UPDATE_INTERVAL = 30000;
    private final float MIN_DISTANCE = 50;
    private String mStatus;
    private String mLocation;
    private Context mContext;
    private LocationManager mLocationManager;
    private GpsLocationListener mGpsLocationListener;

    public GpsObservable(Context context) {

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

    public String getStatus() {
        return mStatus;
    }

    public String getLocation() {
        return mLocation;
    }

    private void enableLocationUpdates() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                            PackageManager.PERMISSION_GRANTED) {
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        MIN_UPDATE_INTERVAL, MIN_DISTANCE, mGpsLocationListener);
            } else {
                mLocation = mContext.getString(R.string.gps_sub_item_location_access_not_granted);
                setChanged();
                notifyObservers();
            }
            
        } else {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    MIN_UPDATE_INTERVAL, MIN_DISTANCE, mGpsLocationListener);
        }

    }

    private void disableLocationUpdates() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                            PackageManager.PERMISSION_GRANTED) {
                mLocationManager.removeUpdates(mGpsLocationListener);
            }

        } else {
            mLocationManager.removeUpdates(mGpsLocationListener);
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
