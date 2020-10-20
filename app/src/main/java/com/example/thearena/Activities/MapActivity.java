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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.example.thearena.Classes.Authentication;
import com.example.thearena.Classes.Question;
import com.example.thearena.Classes.User;
import com.example.thearena.Data.InnerDatabaseHandler;
import com.example.thearena.Fragments.ImageUploadFragment;
import com.example.thearena.Fragments.LoginPage;
import com.example.thearena.Interfaces.IAsyncResponse;
import com.example.thearena.R;
import com.example.thearena.UI.RecyclerViewAdapter;
import com.example.thearena.UI.RecyclerViewImageAdapter;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnMarkerClickListener, NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MAP";
    private IAsyncResponse iAsyncResponse;
    Location lastCurrentLocation;
    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationClient;
    private Boolean LocationPermissionGranted;
    private String currentUserEmail;
    private Boolean isLoggedIn;
    private IAsyncResponse currentLoggedInResponse;
    private User currentLoggedInUser;
    public InnerDatabaseHandler innerDatabaseHandler = new InnerDatabaseHandler(MapActivity.this);
    private final HashMap<String, User> extraMarkerInfo = new HashMap<>();
    private int threadRequestTiming = 1000 * 60 * 3;
    private int timeout = 3000;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView drawerProfilePic;
    private TextView loggedInUserFullName;
    private TextView loggedInUserAge;

    private NavigationView selectedUserNavigationView;
    private ImageView selectedUserProfilePic;
    private TextView selectedUserFirstName;
    private TextView selectedUserLastName;
    private TextView selectedUserage;
    private JSONArray selectedUserPhotosId;
    private Button whatsAppBtn;
    private Button blockBtn;
    private User selectedUser;
    Context context;
    private RecyclerView recyclerView;
    private RecyclerViewImageAdapter recyclerViewImageAdapter;
    private RecyclerView.LayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        isLoggedIn = true;
        currentLoggedInUser = new User();
        //Find view's by Id:
        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigationView);
        selectedUserNavigationView = findViewById(R.id.secNavigationView);

        FloatingActionButton floatingActionButton = findViewById(R.id.mapFloatingButton);
        floatingActionButton.setAlpha(0.50f);
        floatingActionButton.setOnClickListener(view -> drawerLayout.openDrawer(Gravity.RIGHT));
        context = this;
        //setActionBarsDrawable - setting all action bars related stuff:
        setActionBarsDrawable();

        //Location related stuff:

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);
        getLocationPermission();

        currentUserEmail = Preferences.getMail(this);
//        if (currentUserEmail.equals("")) {
//            currentUserEmail = this.innerDatabaseHandler.getUserEmail();
//        }

        currentLoggedInResponse = new IAsyncResponse() {
            @Override
            public <T> void processFinished(T response) {
                try {
                    JSONObject res = new JSONObject(response.toString());
                    if (res.has("age")) {
                        currentLoggedInUser.setFirstName(res.getString("firstName"));
                        currentLoggedInUser.setLastName(res.getString("lastName"));
                        currentLoggedInUser.setUserEmail(res.getString("email"));
                        currentLoggedInUser.setUserAge(res.getInt("age"));
                        currentLoggedInUser.setUserGender(res.getString("gender"));
                        currentLoggedInUser.setUserInterestedIn(res.getString("interestedIn"));
                        currentLoggedInUser.setUserPhoneNumber(res.getString("phoneNumber"));
                    }
                } catch (Throwable t) {
                    Log.d(TAG, "processFinished: " + t.getMessage());
                }
            }
        };

        //Get LoggedIn user Info:
        Authentication.getCurrentUserInfo(this, Preferences.getUserId(this), currentLoggedInResponse);

    }


    private void setActionBarsDrawable() {
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, null, R.string.open, R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                drawerProfilePic = findViewById(R.id.drawerHeaderProfilePic);
                loggedInUserAge = findViewById(R.id.drawerHeaderUserAge);
                loggedInUserFullName = findViewById(R.id.drawerHeaderUserName);
                loggedInUserFullName.setText(currentLoggedInUser.getFirstName() + " " + currentLoggedInUser.getLastName());
                loggedInUserAge.setText(String.valueOf(currentLoggedInUser.getUserAge()));
                GlideUrl glideUrl = new GlideUrl(Constants.PHOTOS_URL, new LazyHeaders.Builder()
                        .addHeader("action", "getProfilePhoto")
                        .addHeader("userId", String.valueOf(Preferences.getUserId(getApplicationContext())))
                        .build());
                Glide.with(MapActivity.this).load(glideUrl).into(drawerProfilePic);
            }
        };
        ActionBarDrawerToggle selectedUserActionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, null, R.string.open, R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
