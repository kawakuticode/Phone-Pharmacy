package com.code.kawakuti.phonepharmacy.location;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

/**
 * Created by russeliusernestius on 09/03/17.
 */

public class GetAsyncLocation extends AsyncTask<MyLocationTrack, Void, Location> {
    private View v;
    private Location result_location;
    private MyLocationTrack myLocationTrack;
    private Context mContext;
    private String type_of_place;
    private ProgressDialog dialog;


    public GetAsyncLocation(MyLocationTrack loc, Context context, String typeOfPlace) {
        this.myLocationTrack = loc;
        this.result_location = myLocationTrack.startRetrievingLocation();
        this.mContext = context;
        this.type_of_place = typeOfPlace;

    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(mContext);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("searching..." + type_of_place);
        dialog.setCancelable(false);
        dialog.show();

    }


    @Override
    protected Location doInBackground(MyLocationTrack... params) {
      if (result_location != null) {
            return result_location;
        }
        return result_location;
    }


    @Override
    protected void onPostExecute(Location location) {
        //super.onPostExecute(location);
        if (location != null) {
            dialog.dismiss();
            Intent findplaces = new Intent(mContext, ShowPlacesOnMap.class);
            findplaces.putExtra("TYPE_OF_PLACE", type_of_place);
            findplaces.putExtra("location", location);
            myLocationTrack.stopRetrievingLocation();
            mContext.startActivity(findplaces);
        } else  {
            dialog.dismiss();
            Toast.makeText(mContext ," Unable to get Your Location Try Again / check you have data conectivity ", Toast.LENGTH_LONG ).show();
        }
    }
}

