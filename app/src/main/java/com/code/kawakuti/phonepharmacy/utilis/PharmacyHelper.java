package com.code.kawakuti.phonepharmacy.utilis;

import android.location.LocationManager;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by russeliusernestius on 17/02/17.
 */

public class PharmacyHelper {


    public static final String TAG = "PHARMACY HELPER";

    public static boolean isGpsLocationProviderEnabled(LocationManager mLocationManager) {
        try {
            return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            // Log.d(TAG, e.getMessage());
        }
        return false;
    }

    public static boolean isNetworkLocationProviderEnabled(LocationManager mLocationManager) {
        try {
            return mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            //Log.d(TAG, e.getMessage());
        }
        return false;
    }

    public static int verifyDate(Date indate) {
        return System.currentTimeMillis() < indate.getTime() ? 1 : 0;
    }

    public static String convertStringToDate(Date indate) {

        String dateString = null;
        SimpleDateFormat sdfr = new SimpleDateFormat("dd/MMM/yyyy");

        try {
            dateString = sdfr.format(indate);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return dateString;
    }
}
