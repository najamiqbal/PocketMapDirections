package com.example.pocketmapdirections;

import android.Manifest;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;
import com.google.android.gms.maps.model.StreetViewPanoramaOrientation;
import com.google.android.gms.maps.model.StreetViewSource;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class StreetViewActivity extends AppCompatActivity
        implements OnStreetViewPanoramaReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private StreetViewPanorama mStreetViewPanorama;
    Toolbar m_toolbar;
    private boolean secondLocation = false;
    ImageView search;
    AutoCompleteTextView mSearchText2;
    String LocationName = "";
    Double lat;
    Double lng;
    PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private GoogleApiClient googleApiClient;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.street_view_activity);
        mSearchText2 = (AutoCompleteTextView) findViewById(R.id.et_search);
        m_toolbar = findViewById(R.id.toolbar);
        m_toolbar.setTitle("Street View");
        setSupportActionBar(m_toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SupportStreetViewPanoramaFragment streetViewFragment =
                (SupportStreetViewPanoramaFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.googleMapStreetView);
        streetViewFragment.getStreetViewPanoramaAsync(this);
        search = findViewById(R.id.ic_search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mSearchText2.getText().toString().isEmpty()) {
                    LatLng address = getLocationFromAddress(StreetViewActivity.this, mSearchText2.getText().toString());
                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(StreetViewActivity.this, Locale.getDefault());

                    try {
                        addresses = geocoder.getFromLocation(address.latitude, address.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                        LocationName = addresses.get(0).getAddressLine(0);
                        lat = address.latitude;
                        lng = address.longitude;
                        //streetViewPanorama.setPosition(new LatLng(lat, lng));

                        Log.d("location name", "YES" + LocationName);
                    } catch (IOException e) {
                        Log.d("locationNot", "NOTTT" + LocationName);
                        e.printStackTrace();
                    }
                    onStreetViewPanoramaReady(mStreetViewPanorama);

                } else {
                    Toast.makeText(StreetViewActivity.this, "Enter Location", Toast.LENGTH_SHORT).show();
                }
            }
        });
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .enableAutoManage(this, this)
                .build();

        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this, googleApiClient,
                LAT_LNG_BOUNDS, null);

        mSearchText2.setAdapter(mPlaceAutocompleteAdapter);

        mSearchText2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {

                    //execute our method for searching

                }

                return false;
            }
        });

    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
        mStreetViewPanorama = streetViewPanorama;
        if (lat != null && lng != null) {
            streetViewPanorama.setPosition(new LatLng(lat, lng));
        } else {
            streetViewPanorama.setPosition(new LatLng(32.5161797, 74.5564174));
        }

        streetViewPanorama.setStreetNamesEnabled(true);
        streetViewPanorama.setPanningGesturesEnabled(true);
        streetViewPanorama.setZoomGesturesEnabled(true);
        streetViewPanorama.setUserNavigationEnabled(true);
        streetViewPanorama.animateTo(
                new StreetViewPanoramaCamera.Builder().
                        orientation(new StreetViewPanoramaOrientation(20, 20))
                        .zoom(streetViewPanorama.getPanoramaCamera().zoom)
                        .build(), 2000);

        streetViewPanorama.setOnStreetViewPanoramaChangeListener(panoramaChangeListener);

    }

    private StreetViewPanorama.OnStreetViewPanoramaChangeListener panoramaChangeListener =
            new StreetViewPanorama.OnStreetViewPanoramaChangeListener() {
                @Override
                public void onStreetViewPanoramaChange(
                        StreetViewPanoramaLocation streetViewPanoramaLocation) {


             //       Toast.makeText(getApplicationContext(), "Lat: " + streetViewPanoramaLocation.position.latitude + " Lng: " + streetViewPanoramaLocation.position.longitude, Toast.LENGTH_SHORT).show();

                }
            };

    public LatLng getLocationFromAddress(Context context, String strAddress) {
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return p1;

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //menu item selected
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}