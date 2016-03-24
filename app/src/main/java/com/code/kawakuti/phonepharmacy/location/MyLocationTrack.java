package com.code.kawakuti.phonepharmacy.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Created by Russelius on 05/03/16.
 */
public class MyLocationTrack implements LocationListener {
    private static final String TAG = "LOCATIONTRACK";
    private Context myContext;
    private LocationManager mLocationManager;
    private String bestProvider;
    private StringWriter output;


    private static long LOCATION_VALIDITY_DURATION_MS = 2 * 60 * 1000; // 2 min

    private final static int LOCATION_TIMEOUT_MS = 30 * 1000; // 30 seconds
    private Handler mHandler;
    Location result;

    public MyLocationTrack(Context mContext , LocationManager lm ) {
        this.myContext = mContext;
        this.mLocationManager = lm;
        output = new StringWriter();

    }

    static void append(Appendable appendable, String c) throws IOException {
        appendable.append(c);
    }

    public boolean isGpsLocationProviderEnabled() {
        try {
            return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        return false;
    }

    public boolean isNetworkLocationProviderEnabled() {
        try {
            return mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
        return false;
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
            if (!isGpsLocationProviderEnabled() && !isNetworkLocationProviderEnabled()) {
                    Log.d(TAG, "UNABLE TO LOCATE ");
                }
            }
            if (isGpsLocationProviderEnabled()) {
                if (ActivityCompat.checkSelfPermission(myContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(myContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                }
            }
            if (isNetworkLocationProviderEnabled()) {
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

            if (isGpsLocationProviderEnabled()) {
                gpsLoc = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            if (isNetworkLocationProviderEnabled()) {
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
        if (isGpsLocationProviderEnabled()) {
            gpsLoc = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        if (isNetworkLocationProviderEnabled()) {
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




   /* */

    /**
     * Get address from location
     *//*
    public static String getReverseGeocodedAddress(Context context, Location location) {
        String addrStr = "";

        Geocoder geocoder = new Geocoder(context);
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            StringBuilder addressStrBuilder = new StringBuilder();
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                for (int i = 0; i < address.getMaxAddressLineIndex() && i < 1; i++) {
                    addressStrBuilder.append(address.getAddressLine(i) + " ");
                }
                addrStr = addressStrBuilder.toString();
            }
        } catch (IOException e) {
            Log.w(TAG, "Unable to retrieve reverse geocoded address.", e);
        }
        return addrStr;
    }

    public static String getReverseGeocodedApproxArea(Context context, Location location) {
        return getReverseGeocodedApproxArea(context, location.getLongitude(), location.getLatitude());
    }

    public static String getReverseGeocodedApproxArea(Context context, double longitude, double latitude) {
        String area = null;

        Geocoder geocoder = new Geocoder(context);
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 3);
            int numAddresses = addresses.size();
            for (int i = 0; i < numAddresses; i++) {
                Address address = addresses.get(i);

                if (area == null) {
                    area = address.getSubLocality();
                }
                if (area == null) {
                    area = address.getLocality();
                }
                if (area == null) {
                    area = address.getSubAdminArea();
                }
                if (area == null) {
                    area = address.getAdminArea();
                }

                if (area != null) {
                    return area;
                }
            }
        } catch (IOException e) {
            Log.w(TAG, "Unable to retrieve reverse geocoded address.", e);
        }
        return "";
    }

*/



    private void printLocation(Location location) {
        if (location != null) {
            System.out.println(location.toString());
        } else {
            System.out.println("\nLocation[unknown]\n\n");
        }
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

        String text = "My Current Location is:\nLatitude = "
                + location.getLatitude() + "\nLongitude = "
                + location.getLongitude();
        output.append(text);
        Toast.makeText(myContext, text, Toast.LENGTH_SHORT)
                .show();

    }


    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(myContext, "provider  enabled " + provider.toString(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {

        Toast.makeText(myContext, "provider  disable  " + provider.toString(),
                Toast.LENGTH_SHORT).show();
        if (ActivityCompat.checkSelfPermission(myContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(myContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        mLocationManager.removeUpdates(this);

    }
    public void turnGPSWifi() {
        WifiManager wifiManager = (WifiManager) myContext.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
    }
}
