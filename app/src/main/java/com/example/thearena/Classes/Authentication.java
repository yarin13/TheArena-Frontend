package com.example.thearena.Classes;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.thearena.Interfaces.IAsyncResponse;
import com.example.thearena.Utils.Constants;
import com.example.thearena.Utils.Encryption;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Authentication {
    /*
        This class is compact with functions that all of them call the - requestManager function.
        registerNewUser (context, callback) - get all the field from the Registration class
        signIn (context, email, password, callback) -
        logoff (context, email) -
        sendLocation(context, email, coordinates, callback) -
        sendPasswordResetRequest(context, email, newPassword, callback) -

        requestManager - This function in responsible to manage all the requests,
                        to call this function need to make sure you pass a context , URL , Method_Type , callback , map.
                        it first check the URL , then the Method_Type - GET, POST...
     */

    public static HashMap<String, Object> map = new HashMap<>();

    public static void registerNewUser(final Context context, final IAsyncResponse callBack) {
        map.clear();
        map.put("userEmail", Registration.getEmail().trim());
        map.put("userFirstName", Registration.getFirstName().trim());
        map.put("userLastName", Registration.getLastName().trim());
        map.put("userPhoneNumber", Registration.getPhoneNumber().trim());
        map.put("userAge", Registration.getAge());
        map.put("userGender", Registration.getIsMale().trim());
        map.put("userInterestedIn", Registration.getInterestedInWomen().trim());
        map.put("userScore", Registration.getScore());
        map.put("userPassword", String.valueOf(Registration.getPassword()));
        requestManager(context, Constants.AUTH_URL, Request.Method.POST, callBack, map);
    }

    public static void signIn(final Context context, String email, String password, final IAsyncResponse callBack) {
        map.clear();
        if (password.length() != 64)
            map.put("password", Encryption.encryptThisString(password));
        else
            map.put("password", password);

        map.put("email", email);
        requestManager(context, Constants.AUTH_URL, Request.Method.POST, callBack, map);
    }

    public static void logoff(final Context context, final String userToLogoff) {
        map.clear();
        map.put("email", userToLogoff);
        requestManager(context, Constants.ONLINE_USER_LOCATION, Request.Method.POST, null, map);
    }


    public static void sendLocation(final Context context, final String currentUserEmail, final Location lastCurrentLocation, final IAsyncResponse iAsyncResponse) {
        map.clear();
        if (lastCurrentLocation != null) {
            map.put("lat", lastCurrentLocation.getLatitude());
            map.put("lng", lastCurrentLocation.getLongitude());
            map.put("email", currentUserEmail);
            requestManager(context, Constants.ONLINE_USER_LOCATION, Request.Method.PUT, iAsyncResponse, map);
        }
    }

    public static void sendPasswordResetRequest(final Context context, String currentUserEmail, String newPassword, final IAsyncResponse iAsyncResponse) {
        map.clear();
        map.put("email", currentUserEmail);
        map.put("newPassword", Encryption.encryptThisString(newPassword));
        requestManager(context, Constants.AUTH_URL, Request.Method.PUT, iAsyncResponse, map);
    }

    public static void getCurrentUserInfo(final Context context, final int userId, final IAsyncResponse iAsyncResponse) {
        map.clear();
        map.put("userId", userId);
        requestManager(context, Constants.USERINFO_URL, Request.Method.GET, iAsyncResponse, map);
    }


    public static void requestManager(final Context context, final String uri, final Integer method, final IAsyncResponse callback, final HashMap<String, Object> paramsToBody) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        if (paramsToBody.size() != 0) {
            StringRequest request = new StringRequest(method, uri, response -> {
                if (response != null) {
                    callback.processFinished(response);
                }
            }, error -> Log.d("Error", "onErrorResponse: " + error)) {
                @Override
                public String getBodyContentType() {
                    return "application/json";
                }

                @Override
                public byte[] getBody() {
                    JSONObject body = new JSONObject();
                    try {
                        if (uri.equals(Constants.AUTH_URL)) {
                            switch (method) {
                                case Method.POST:
                                    if (paramsToBody.size() > 2) {
                                        body.put("email", paramsToBody.get("userEmail"));
                                        body.put("firstName", paramsToBody.get("userFirstName"));
                                        body.put("lastName", paramsToBody.get("userLastName"));
                                        body.put("phoneNumber", paramsToBody.get("userPhoneNumber"));
                                        body.put("age", String.valueOf(paramsToBody.get("userAge")));
                                        body.put("gender", paramsToBody.get("userGender"));
                                        body.put("interestedIn", paramsToBody.get("userInterestedIn"));
                                        body.put("score", String.valueOf(paramsToBody.get("userScore")));
                                        body.put("password", paramsToBody.get("userPassword"));
                                    } else {
                                        body.put("email", paramsToBody.get("email"));
                                        body.put("password", paramsToBody.get("password"));
                                    }
                                    break;
                                case Method.PUT:
                                    body.put("email", paramsToBody.get("email"));
                                    body.put("newPassword", paramsToBody.get("newPassword"));
                                    break;
                            }
                            return body.toString().getBytes();
                        } else if (uri.equals(Constants.ONLINE_USER_LOCATION)) {
                            switch (method) {
                                case Method.PUT:
                                    body.put("lat", paramsToBody.get("lat"));
                                    body.put("lng", paramsToBody.get("lng"));
                                    body.put("email", paramsToBody.get("email"));
                                    break;
                                case Method.POST:
                                    body.put("email", paramsToBody.get("email"));
                                    break;
                            }
                            return body.toString().getBytes();
                        }
                    } catch (Exception e) {
                        Log.d("GET-BODY ERROR!", "getBody: " + e.getMessage());
                    }
                    return "".getBytes();
                }

                @Override
                public Map<String, String> getHeaders(){
                    Map<String, String> params = new HashMap<>();
                    if (uri.equals(Constants.USERINFO_URL))
                        params.put("userId", String.valueOf(paramsToBody.get("userId")));
                    return params;
                }
            };
            requestQueue.getCache().clear();
            requestQueue.add(request);
        }
    }
}