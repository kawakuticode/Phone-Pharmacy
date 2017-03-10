package com.code.kawakuti.phonepharmacy.location;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.code.kawakuti.phonepharmacy.R;
import com.code.kawakuti.phonepharmacy.utilis.ImageLoader;
import com.code.kawakuti.phonepharmacy.utilis.PharmacyHelper;

/**
 * Created by Russelius on 01/02/16.
 */
public class FindHospitalFragment extends Fragment {

    private ImageLoader l;
    private LocationManager lmanager;
    private MyLocationTrack mylocation;
    private Location result_location = null;
    private static String TYPE_OF_PLACE = "hospital";
    private LocationUtilities l_utilities;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.findhospital, container, false);

        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.hospitalx);
        Bitmap circularBitmap = ImageLoader.getRoundedCornerBitmap(bitmap, 100);

        ImageView circularImageView = (ImageView) rootView.findViewById(R.id.circleHospital);
        circularImageView.setImageBitmap(circularBitmap);

        lmanager = (LocationManager) this.getContext().getSystemService(Context.LOCATION_SERVICE);
        mylocation = new MyLocationTrack(getContext(), lmanager);
        l_utilities = new LocationUtilities(getContext(), getActivity());

        circularImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getContext(), "Searching for Hospitals", Toast.LENGTH_SHORT).show();
                if (!PharmacyHelper.isGpsLocationProviderEnabled(lmanager)) {
                    l_utilities.showGpsSettingsAlert();
                } else {
                    new GetAsyncLocation(mylocation, result_location, getContext(), TYPE_OF_PLACE).execute();

                }

            }
        });
        return rootView;
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
}


