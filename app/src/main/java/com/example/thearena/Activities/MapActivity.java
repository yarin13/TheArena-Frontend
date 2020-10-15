package com.example.thearena.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.example.thearena.Classes.Authentication;
import com.example.thearena.Classes.User;
import com.example.thearena.Data.InnerDatabaseHandler;
import com.example.thearena.Interfaces.IAsyncResponse;
import com.example.thearena.R;
import com.example.thearena.Utils.Constants;
import com.example.thearena.Utils.Preferences;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnMarkerClickListener, NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MAP";
    private IAsyncResponse iAsyncResponse;
    Location lastCurrentLocation;
    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationClient;
    private Boolean LocationPermissionGranted;
    private String currentUserEmail;
    private Boolean isLoggedIn;
    public InnerDatabaseHandler innerDatabaseHandler = new InnerDatabaseHandler(MapActivity.this);
    private final HashMap<String, User> extraMarkerInfo = new HashMap<>();
    private int threadRequestTiming = 1000 * 60 * 3;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView drawerProfilePic;
    private TextView loggedInUserFullName;

    private NavigationView selectedUserNavigationView;
    private ActionBarDrawerToggle selectedUserActionBarDrawerToggle;
    private ImageView selectedUserProfilePic;
    private TextView selectedUserFullName;
    private User selectedUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        isLoggedIn = true;

        //Find view's by Id:

        selectedUserNavigationView = findViewById(R.id.secNavigationView);
        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigationView);

        FloatingActionButton floatingActionButton = findViewById(R.id.mapFloatingButton);
        floatingActionButton.setAlpha(0.50f);
        floatingActionButton.setOnClickListener(view -> drawerLayout.openDrawer(Gravity.RIGHT));

        //setActionBarsDrawable - setting all action bars related stuff:
        setActionBarsDrawable();

        //Location related stuff:

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);
        getLocationPermission();

        currentUserEmail = Preferences.getMail(getBaseContext());
        if (currentUserEmail.equals("")) {
            currentUserEmail = this.innerDatabaseHandler.getUserEmail();
        }

        iAsyncResponse = new IAsyncResponse() {
            @Override
            public <T> void processFinished(T response) {
                /*
                This processFinished method is a callback function...
                Once she had a response it convert it to Map Marker.
                for now we do not need userArrayList - but maybe in the future we will need to use it to grab images from the server.
                 */
                if (!response.equals("[{\"Error\":\"No one else was found\"}]") && !response.equals("")) {
                    try {
                        JSONArray jsonArray = new JSONArray(response.toString());
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        JSONArray keys = jsonObject.names();
                        extraMarkerInfo.clear();
                        for (int i = 0; i < keys.length(); i++) {
                            User user = new User();
                            String id = keys.getString(i);
                            JSONArray value = jsonObject.getJSONArray(id);
                            user.setUserId(Integer.parseInt(id));
                            user.setFirstName(value.getString(0));
                            user.setLastName(value.getString(1));
                            user.setUserEmail(value.getString(2));
                            user.setUserPhoneNumber(value.getString(3));
                            user.setUserAge(value.getInt(4));
                            user.setUserLatitude(value.getDouble(5));
                            user.setUserLongitude(value.getDouble(6));

                            createMarker(map, user);
                        }

                    } catch (Exception e) {
                        Log.d(TAG, "processFinished: " + e);
                    }

                } else
                    Toast.makeText(MapActivity.this, "look like there in no one here", Toast.LENGTH_LONG).show();
            }
        };

        new Thread(() -> {
            /*
            This is a Thread that run all the life of the application,
            during his running his send the current location under 2 circumstance :
                1. currentUserEmail must not be null or empty String.
                2. isLoggedIn must be true - when using the onStop method: isLoggedIn become false, ans prevent this Thread to run endlessly.
             */
            while (true) {
                try {
                    if (isLoggedIn && !currentUserEmail.equals("")) {
                        //Thread.sleep(threadRequestTiming); --------------> Wait 5 minutes between every update request.
                        Thread.sleep(10000);
                        Authentication.sendLocation(MapActivity.this, currentUserEmail, lastCurrentLocation, iAsyncResponse);
                    } else if (currentUserEmail.equals("")) {
                        currentUserEmail = Preferences.getMail(getApplicationContext());
                        break;
                    }
                    //remove the break for thread to run forever
                    //break;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void openWhatsApp(){
        String url = "https://api.whatsapp.com/send?phone="+selectedUser.getUserPhoneNumber();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    private void setActionBarsDrawable() {
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, null, R.string.open, R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                drawerProfilePic = findViewById(R.id.drawerHeaderProfilePic);
                loggedInUserFullName = findViewById(R.id.drawerHeaderUserName);
                GlideUrl glideUrl = new GlideUrl(Constants.PHOTOS_URL, new LazyHeaders.Builder()
                        .addHeader("action", "getProfilePhoto")
                        .addHeader("userId", String.valueOf(Preferences.getUserId(getApplicationContext())))
                        .build());
                Glide.with(MapActivity.this).load(glideUrl).into(drawerProfilePic);
            }
        };
        selectedUserActionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, null, R.string.open, R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                selectedUserProfilePic = findViewById(R.id.sec_drawerHeaderProfilePic);
                selectedUserFullName = findViewById(R.id.sec_drawerHeaderUserName);
                selectedUserFullName.setText(selectedUser.getFirstName() + " "+ selectedUser.getLastName());
                GlideUrl glideUrl = new GlideUrl(Constants.PHOTOS_URL, new LazyHeaders.Builder()
                        .addHeader("action", "getProfilePhoto")
                        .addHeader("userId", String.valueOf(selectedUser.getUserId()))
                        .build());
                Glide.with(MapActivity.this).load(glideUrl).into(selectedUserProfilePic);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();
        selectedUserNavigationView.setNavigationItemSelectedListener(this);
        navigationView.setNavigationItemSelectedListener(this);
    }


    private void createMarker(GoogleMap map, User user) {
        Marker marker = map.addMarker(new MarkerOptions()
                .position(new LatLng(user.getUserLatitude(), user.getUserLongitude()))
                .title(user.getUserEmail()));

        if (user.getProfilePic() != null) {
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(user.getProfilePic()));
        }else{
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        }
        extraMarkerInfo.put(marker.getId(), user);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        selectedUser = extraMarkerInfo.get(marker.getId());
        openWhatsApp();
        return false;
    }

    private void getLocationPermission() {
        /*
          in this function we create an array of the permissions we want(same permission we added in the manifest)
          we send this array along with "Constants.LOCATION_PERMISSION_REQUEST_CODE" using built in function(requestPermissions) that prompts the user
          if the permissions already granted we initializing the map, if not the prompt will be shown and when the user answer to the prompt("ok" or "deny")
          the "onRequestPermissionsResult()" will be called by the activity
         */
        String[] permissions = {Constants.FINE_LOCATION, Constants.COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this, Constants.FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Constants.COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            LocationPermissionGranted = true;
            initMap();
        } else
            ActivityCompat.requestPermissions(this, permissions, Constants.LOCATION_PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        /*
              this function called by google activity(its a google's listener) after the user answers the prompt
              we recognize if its coming from the prompt if the "requestCode" variable equals to "Constants.LOCATION_PERMISSION_REQUEST_CODE" from earlier
              we are looping the "grantResult" array to check if all the permissions we asked are granted
              we initialize the map if we have all the permissions
        */
        if (requestCode == Constants.LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        LocationPermissionGranted = false;
                        return;
                    }
                }
                LocationPermissionGranted = true;

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, location -> {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                Log.d("latlng", "Last known location in lon: " + location.getLongitude() + ", lat: " + location.getLatitude());
                            }
                        });
                //locationManager.requestLocationUpdates(provider, 4000, 1, this);
                initMap();
            }
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        //this function is called automatically by the locationManager.getLocationUpdates according to the time we set it to be called
        onMapReady(map);
    }

    private void moveCamera(LatLng latLng) {
        // to make the map look on the current location
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, Constants.DEFAULT_ZOOM));
    }

    public void initMap() {
        // this function is using a built in function ("getMapAsync()") that calls to "onMapReady" function when the map is ready
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.MapId);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (LocationPermissionGranted) {
            getDeviceLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            map.setMyLocationEnabled(true);
            new Thread(() -> Authentication.sendLocation(MapActivity.this, currentUserEmail, lastCurrentLocation, null)).start();
            googleMap.setOnMarkerClickListener(this);
            googleMap.setOnMapClickListener(latLng -> selectedUserNavigationView.setVisibility(View.GONE));

        } else
            getLocationPermission();

    }


    private void getDeviceLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        lastCurrentLocation = location;
                        moveCamera(new LatLng(location.getLatitude(), lastCurrentLocation.getLongitude()));
                    } else {
                        Toast.makeText(MapActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onStop() {
        isLoggedIn = false;
        Authentication.logoff(MapActivity.this, currentUserEmail);
        super.onStop();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logout:
                //do something..
                return true;
            case R.id.menu_profile:
                //do something..
                return true;

        }
        return true;
    }


    //========================================================================================================================================
//private void getProfilePic(User user, int userId) {
//    Retrofit retrofit = NetworkClient.getRetrofit();
//    UploadApis uploadApis = retrofit.create(UploadApis.class);
//    Call<ResponseBody> call = uploadApis.getImage("getProfilePhoto", userId);
//    call.enqueue(new Callback<ResponseBody>() {
//        @Override
//        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//            if (response.isSuccessful()) {
//                if (response.body() != null) {
////                        ResponseBody in = response.body();
////                        InputStream inputStream = in.byteStream();
////                        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
////                        Bitmap bmp=BitmapFactory.decodeStream(bufferedInputStream);
//
//                    // display the image data in a ImageView or save it
//                    Bitmap bmp = BitmapFactory.decodeStream(response.body().byteStream());
//                    user.setProfilePic(bmp);
//                    Log.d("Testingg", "getProfilePic bmp: " + bmp);
//                    createMenu(map, user);
//                } else {
//                    // TODO
//                }
//            } else {
//                // TODO
//            }
//        }
//
//        @Override
//        public void onFailure(Call<ResponseBody> call, Throwable t) {
//            // TODO
//        }
//    });
//}

//========================================================================================================================================
    //        menuBtn.setOnClickListener(new View.OnClickListener() {
//            @SuppressLint("UseCompatLoadingForDrawables")
//            @Override
//            public void onClick(View view) {
//                if (menuBtn.getContentDescription().equals("menu")) {
//                    Snackbar.make(view, "Opening....", Snackbar.LENGTH_LONG)
//                            .setBackgroundTint(getColor(R.color.colorPrimary))
//                            .setAction("Action", null).show();
//
//                    menuBtn.setContentDescription("close");
//                    menuBtn.setImageDrawable(getDrawable(R.drawable.baseline_close_24));
//                }else {
//                    Snackbar.make(view, "closing....", Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();
//
//                    menuBtn.setContentDescription("menu");
//                    menuBtn.setImageDrawable(getDrawable(R.drawable.baseline_menu_24));
//
//                }
//            }
//        });

}