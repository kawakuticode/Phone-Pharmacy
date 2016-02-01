package com.code.kawakuti.phonepharmacy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class FindPharmacyFragment extends Fragment {

   private  ImageLoader l;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout resource that'll be returned
        View rootView = inflater.inflate(R.layout.findpharmacy, container, false);

        // Get the arguments that was supplied when
        // the fragment was instantiated in the
        // CustomPagerAdapter

        l = new ImageLoader();
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.farmacy);
        Bitmap circularBitmap = l.getRoundedCornerBitmap(bitmap, 100);

        ImageView circularImageView = (ImageView)rootView.findViewById(R.id.circleView);
        circularImageView.setImageBitmap(circularBitmap);

        TextView find = ((TextView) rootView.findViewById(R.id.textView));

        circularImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Searching for pharmacies" , Toast.LENGTH_SHORT).show();
                Intent findplaces = new Intent(getContext(), FindPlaces.class);
                    findplaces.putExtra("TYPE_OF_PLACE", "pharmacy");
                startActivity(findplaces);
            }
        });


        return rootView;
    }

    }

