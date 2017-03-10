package com.code.kawakuti.phonepharmacy.location;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.code.kawakuti.phonepharmacy.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Created by Russelius on 01/02/16.
 */
public class ShowPlacesOnMap extends AppCompatActivity implements LocationListener {

    private GoogleMap mMap;
    private String type_of_place;
    private Location location;
    private float zoom_value;
    private int radius;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.findplaces);
        initCompo(savedInstanceState);

        radius = 1000;
        zoom_value = 14;

        initMap();
        new SyncGetPlaces(ShowPlacesOnMap.this,
                type_of_place).execute();

    }


    public void initMap() {
        try {
            if (mMap == null) {
                ((MapFragment) getFragmentManager().
                        findFragmentById(R.id.map)).getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        mMap = googleMap;
                    }
                });
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void initCompo(Bundle savedInstanceState) {

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                type_of_place = extras.getString("TYPE_OF_PLACE");
                radius = extras.getInt("radius");
                location = extras.getParcelable("location");
            }
        } else {
            type_of_place = (String) savedInstanceState.getSerializable(("TYPE_OF_PLACE"));
            radius = savedInstanceState.getInt("radius");
            location = savedInstanceState.getParcelable("location");
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("TYPE_OF_PLACE", type_of_place);
        outState.putParcelable("location", location);
        outState.putInt("radius" , radius);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        type_of_place = savedInstanceState.getString("TYPE_OF_PLACE");
        location = savedInstanceState.getParcelable("location");
        radius = savedInstanceState.getInt("radius");
    }

    @Override
    public void onLocationChanged(Location location) {
        location.set(location);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_distances, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String url = null;
        Intent intent = null;
        switch (item.getItemId()) {
            case R.id.menu_1km:
                radius = 1000;
                zoom_value = 14;
                new SyncGetPlaces(ShowPlacesOnMap.this,
                    type_of_place).execute();
                break;
            case R.id.menu_2km:
                radius = 2000;
                zoom_value = 13;
                new SyncGetPlaces(ShowPlacesOnMap.this,
                        type_of_place).execute();
                break;
            case R.id.menu_3km:
                radius = 3000;
                zoom_value = 12;
                new SyncGetPlaces(ShowPlacesOnMap.this,
                        type_of_place).execute();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class SyncGetPlaces extends AsyncTask<ArrayList<Place>, Integer, ArrayList<Place>> {
        ArrayList<Place> findPlaces = new ArrayList<>();
        private ProgressDialog dialog;
        private Context context;
        private String type_of_place;

        public SyncGetPlaces(Context c, String type) {
            this.context = c;
            this.type_of_place = type;
        }

        @Override
        protected ArrayList<Place> doInBackground(ArrayList<Place>... params) {
            PlacesLocator placesLocator = new PlacesLocator("AIzaSyA-iEtJ1Mqofg3n9WyjxeLTCAZ_68wR06Y");
            if (location != null) {
                findPlaces = placesLocator.getPlacesArround(location.getLatitude(), location.getLongitude(), type_of_place, radius);
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
                                new LatLng(location.getLatitude(), location.getLongitude()))).setAlpha(5);
                for (int i = 0; i < places.size(); i++) {
                    mMap.addMarker(new MarkerOptions()
                            .title(places.get(i).getName())
                            .position(
                                    new LatLng(places.get(i).getLatitude(), places
                                            .get(i).getLongitude())));
                    //Log.e("FIND", "places-->  : " + places.get(i).getName());

                }
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(places.get(0).getLatitude(), places
                                .get(0).getLongitude()))
                                // Sets the center of the map to
                                // Mountain View
                        .zoom(zoom_value) // Sets the zoom
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
