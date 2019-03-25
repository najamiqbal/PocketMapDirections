package com.example.pocketmapdirections;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.pocketmapdirections.modules.DirectionFinder;
import com.example.pocketmapdirections.modules.DirectionFinderListener;
import com.example.pocketmapdirections.modules.Route;
import com.example.pocketmapdirections.utils.CheckInterNet;
import com.example.pocketmapdirections.utils.DatabaseHelper;
import com.example.pocketmapdirections.utils.MarshMallowPermission;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, DirectionFinderListener, NavigationView.OnNavigationItemSelectedListener {
    private GoogleMap mMap;
    private Button btnFindPath;
    ImageView IVdistancve,IVtime;
    TextView tv_distance,tv_time;
    StringBuilder sdate = new StringBuilder();
    StringBuilder stime = new StringBuilder();
    public static String date;
    public static String time;
    public static int Year;
    public static int month;
    public static int day;
    public static int mint;
    public static int hour;
    ImageButton btnNavigate, btnSave;
    private SimpleDateFormat mSimpleDateFormat;
    private Calendar mCalendar;
    private Activity mActivity;
    ImageView navImg,voiceSearch,voiceSearch2;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarker = new ArrayList<>();
    private List<Polyline> polyLinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    private GoogleApiClient googleApiClient;
    private static final int LOCATION_REQUEST_CODE = 101;
    String startpoint = "";
    String endpoint = "";
    String endPointIntent = "";
    MarshMallowPermission marshMallowPermission;
    DatabaseHelper db;
    AutoCompleteTextView mSearchText, mSearchText2;
    PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    FloatingActionButton fab1, fab3, fab2, fab4;
    Boolean isFABOpen = false;
    private boolean showTraffic = false;
    String LocationName = "";
    private boolean Flag_voice = false;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        marshMallowPermission = new MarshMallowPermission(MapsActivity.this);
        if (!marshMallowPermission.checkLocationPermission()) {
            marshMallowPermission.requestPermissionForLocation();
        }
        mSearchText = (AutoCompleteTextView) findViewById(R.id.input_search);
        mSearchText2 = (AutoCompleteTextView) findViewById(R.id.input_search2);
        btnFindPath = (Button) findViewById(R.id.buttonFindPath);
        IVdistancve=findViewById(R.id.IV_distance);
        IVtime=findViewById(R.id.IV_time);
        tv_distance=findViewById(R.id.textViewDistance);
        tv_time=findViewById(R.id.textViewTime);
        navImg = findViewById(R.id.ic_magnify);
        btnSave = findViewById(R.id.btnsave);
        voiceSearch=findViewById(R.id.ic_search_voice);
        voiceSearch2=findViewById(R.id.ic_search_voice2);
        mActivity = this;
        db = new DatabaseHelper(this);
        mSimpleDateFormat = new SimpleDateFormat("MM/dd/yyyy ", Locale.getDefault());
        btnNavigate = findViewById(R.id.navigatebtn);
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!startpoint.isEmpty() && !endpoint.isEmpty()) {
                    mCalendar = Calendar.getInstance();
                    new DatePickerDialog(mActivity, mDateDataSet, mCalendar.get(Calendar.YEAR),
                            mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH)).show();
                } else {
                    Toast.makeText(MapsActivity.this, "Some Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTraffic = !showTraffic;
                if (showTraffic) {
                    mMap.setTrafficEnabled(true);
                } else {
                    mMap.setTrafficEnabled(false);
                }
            }
        });
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this, MainActivity.class);
                closeFABMenu();
                startActivity(intent);
            }
        });
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!startpoint.isEmpty() && !endpoint.isEmpty()) {
                    String url = "http://maps.google.com/maps?saddr=" + startpoint.replaceAll("\\s+", "%20") + "&daddr=" + endpoint.replaceAll("\\s+", "%20");
                    //url.replaceAll("\\s+","");

                    Log.d("", "URL OF " + url);
                    closeFABMenu();
                    Log.d("Start", "End" + startpoint + "\n" + endpoint);
                    Intent email = new Intent(Intent.ACTION_SEND);
                    email.putExtra(Intent.EXTRA_TEXT, "I am Travling on this Route" + "\nFrom  " + startpoint + "\nTo " + endpoint + "\nVisit this link to View the Route\n" + url);
                    email.setType("text/plain");
                    startActivity(Intent.createChooser(email, "Choose an Email client :"));
                } else {
                    Toast.makeText(MapsActivity.this, "Please add Origin and Destination", Toast.LENGTH_SHORT).show();
                }
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFABOpen) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
            }
        });
        btnNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String location = mSearchText2.getText().toString();
                if (!location.isEmpty()) {
                    Uri navigationIntentUri = Uri.parse("google.navigation:q=" + location);//creating intent with latlng
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, navigationIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");

                    try {
                        startActivity(mapIntent);
                    } catch (ActivityNotFoundException ex) {
                        Toast.makeText(MapsActivity.this, "Please install a maps application", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(MapsActivity.this, "Please enter destination", Toast.LENGTH_SHORT).show();
                }

            }
        });
        btnFindPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
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

        mSearchText.setAdapter(mPlaceAutocompleteAdapter);
        mSearchText2.setAdapter(mPlaceAutocompleteAdapter);

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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

        voiceSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Flag_voice=!Flag_voice;
                GetText();
            }
        });
        voiceSearch2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetText();
            }
        });
    }

    private void GetText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
        try{
            startActivityForResult(intent,200);
        }catch (ActivityNotFoundException a){
            Toast.makeText(getApplicationContext(),"Intent problem", Toast.LENGTH_SHORT).show();
        }
    }

    private void showFABMenu() {
        isFABOpen = true;
        fab1.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        fab3.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
        fab2.animate().translationY(-getResources().getDimension(R.dimen.standard_155));
        //fab4.animate().translationY(-getResources().getDimension(R.dimen.standard_205));
    }

    private void closeFABMenu() {
        isFABOpen = false;
        fab1.animate().translationY(0);
        fab2.animate().translationY(0);
        fab3.animate().translationY(0);
        //fab4.animate().translationY(0);
    }

    private void sendRequest() {
        String origin = mSearchText.getText().toString();
        String destination = mSearchText2.getText().toString();

        if (origin.isEmpty()) {
            if (!LocationName.isEmpty()) {
                origin = LocationName.toString();
                Log.d("", "CurrentLocation" + origin);
            } else {
                Toast.makeText(this, "Please enter origin!", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if (destination.isEmpty()) {
            Toast.makeText(this, "Please enter destination!", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            new DirectionFinder(this, origin, destination).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }

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
        CheckInterNet checkInterNet = new CheckInterNet(this);
        if (checkInterNet.isNetworkAvailable()) {
            checkLocationandAddToMap();
            checkIntentData();
        }else {
            ViewDialog alert = new ViewDialog();
            alert.showDialog(MapsActivity.this);
        }
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
/*
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                    checkLocationandAddToMap();
                } else
                    Toast.makeText(this, "Location Permission Denied", Toast.LENGTH_SHORT).show();
                break;
        }
    }*/

    private void checkLocationandAddToMap() {
        //Checking if the user has granted the permission
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
            //Requesting the Location permission
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION
            }, LOCATION_REQUEST_CODE);
            return;
        }
        //Fetching the last known location using the Fus
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location!=null){
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            //MarkerOptions are used to create a new Marker.You can specify location, title etc with MarkerOptions
            MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("You are Here");
            //Adding the created the marker on the map
            mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                LocationName = addresses.get(0).getAddressLine(0);
                Log.d("location name", "YES" + LocationName);
            } catch (IOException e) {
                Log.d("locationNot", "NOTTT" + LocationName);
                e.printStackTrace();
            }
        }else {
            Toast.makeText(mActivity, "Please Enable Location", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait", "Finding direction", true);
        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }
        if (destinationMarker != null) {
            for (Marker marker : destinationMarker) {
                marker.remove();
            }
        }
        if (polyLinePaths != null) {
            for (Polyline polylinePath : polyLinePaths) {
                polylinePath.remove();
            }
        }

    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        btnFindPath.setVisibility(View.GONE);
        btnNavigate.setVisibility(View.VISIBLE);
        btnSave.setVisibility(View.VISIBLE);
        IVdistancve.setVisibility(View.VISIBLE);
        IVtime.setVisibility(View.VISIBLE);
        tv_distance.setVisibility(View.VISIBLE);
        tv_time.setVisibility(View.VISIBLE);
        polyLinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarker = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            ((TextView) findViewById(R.id.textViewDistance)).setText(route.distance.text);
            ((TextView) findViewById(R.id.textViewTime)).setText(route.duration.text);
            startpoint = route.startAddress;
            endpoint = route.endAddress;

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                    .title(route.startAddress)
                    .position(route.startLocation)));

            destinationMarker.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions()
                    .geodesic(true)
                    .color(Color.BLUE)
                    .width(10);

            for (int i = 0; i < route.points.size(); i++) {
                polylineOptions.add(route.points.get(i));
            }

            polyLinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_weather) {
            Intent intent = new Intent(MapsActivity.this, MainActivity.class);
            startActivity(intent);

            // Handle the camera action
        } else if (id == R.id.nav_favorites) {
            Intent intent = new Intent(MapsActivity.this, FavoritePlaces.class);
            startActivity(intent);

        } else if (id == R.id.nav_planed) {
            Intent intent = new Intent(MapsActivity.this, PlanedRoute.class);
            startActivity(intent);

        } else if (id == R.id.nav_nearbyplaces) {
            Intent intent=new Intent(MapsActivity.this,NearbyPlacesAcitivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_speedo) {
            Intent intent=new Intent(MapsActivity.this,SpeedoMeter.class);
            startActivity(intent);

        } else if (id == R.id.nav_compass) {
            Intent intent=new Intent(MapsActivity.this,CampassActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_streetView) {
            Intent intent=new Intent(MapsActivity.this,StreetViewActivity.class);
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (btnNavigate.getVisibility() == View.VISIBLE) {
                Intent intent = new Intent(MapsActivity.this, MapsActivity.class);
                startActivity(intent);
                finish();

            } else {
                if (isFABOpen) {
                    closeFABMenu();
                } else {
                    super.onBackPressed();
                }
            }
        }
    }

    private void checkIntentData() {
        if (getIntent().getStringExtra("location") != null) {
            endPointIntent = getIntent().getStringExtra("location");
            Log.d("MapsActivityis", "endPointis" + endPointIntent);
            mSearchText2.setText(endPointIntent);
        }
        if (getIntent().getStringExtra("endpoint") != null) {
            endPointIntent = getIntent().getStringExtra("endpoint");
            Log.d("MapsActivityis", "endPointis" + endPointIntent);
            mSearchText2.setText(endPointIntent);
            sendRequest();
        }
    }


    /* After user decided on a date, store those in our calendar variable and then start the TimePickerDialog immediately */
    private final DatePickerDialog.OnDateSetListener mDateDataSet = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, monthOfYear);
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            Year=year;
            month=monthOfYear;
            day=dayOfMonth;
            sdate.append(year);
            sdate.append('-');
            sdate.append(monthOfYear + 1);
            sdate.append('-');
            sdate.append(dayOfMonth);
            date = sdate.toString();
            Log.d("MapsActivity", "Date and Time " + date);
            new TimePickerDialog(mActivity, mTimeDataSet, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), false).show();
        }
    };

    /* After user decided on a time, save them into our calendar instance, and now parse what our calendar has into the TextView */
    private final TimePickerDialog.OnTimeSetListener mTimeDataSet = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            mCalendar.set(Calendar.MINUTE, minute);
            mint=minute;
            hour=hourOfDay;
            stime.append(hourOfDay);
            stime.append(":");
            stime.append(minute);
            time = stime.toString();
            if (!date.isEmpty() && !time.isEmpty()) {
                SaveRoute(date, time, startpoint, endpoint);
            } else {
                Toast.makeText(mActivity, "Some Error", Toast.LENGTH_SHORT).show();
            }
            Log.d("MapsActivity", "Date and Time " + time);
        }
    };

    private void SaveRoute(String date, String time, String startpoint, String endpoint) {
        // Toast.makeText(this, ""+date+time+"\n"+startpoint
        //       +"\n"+endpoint, Toast.LENGTH_SHORT).show();
        db.insertRoute(startpoint, endpoint, date, time);
        Calendar cal = Calendar.getInstance();
        cal.set(Year, month, day, hour, mint);
        setAlarm(cal);
        Toast.makeText(this, "Route Is Save", Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

    private void setAlarm(Calendar target){
        Intent intent = new Intent(getBaseContext(), ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(), 100, intent, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, target.getTimeInMillis(), pendingIntent);
        Log.d("okDaone","okok");

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MarshMallowPermission.EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE_BY_LOAD_PROFILE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkLocationandAddToMap();
                    //permission granted successfully

                } else {
                    //permission denied
                }
                break;
        }
    }

    public class ViewDialog {

        public void showDialog(Activity activity) {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.custom_dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            FrameLayout mDialogNo = dialog.findViewById(R.id.frmNo);
            mDialogNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                }
            });

            FrameLayout mDialogOk = dialog.findViewById(R.id.frmOk);
            mDialogOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                    finish();
                }
            });

            dialog.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 200){
            if(resultCode == RESULT_OK && data != null){
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (Flag_voice){
                    mSearchText.setText(result.get(0));
                    Flag_voice=!Flag_voice;
                }else {
                    mSearchText2.setText(result.get(0));
                }

            }else {
                if (Flag_voice==true){
                    Flag_voice=false;
                }
                //Toast.makeText(mActivity, "NULL", Toast.LENGTH_SHORT).show();
            }
        }
    }
}