package com.example.thearena.Utils;

import android.Manifest;

public class Constants {

    public static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    public static final float DEFAULT_ZOOM = 15f;

    public static final String QUESTIONS_URL = "http://10.0.2.2:8080/TheArenaServlet/QuestionServlet";

    public static final String AUTH_URL = "http://10.0.2.2:8080/TheArenaServlet/Authentication";
    public static final String LOGOFF_USER_URL = "http://localhost:8080/TheArenaServler/onlineUsersLocation";

}
