package com.example.thearena.Classes;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.thearena.Interfaces.IAsyncResponse;
import com.example.thearena.Utils.Constants;
import com.example.thearena.Utils.Encryption;

import org.json.JSONObject;
import java.util.HashMap;

public class Authentication {

    public static HashMap<String, String> map = new HashMap<>();

    public static void registerNewUser(final Context context, final IAsyncResponse callBack) {
        map.clear();
        map.put("userEmail", Registration.getEmail().trim());
        map.put("userFirstName", Registration.getFirstName().trim());
        map.put("userLastName", Registration.getLastName().trim());
        map.put("userPhoneNumber", Registration.getPhoneNumber().trim());
        map.put("userAge", String.valueOf(Registration.getAge()).trim());
        map.put("userGender", Registration.getIsMale().trim());
        map.put("userInterestedIn", Registration.getInterestedInWomen().trim());
        map.put("userScore", String.valueOf(Registration.getScore()).trim());
        map.put("userPassword", Encryption.encryptThisString(Registration.getPassword().trim()));
        requestManager(context, Constants.AUTH_URL, Request.Method.POST, callBack, map);
    }

    public static void signIn(String email, String password, final Context context, final IAsyncResponse callBack) {
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
            map.put("lat", String.valueOf(lastCurrentLocation.getLatitude()));
            map.put("lng", String.valueOf(lastCurrentLocation.getLongitude()));
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


    public static void requestManager(final Context context, final String uri, final Integer method, final IAsyncResponse callback, final HashMap<String, String> paramsToBody) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        if (paramsToBody.size() != 0) {
            StringRequest request = new StringRequest(method, uri, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    if (response != null) {
                        callback.processFinished(response);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Error", "onErrorResponse: " + error);
                }
            }) {
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
                                    }else{
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
                                    body.put("mail", paramsToBody.get("email"));
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
            };
            requestQueue.getCache().clear();
            requestQueue.add(request);
        }
    }
}