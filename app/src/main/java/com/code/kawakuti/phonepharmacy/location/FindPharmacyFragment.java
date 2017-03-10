package com.code.kawakuti.phonepharmacy.location;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.code.kawakuti.phonepharmacy.R;
import com.code.kawakuti.phonepharmacy.utilis.ImageLoader;
import com.code.kawakuti.phonepharmacy.utilis.PharmacyHelper;

public class FindPharmacyFragment extends Fragment {

    private static String TYPE_OF_PLACE = "pharmacy";
    private ImageLoader l;
    private LocationManager lmanager;
    private MyLocationTrack mylocation;
    private LocationUtilities l_utilities;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout resource that'll be returned
        View rootView = inflater.inflate(R.layout.findpharmacy, container, false);

        lmanager = (LocationManager) this.getContext().getSystemService(Context.LOCATION_SERVICE);
        l_utilities = new LocationUtilities(getContext(), getActivity());

        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.farmacy);
        Bitmap circularBitmap = ImageLoader.getRoundedCornerBitmap(bitmap, 100);
        ImageView circularImageView = (ImageView) rootView.findViewById(R.id.circleView);
        circularImageView.setImageBitmap(circularBitmap);

        TextView find = ((TextView) rootView.findViewById(R.id.textView));

        lmanager = (LocationManager) this.getContext().getSystemService(Context.LOCATION_SERVICE);
        mylocation = new MyLocationTrack(getContext(), lmanager);

        circularImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getContext(), "finding your location ...", Toast.LENGTH_SHORT).show();
                if (!PharmacyHelper.isGpsLocationProviderEnabled(lmanager)) {
                    l_utilities.showGpsSettingsAlert();
                } else if (PharmacyHelper.isGpsLocationProviderEnabled(lmanager)) {
                    new GetAsyncLocation(mylocation, getContext(), TYPE_OF_PLACE).execute();

                }
            }
        });
        return rootView;
    }



    @Override
    public void onPause() {
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
                // The user was asked to change settings, but chose not to
                Toast.makeText(getActivity(), "Location not enabled, user cancelled.", Toast.LENGTH_LONG).show();
                break;
            }
            default: {
                break;
            }
        }

    }


}


