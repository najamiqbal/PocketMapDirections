package com.example.pocketmapdirections;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pocketmapdirections.utils.DatabaseHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class SavePlaceMapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private GoogleMap mMap;
    private Button btnSavePlace;
    private ProgressDialog progressDialog;
    private GoogleApiClient googleApiClient;
    private static final int LOCATION_REQUEST_CODE = 101;
    DatabaseHelper db;
    String startpoint="";
    String endpoint="";
    Double latitude=0.0;
    Double longitude=0.0;
    GoogleApiClient mGoogleApiClient;
    String addressString="";
    String LocationName="";
    private Marker mPreviousMarker ;
    LocationRequest mLocationRequest;
    Location mylocation;
    Toolbar m_toolbar;
    ImageView search;
    AutoCompleteTextView mSearchText2;
    PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.save_place_map_activity);
        m_toolbar = findViewById(R.id.toolbar);
        m_toolbar.setTitle("History");
        setSupportActionBar(m_toolbar);
        db = new DatabaseHelper(this);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mSearchText2 = (AutoCompleteTextView) findViewById(R.id.input_search2);
        btnSavePlace=findViewById(R.id.saveplacebtn);
        btnSavePlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!LocationName.isEmpty()){
                    ShowDialog();
                }else {
                    Toast.makeText(SavePlaceMapActivity.this, "Some Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
        search=findViewById(R.id.ic_magnify2);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mSearchText2.getText().toString().isEmpty()) {
                    LatLng address = getLocationFromAddress(SavePlaceMapActivity.this, mSearchText2.getText().toString());
                    Geocoder geocoder;
                    List<Address> addresses;
                    geocoder = new Geocoder(SavePlaceMapActivity.this, Locale.getDefault());

                    try {
                        addresses = geocoder.getFromLocation(address.latitude, address.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                        LocationName = addresses.get(0).getAddressLine(0);
                        Log.d("location name","YES"+LocationName);
                    } catch (IOException e) {
                        Log.d("locationNot","NOTTT"+LocationName);
                        e.printStackTrace();
                    }
                    if (mPreviousMarker!=null){
                        mPreviousMarker.remove();
                    }
                    mPreviousMarker=mMap.addMarker(new MarkerOptions().position(address).title("You are here"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(address));
                }else {
                    Toast.makeText(SavePlaceMapActivity.this, "Enter Location", Toast.LENGTH_SHORT).show();
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
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){

                    //execute our method for searching

                }

                return false;
            }
        });
    }

    private void ShowDialog() {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_subtitle_save_location, null);

        final EditText editText = (EditText) dialogView.findViewById(R.id.edt_comment);
        Button button1 = (Button) dialogView.findViewById(R.id.buttonSubmit);
        Button button2 = (Button) dialogView.findViewById(R.id.buttonCancel);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
            }
        });
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // DO SOMETHINGS
               if (!editText.getText().toString().isEmpty()){
                   SaveLocation(LocationName,editText.getText().toString());
                   dialogBuilder.dismiss();
               }else {
                   Toast.makeText(SavePlaceMapActivity.this, "Please enter title", Toast.LENGTH_SHORT).show();
               }
            }
        });

        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    private void SaveLocation(String locationName, String subtitle) {
        // inserting note in db and getting
        // newly inserted note id
        db.insertNote(locationName,subtitle);

    }
    public LatLng getLocationFromAddress(Context context, String strAddress)
    {
        Geocoder coder= new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try
        {
            address = coder.getFromLocationName(strAddress, 5);
            if(address==null)
            {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return p1;

    }
    public void onStart() {
        super.onStart();
        googleApiClient.connect();
    }
    public void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        checkLocationandAddToMap();

    }
    @Override
    public void onConnected(Bundle bundle) {
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                    checkLocationandAddToMap();
                } else
                    Toast.makeText(this, "Location Permission Denied", Toast.LENGTH_SHORT).show();
                break;
        }
    }
    private void checkLocationandAddToMap() {
        //Checking if the user has granted the permission
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
            //Requesting the Location permission
            ActivityCompat.requestPermissions(this, new String[] {
                    android.Manifest.permission.ACCESS_FINE_LOCATION
            }, LOCATION_REQUEST_CODE);
            return;
        }
        //Fetching the last known location using the Fus
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        //MarkerOptions are used to create a new Marker.You can specify location, title etc with MarkerOptions
        MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("You are Here");
        //Adding the created the marker on the map
        if (mPreviousMarker!=null){
            mPreviousMarker.remove();
        }
        mPreviousMarker=mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            LocationName = addresses.get(0).getAddressLine(0);
            Log.d("location name","YES"+LocationName);
        } catch (IOException e) {
            Log.d("locationNot","NOTTT"+LocationName);
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
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
