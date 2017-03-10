package com.code.kawakuti.phonepharmacy.location;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.code.kawakuti.phonepharmacy.R;

/**
 * Created by russeliusernestius on 09/03/17.
 */

public class LocationUtilities {

    private Context mContext;
    private Activity mActivity;

    public LocationUtilities(Context mContext, Activity activity) {
        this.mContext = mContext;
        this.mActivity = activity;
    }

    public static void turnGPSWifi(Context mContext) {
        WifiManager wifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public void showGpsSettingsAlert() {

        final Dialog dialog = new Dialog(this.getmContext());
        dialog.setContentView(R.layout.wifi_gps_turn_dialog);

        dialog.setTitle("Use location?");
        TextView text_message = (TextView) dialog.findViewById(R.id.text_message);
        text_message.setText("This app wants to change \n your device Settings : ");

        TextView text = (TextView) dialog.findViewById(R.id.text_dialog);
        text.setText("use GPS, Wi-Fi, and cell networks for location");
        ImageView image = (ImageView) dialog.findViewById(R.id.image);
        image.setImageResource(R.drawable.pin66);
        Button ok = (Button) dialog.findViewById(R.id.ok_turn);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnGPSWifi(getmContext());
                dialog.dismiss();
                mActivity.startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1);
            }
        });

        Button cancel = (Button) dialog.findViewById(R.id.cancel_turn);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }



}
