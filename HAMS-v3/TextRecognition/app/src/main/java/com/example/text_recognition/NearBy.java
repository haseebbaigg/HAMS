package com.example.text_recognition;


import android.os.AsyncTask;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class NearBy extends AsyncTask<Object,String,String> {

    String googlePlaceData,url;
    GoogleMap map;

    @Override
    protected String doInBackground(Object... objects) {
        map = (GoogleMap) objects[0];
        url = (String) objects[1];

        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googlePlaceData = downloadUrl.ReadUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return googlePlaceData;
    }

    @Override
    protected void onPostExecute(String s) {
        List<HashMap<String,String>> nearByPlacesList = null;
        dataParser dataParser = new dataParser();
        nearByPlacesList= dataParser.parse(s);

        DisplayNearbyPlaces(nearByPlacesList);
    }

    public void DisplayNearbyPlaces(List<HashMap<String,String>> nearByPlacesList)
    {
        for(int i=0;i<nearByPlacesList.size();i++)
        {
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String,String> googleNearByPlaces = nearByPlacesList.get(i);
            String nameofPlace = googleNearByPlaces.get("place_name");
            String vicinity = googleNearByPlaces.get("vicinity");
            double lat = Double.parseDouble(googleNearByPlaces.get("lat"));
            double lng = Double.parseDouble(googleNearByPlaces.get("lng"));

            LatLng latLng = new LatLng(lat,lng);

            markerOptions.position(latLng);
            markerOptions.title(nameofPlace + " : " + vicinity);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE ));
            map.addMarker(markerOptions);

            map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            map.moveCamera(CameraUpdateFactory.zoomTo(14));

        }
    }

}
