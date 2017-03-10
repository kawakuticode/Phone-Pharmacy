package com.code.kawakuti.phonepharmacy.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.code.kawakuti.phonepharmacy.utilis.PharmacyHelper;

import java.io.StringWriter;

/**
 * Created by Russelius on 05/03/16.
 */
public class MyLocationTrack implements LocationListener {
    private static final String TAG = "LOCATIONTRACK";
    private Context myContext;

    public LocationManager getmLocationManager() {
        return mLocationManager;
    }

    private LocationManager mLocationManager;
    private StringWriter output;


    private static final int COARSE_PERMISSION_REQUEST_CODE = 0;
    private static long LOCATION_VALIDITY_DURATION_MS = 2 * 60 * 1000; // 2 min
    Location result;

    public MyLocationTrack(Context mContext , LocationManager lm ) {
        this.myContext = mContext;
        this.mLocationManager = lm;
        output = new StringWriter();

    }
    /**
     * Start retrieving location, caching it for a few minutes.
     */
    public Location startRetrievingLocation() {
        Location location = getLastKnownLocation();

        // If the last known location is recent enough, just return it, else request location updates
        if (location != null && (System.currentTimeMillis() - location.getTime()) <= LOCATION_VALIDITY_DURATION_MS) {
                result = location;
        } else {
            // Don't start listeners if no provider is enabled
            if (!PharmacyHelper.isGpsLocationProviderEnabled(getmLocationManager()) && !PharmacyHelper.isNetworkLocationProviderEnabled(getmLocationManager())) {
                    Log.d(TAG, "UNABLE TO LOCATE ");
                }
            }
        if (PharmacyHelper.isGpsLocationProviderEnabled(getmLocationManager())) {
                if (ActivityCompat.checkSelfPermission(myContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(myContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                }
            }
        if (PharmacyHelper.isNetworkLocationProviderEnabled(getmLocationManager())) {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            }
    return result;
    }




    public void stopRetrievingLocation() {
        if (ActivityCompat.checkSelfPermission(myContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(myContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        this.mLocationManager.removeUpdates(this);
    }



    /**
     * Get last known location even if this location is old.
     */
    public Location getLastKnownLocation() {


        Location netLoc = null;
        Location gpsLoc = null;
        Location result = null;

        if (ActivityCompat.checkSelfPermission(myContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(myContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (PharmacyHelper.isGpsLocationProviderEnabled(getmLocationManager())) {
                gpsLoc = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            if (PharmacyHelper.isNetworkLocationProviderEnabled(getmLocationManager())) {
                    netLoc = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            // If there are both values use the latest one
            if (gpsLoc != null && netLoc != null) {
                if (gpsLoc.getTime() > netLoc.getTime()) {
                    result = gpsLoc;
                } else {
                    result = netLoc;
                }
            }

            if (gpsLoc != null) {
                result = gpsLoc;
            }
            if (netLoc != null) {
                result = netLoc;
            }

        }
        if (PharmacyHelper.isGpsLocationProviderEnabled(getmLocationManager())) {
            gpsLoc = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        if (PharmacyHelper.isNetworkLocationProviderEnabled(getmLocationManager())) {
            netLoc = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        // If there are both values use the latest one
        if (gpsLoc != null && netLoc != null) {
            if (gpsLoc.getTime() > netLoc.getTime()) {
                result = gpsLoc;
            } else {
                result = netLoc;
            }
        }

        if (gpsLoc != null) {
            return gpsLoc;
        }
        if (netLoc != null) {
            result = netLoc;
        }
        return result;

    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        output.append("\n\nProvider Status Changed: " + provider + ", Status=" + status + ", Extras=" + extras);
    }


    @Override
    public void onLocationChanged(Location location) {
        // Retrieving Latitude
        location.getLatitude();
        location.getLongitude();

/*        String text = "My Current Location is:\nLatitude = "
                + location.getLatitude() + "\nLongitude = "
                + location.getLongitude();
        output.append(text);
        Toast.makeText(myContext, text, Toast.LENGTH_SHORT)
                .show()*/;
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(myContext, "provider  enabled " + provider.toString(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(myContext, "provider  disable  " + provider.toString(), Toast.LENGTH_SHORT).show();
        if (ActivityCompat.checkSelfPermission(myContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(myContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        mLocationManager.removeUpdates(this);

    }
}
