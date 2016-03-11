package com.code.kawakuti.phonepharmacy.location;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.code.kawakuti.phonepharmacy.R;
import com.code.kawakuti.phonepharmacy.home.ImageLoader;

/**
 * Created by Russelius on 01/02/16.
 */
public class FindHospitalFragment extends Fragment {

    private ImageLoader l;
    private LocationManager lmanager;
    private MyLocationTrack mylocation;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.findhospital, container, false);

        l = new ImageLoader(getContext());
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.hospitalx);
        Bitmap circularBitmap = ImageLoader.getRoundedCornerBitmap(bitmap, 100);

        ImageView circularImageView = (ImageView) rootView.findViewById(R.id.circleHospital);
        circularImageView.setImageBitmap(circularBitmap);

        lmanager = (LocationManager) this.getContext().getSystemService(Context.LOCATION_SERVICE);
        mylocation = new MyLocationTrack(getContext(), lmanager);

        circularImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getContext(), "Searching for Hospitals", Toast.LENGTH_SHORT).show();
                if (!mylocation.isGpsLocationProviderEnabled()) {
                    showGpsSettingsAlert();
                } else if (mylocation.isGpsLocationProviderEnabled()) {
                    new SyncLocation().execute();
                }

            }
        });
        return rootView;
    }

    public void showGpsSettingsAlert() {

        final Dialog dialog = new Dialog(this.getContext());
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
                mylocation.turnGPSWifi();
                dialog.dismiss();
                startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1);
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

    @Override
    public void onPause() {
       mylocation.stopRetrievingLocation();
        super.onPause();
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);

        switch (requestCode) {
            case 1: {
                Toast.makeText(getActivity(), "Location enabled by user!", Toast.LENGTH_LONG).show();
                break;
            }
            case Activity.RESULT_CANCELED: {
                Toast.makeText(getActivity(), "Location not enabled, user cancelled.", Toast.LENGTH_LONG).show();
                break;
            }
            default: {
                break;
            }
        }

    }

    private class SyncLocation extends AsyncTask<MyLocationTrack, Integer, Location> {
        private Location resultx = null;
        ProgressDialog dialog;

        public SyncLocation() {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(getContext());
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage("wait while find location...");
            dialog.setCancelable(false);
            dialog.show();
            resultx = mylocation.startRetrievingLocation();
        }

        @Override
        protected void onProgressUpdate(Integer... locations) {
            super.onProgressUpdate(locations);
        }

        @Override
        protected Location doInBackground(MyLocationTrack... params) {
            if (resultx != null) {
                return resultx;
            }
            return resultx;
        }


        @Override
        protected void onPostExecute(Location location) {
            super.onPostExecute(location);
            dialog.dismiss();
            Intent findplaces = new Intent(getContext(), FindPlaces.class);
            findplaces.putExtra("TYPE_OF_PLACE", "hospital");
            findplaces.putExtra("location", location);
            startActivity(findplaces);

        }
    }
}


