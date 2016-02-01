package com.code.kawakuti.phonepharmacy;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Created by Russelius on 01/02/16.
 */
public class FindPlaces extends AppCompatActivity implements LocationListener {


    private final String TAG = getClass().getSimpleName();
    private GoogleMap mMap;
    private String type_of_place;
    private GpsTracker gpsTracker;
    private LocationManager locationManager;
    private Location location;

    private ConnectionDectector connectionDectector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.findplaces);


        initCompo(savedInstanceState);
        gpsTracker = new GpsTracker(this);
        connectionDectector = new ConnectionDectector(this);

        try {
            if (mMap == null) {
                mMap = ((MapFragment) getFragmentManager().
                        findFragmentById(R.id.map)).getMap();
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        new GetPlaces(FindPlaces.this,
                type_of_place).execute();

    }


    private void initCompo(Bundle savedInstanceState) {

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                type_of_place = extras.getString("TYPE_OF_PLACE");

            } else {
                type_of_place = null;

            }
        } else {
            type_of_place = (String) savedInstanceState.getSerializable(("TYPE_OF_PLACE"));

        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
    private class GetPlaces extends AsyncTask<ArrayList<Place>, Integer, ArrayList<Place>> {
        private ProgressDialog dialog;
        private Context context;
        private String type_of_place;
        ArrayList<Place> findPlaces = new ArrayList<>();

        public GetPlaces(Context c, String type) {
            this.context = c;
            this.type_of_place = type;
        }

        @Override
        protected ArrayList<Place> doInBackground(ArrayList<Place>... params) {
            PlacesService service = new PlacesService("AIzaSyA-iEtJ1Mqofg3n9WyjxeLTCAZ_68wR06Y");

            if (connectionDectector.isConnectingToInternet()) {
                if (gpsTracker.canGetLocation()) {

                    location = gpsTracker.getLocation();

                    Log.d(TAG , location.toString() );
                    if (location != null) {
                        findPlaces = service.findPlaces(
                                gpsTracker.getLatitude(), gpsTracker.getLongitude(), type_of_place);
                    }
                }
            } else {
                connectionDectector.showSettingsAlertInternet();
            }
            return findPlaces;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            dialog.setCancelable(false);
            dialog.setMessage("Loading....");
            dialog.isIndeterminate();
            dialog.show();
        }

        @Override
        protected void onPostExecute(ArrayList<Place> places) {
            super.onPostExecute(places);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            mMap.clear();
            if (places.size() != 0) {

                mMap.addMarker(new MarkerOptions()
                        .title(" I am Here!! ")
                        .position(
                                new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude()))).setAlpha(5);
                for (int i = 0; i < places.size(); i++) {


                    mMap.addMarker(new MarkerOptions()
                            .title(places.get(i).getName())
                            .position(
                                    new LatLng(places.get(i).getLatitude(), places
                                            .get(i).getLongitude())));
                    Log.e(TAG, "places-->  : " + places.get(i).getName());

                }
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(places.get(0).getLatitude(), places
                                .get(0).getLongitude()))
                                // Sets the center of the map to
                                // Mountain View
                        .zoom(14) // Sets the zoom
                        .tilt(30) // Sets the tilt of the camera to 30 degrees
                        .build(); // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));
            } else {
                Toast.makeText(getApplicationContext(), "No " + type_of_place + " to Show ", Toast.LENGTH_LONG).show();
            }
        }
    }
}
