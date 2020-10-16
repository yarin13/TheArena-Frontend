package com.example.thearena.Utils;

import android.Manifest;

public class Constants {
    //Location Constants
    public static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    public static final int GALLERY_PERMISSION__REQUEST_CODE = 123;
    public static final int PICTURE_PERMISSION__REQUEST_CODE = 12;
    public static final float DEFAULT_ZOOM = 18f;

    //Images' upload Constants
    public static final String READ_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    public static final String WRITE_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final int REQUEST_EXTERNAL_STORAGE = 3;

    //URL's :
    public static final String BASE_URL = "http://10.0.2.2:8080/TheArenaServlet/";
    public static final String QUESTIONS_URL = "http://10.0.2.2:8080/TheArenaServlet/QuestionServlet";
    public static final String AUTH_URL = "http://10.0.2.2:8080/TheArenaServlet/Authentication";
    public static final String ONLINE_USER_LOCATION = "http://10.0.2.2:8080/TheArenaServlet/onlineUsersLocation";
    public static final String PHOTOS_URL = "http://10.0.2.2:8080/TheArenaServlet/PhotosServlet";
    public static final String USERINFO_URL = "http://localhost:8080/TheArenaServlet/UserServlet";

    //inner Database
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "innerArena_db";
    public static final String TABLE_NAME = "personal_info";

    //personal_info columns names
    public static final String KEY_ID = "id";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
}
