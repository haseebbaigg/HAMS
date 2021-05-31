package com.example.text_recognition;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class dataParser {
    HashMap<String,String> getSingleNearbyPlace(JSONObject googlePlaceJSON)
    {
        HashMap<String,String> googlePlaceMap = new HashMap<>();
        String nameOfPlace = "-NA-";
        String vicinity = "-NA-";
        String latitude = "";
        String longtitude = "";
        String reference = "";

        try {
            if(!googlePlaceJSON.isNull("name")) {
                nameOfPlace = googlePlaceJSON.getString("name");
            }
            if(!googlePlaceJSON.isNull("vicinity")) {
                vicinity = googlePlaceJSON.getString("vicinity");
            }
            latitude = googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longtitude = googlePlaceJSON.getJSONObject("geometry").getJSONObject("location").getString("lng");
            reference = googlePlaceJSON.getString("reference");

            googlePlaceMap.put("place_name",nameOfPlace);
            googlePlaceMap.put("vicinity",vicinity);
            googlePlaceMap.put("lat",latitude);
            googlePlaceMap.put("lng",longtitude);
            googlePlaceMap.put("ref",reference);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return googlePlaceMap;
    }



    public List<HashMap<String,String>> getAllNearbyPlaces(JSONArray jsonArray)
    {
        int counter = jsonArray.length();

        List<HashMap<String,String>> nearByPlacesList = new ArrayList<>();

        HashMap<String,String> nearByPlaceMap = null;

        for(int i=0;i<counter;i++)
        {
            try {
                nearByPlaceMap = getSingleNearbyPlace((JSONObject) jsonArray.get(i));

                nearByPlacesList.add(nearByPlaceMap);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return nearByPlacesList;
    }

    public List<HashMap<String,String>> parse(String jsonData)
    {
        JSONArray jsonArray = null;
        JSONObject jsonObject;

        try {
            jsonObject = new JSONObject(jsonData);
            jsonArray = jsonObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getAllNearbyPlaces(jsonArray);
    }
}
