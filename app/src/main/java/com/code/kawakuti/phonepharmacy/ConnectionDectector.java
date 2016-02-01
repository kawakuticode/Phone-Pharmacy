package com.code.kawakuti.phonepharmacy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

/**
 * Created by Russelius on 01/02/16.
 */
public class ConnectionDectector {
    private Context _context;

    public ConnectionDectector(Context context) {
        this._context = context;
    }

    /**
     * Checking for all possible internet providers
     **/
    public boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

    public void showSettingsAlertInternet() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(_context);

// Setting Dialog Title
        alertDialog.setTitle("INTERNET SETTINGS");

// Setting Dialog Message
        alertDialog.setMessage("INTERNET Is Not Enabled. Want go Settings?! ");

// On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                _context.startActivity(intent);
            }
        });

// on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

// Showing Alert Message
        alertDialog.show();
    }
}

