package com.example.thearena.Utils;

import android.Manifest;

public class Constants {

    public static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    public static final float DEFAULT_ZOOM = 18f;

    //URL's :
    public static final String QUESTIONS_URL = "http://10.0.2.2:8080/TheArenaServlet/QuestionServlet";
    public static final String AUTH_URL = "http://10.0.2.2:8080/TheArenaServlet/Authentication";
    public static final String ONLINE_USER_LOCATION = "http://10.0.2.2:8080/TheArenaServlet/onlineUsersLocation";

    //inner Database
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "innerArena_db";
    public static final String TABLE_NAME = "personal_info";

    //personal_info columns names
    public static final String KEY_ID = "id";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
}
