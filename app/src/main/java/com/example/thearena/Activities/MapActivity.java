package com.example.thearena.Activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.thearena.Classes.Authentication;
import com.example.thearena.Classes.User;
import com.example.thearena.Interfaces.IAsyncResponse;
import com.example.thearena.R;
import com.example.thearena.Utils.Constants;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {
    private IAsyncResponse iAsyncResponse;
    Location lastCurrentLocation;

    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationManager locationManager;
    private String provider;
    private Boolean LocationPermissionGranted;
    private User currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        getLocationPermission();


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
              we are looping the "grantResault" array to check if all the permissions we asked are granted
              we initialize the map if we have all the permissions
        */
        if (requestCode == Constants.LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        LocationPermissionGranted = false;
                        return;
                    }
                }
                LocationPermissionGranted = true;

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    Log.d("latlon", "Last known location in lon: " + location.getLongitude() + ", lat: " + location.getLatitude());
                                }
                            }
                        });
                //locationManager.requestLocationUpdates(provider, 4000, 1, this);
                initMap();
            }
        }
    }

    // to make the map look on the current location
    private void moveCamera(LatLng latLng, float zoom) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    /// this function is using a built in function ("getMapAsync()") that calls to "onMapReady" function when the map is ready
    public void initMap() {
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

        } else {
            getLocationPermission();
        }
    }

    //this function is called automatically by the locationManager.getLocationUpdates according to the time we set it to be called
    @Override
    public void onLocationChanged(@NonNull Location location) {
        onMapReady(map);
    }

    /*
    private void getDeviceLocation() {
//    this function is getting the device location

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (LocationPermissionGranted) {
                Task location = fusedLocationClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null) {
                                Location currentLocation = (Location) task.getResult();
                                moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                        Constants.DEFAULT_ZOOM);
                                Toast.makeText(getSupportFragmentManager().findFragmentById(R.id.MapId).getActivity(), "location: " + currentLocation.getLatitude(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getSupportFragmentManager().findFragmentById(R.id.MapId).getActivity(), "Unable to get current location", Toast.LENGTH_SHORT).show();
                            }
                        } else
                            Toast.makeText(getSupportFragmentManager().findFragmentById(R.id.MapId).getActivity(), "Unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (SecurityException e) {
            Log.d("getDeviceLocation function", "getDeviceLocation: " + e.getMessage());
        }

    }
    */

    private void getDeviceLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            lastCurrentLocation = location;
                            moveCamera(new LatLng(location.getLatitude(), lastCurrentLocation.getLongitude()),Constants.DEFAULT_ZOOM);
                        }
                        else {
                            Toast.makeText(MapActivity.this, "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Authentication.logoff(this,currentUser,iAsyncResponse);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Authentication.logoff(this,currentUser,iAsyncResponse);
    }
}