//                recyclerView = findViewById(R.id.selected_user_recycle_view_container);
//                recyclerView.setHasFixedSize(true);
//                layoutManager = new LinearLayoutManager(getApplicationContext());
//                recyclerView.setLayoutManager(layoutManager);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        drawerLayout.addDrawerListener(selectedUserActionBarDrawerToggle);
        selectedUserActionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        selectedUserActionBarDrawerToggle.syncState();
        selectedUserNavigationView.setNavigationItemSelectedListener(this);
    }

    private void openWhatsApp() {
        String url = "https://api.whatsapp.com/send?phone=" + selectedUser.getUserPhoneNumber();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }


    private void createMarker(GoogleMap map, User user) {
        Marker marker = map.addMarker(new MarkerOptions()
                .position(new LatLng(user.getUserLatitude(), user.getUserLongitude()))
                .title(user.getUserEmail()));

        if (user.getProfilePic() != null) {
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(user.getProfilePic()));
        } else {
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        }
        extraMarkerInfo.put(marker.getId(), user);
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        selectedUser = extraMarkerInfo.get(marker.getId());
        IAsyncResponse iAsync = new IAsyncResponse() {
            @Override
            public <T> void processFinished(T response) {
                try {
                    JSONObject object = new JSONObject(response.toString());
                    selectedUserPhotosId = object.getJSONArray(selectedUser.getUserEmail());
                    ArrayList<Integer> listdata = new ArrayList<>();
                    for (int i = 0; i < selectedUserPhotosId.length(); i++) {
                        listdata.add(selectedUserPhotosId.getInt(i));
                    }
                    recyclerView = findViewById(R.id.selected_user_recycle_view_container);
                    recyclerView.setHasFixedSize(true);
                    layoutManager = new LinearLayoutManager(context);
                    recyclerView.setLayoutManager(layoutManager);

                    recyclerViewImageAdapter = new RecyclerViewImageAdapter(context,listdata);
                    recyclerView.setAdapter(recyclerViewImageAdapter);
                    recyclerViewImageAdapter.notifyDataSetChanged();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        getPhotosId(iAsync);
        if (selectedUser != null) {

            selectedUserProfilePic = findViewById(R.id.sec_drawerHeaderProfilePic);
            selectedUserFirstName = findViewById(R.id.sec_drawerHeaderUserFirstName);
            selectedUserLastName = findViewById(R.id.sec_drawerHeaderUserLastName);
            selectedUserage = findViewById(R.id.sec_drawerHeaderUserAge);
            whatsAppBtn = findViewById(R.id.selected_user_whatsApp_button);
            blockBtn = findViewById(R.id.selected_user_block_button);

            selectedUserFirstName.setText(selectedUser.getFirstName());
            selectedUserLastName.setText(selectedUser.getLastName());
            selectedUserage.setText(String.valueOf(selectedUser.getUserAge()));

            whatsAppBtn.setOnClickListener(v -> openWhatsApp());
            blockBtn.setOnClickListener(v -> Toast.makeText(this, "Block is for subscribed users only", Toast.LENGTH_LONG).show());

            GlideUrl glideUrl = new GlideUrl(Constants.PHOTOS_URL, new LazyHeaders.Builder()
                    .addHeader("action", "getProfilePhoto")
                    .addHeader("userId", String.valueOf(selectedUser.getUserId()))
                    .build());

            Glide.with(MapActivity.this)
                    .load(glideUrl)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(selectedUserProfilePic);

            drawerLayout.openDrawer(Gravity.LEFT);
        }

        return false;
    }

    private void getPhotosId(final IAsyncResponse iAsync) {
        new Thread(() -> {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.PHOTOS_URL, listener -> {
                try {
                    if (listener.contains(selectedUser.getUserEmail())) {
                        iAsync.processFinished(listener);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, error -> {
                Log.d(TAG, error.getMessage());
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("action", "getPhotosIds");
                    params.put("email", selectedUser.getUserEmail());
                    return params;
                }
            };
            requestQueue.getCache().clear();
            requestQueue.add(stringRequest);
        }).start();

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

            googleMap.setOnMarkerClickListener(this);
            googleMap.setOnMapClickListener(latLng -> selectedUserNavigationView.setVisibility(View.GONE));
            new Thread(() -> Authentication.sendLocation(MapActivity.this, currentUserEmail, lastCurrentLocation, null)).start();
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
    protected void onPostResume() {
        currentUserEmail = Preferences.getMail(this);
        isLoggedIn = true;
        new Thread(() -> Authentication.sendLocation(MapActivity.this, currentUserEmail, lastCurrentLocation, null)).start();
        getMatches();
        super.onPostResume();
    }

    @Override
    protected void onStop() {
        map.clear();
        isLoggedIn = false;
        Authentication.logoff(MapActivity.this, currentUserEmail);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        map.clear();
        isLoggedIn = false;
        Authentication.logoff(this, currentUserEmail);
        super.onDestroy();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logout:
                map.clear();
                isLoggedIn = false;
                Authentication.logoff(this, currentUserEmail);
                MainActivity mainActivity = new MainActivity();
                mainActivity.mainFragmentManager(new LoginPage());
                break;
            case R.id.menu_profile:
                //do something..
                break;
            case R.id.upload_image:
                break;
            case R.id.upload_profile_image:
//                ImageUploadFragment imageUploadFragment = new ImageUploadFragment();
//                imageUploadFragment.selectImageFromGallery();
                break;
        }
        return true;
    }

    private void getMatches() {
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
            // while (true) {
            try {
                if (isLoggedIn && !currentUserEmail.equals("")) {
                    //Thread.sleep(threadRequestTiming); --------------> Wait 5 minutes between every update request.
                    Thread.sleep(timeout);
                    Authentication.sendLocation(MapActivity.this, currentUserEmail, lastCurrentLocation, iAsyncResponse);

                } else if (currentUserEmail.equals("")) {
                    currentUserEmail = Preferences.getMail(getApplicationContext());
                    //   break;
                }
                //remove the break for thread to run forever
                //break;

            } catch (Exception e) {
                e.printStackTrace();
            }
            //  }
        }).start();
    }

}