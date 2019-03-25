package com.example.pocketmapdirections.utils;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.pocketmapdirections.NearbyPlacesAcitivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;

public class GetNearbyBanksData extends AsyncTask<Object, String, String> {
    private String googlePlacesData;
    private GoogleMap mMap;
    private String url;
    @Override
    protected String doInBackground(Object... params) {
        try {
            Log.d("GetNearbyBanksData", "doInBackground entered");
            mMap = (GoogleMap) params[0];
            url = (String) params[1];
            UrlConnection urlConnection = new UrlConnection();
            googlePlacesData = urlConnection.readUrl(url);
            Log.d("GooglePlacesReadTask", "doInBackground Exit");
        } catch (Exception e) {
            Log.d("GooglePlacesReadTask", e.toString());
        }
        return googlePlacesData;
    }
    @Override
    protected void onPostExecute(String result) {
        Log.d("GooglePlacesReadTask", "onPostExecute Entered");
        List<HashMap<String, String>> nearbyPlacesList;
        DataParser dataParser = new DataParser();
        nearbyPlacesList = dataParser.parse(result);
        ShowNearbyPlaces(nearbyPlacesList);
        Log.d("GooglePlacesReadTask", "onPostExecute Exit");
    }
    private void ShowNearbyPlaces(List<HashMap<String, String>> nearbyPlacesList) {
       if (nearbyPlacesList.size()!=0) {
           for (int i = 0; i < nearbyPlacesList.size(); i++) {
               Log.d("onPostExecute", "Entered into showing locations");
               MarkerOptions markerOptions = new MarkerOptions();
               HashMap<String, String> googlePlace = nearbyPlacesList.get(i);
               double lat = Double.parseDouble(googlePlace.get("lat"));
               double lng = Double.parseDouble(googlePlace.get("lng"));
               String placeName = googlePlace.get("place_name");
               String vicinity = googlePlace.get("vicinity");
               LatLng latLng = new LatLng(lat, lng);
               markerOptions.position(latLng);
               markerOptions.title(placeName);
               mMap.addMarker(markerOptions);
               markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//move map camera
               mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
               mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
           }

       }
    }
}
