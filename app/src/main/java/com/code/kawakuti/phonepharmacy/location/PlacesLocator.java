package com.code.kawakuti.phonepharmacy.location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Russelius on 01/02/16.
 */
public class PlacesLocator {

    private String API_KEY;
    public PlacesLocator(String apikey) {
        this.API_KEY = apikey;
    }
    public void setApiKey(String apikey) {
        this.API_KEY = apikey;
    }
    public ArrayList<Place> getPlacesArround(double latitude, double longitude,
                                             String placeSpecification , int radius) {

        String urlString = makeUrl(latitude, longitude, placeSpecification, radius);
        ArrayList<Place> arrayList = new ArrayList<>();

        try {

            String json = getJSON(urlString);
            JSONObject object = new JSONObject(json);
            JSONArray array = object.getJSONArray("results");

            for (int i = 0; i < array.length(); i++) {
                try {
                    Place place = Place
                            .jsonToReferencePoint((JSONObject) array.get(i));
                    //Log.v("Places Services ", "" + place);
                    arrayList.add(place);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (JSONException ex) {
            Logger.getLogger(PlacesLocator.class.getName()).log(Level.SEVERE,
                    null, ex);
        }
        return arrayList;
    }

    // https://maps.googleapis.com/maps/api/place/search/json?location=28.632808,77.218276&radius=500&types=atm&sensor=false&key=apikey
    private String makeUrl(double latitude, double longitude, String place , int radius) {
        StringBuilder urlString = new StringBuilder(
                "https://maps.googleapis.com/maps/api/place/search/json?");

        if (place.equals("")) {
            urlString.append("&location=");
            urlString.append(Double.toString(latitude));
            urlString.append(",");
            urlString.append(Double.toString(longitude));
            urlString.append("&radius="+radius);
            urlString.append("&type="+place);
            urlString.append("&sensor=false&key=" + API_KEY);
        } else {
            urlString.append("&location=");
            urlString.append(Double.toString(latitude));
            urlString.append(",");
            urlString.append(Double.toString(longitude));
            urlString.append("&radius="+radius);
            urlString.append("&type=" + place);
            urlString.append("&sensor=false&key=" + API_KEY);
        }
        return urlString.toString();
    }

    protected String getJSON(String url) {
        return getUrlContents(url);
    }

    private String getUrlContents(String theUrl) {
        StringBuilder content = new StringBuilder();
        try {
            URL url = new URL(theUrl);
            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(urlConnection.getInputStream()), 8);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line + "\n");
            }
            bufferedReader.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();

    }
}
