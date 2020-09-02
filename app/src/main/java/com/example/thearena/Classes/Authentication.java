package com.example.thearena.Classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.thearena.Activities.MainActivity;
import com.example.thearena.Activities.MapActivity;
import com.example.thearena.Fragments.LoginPage;
import com.example.thearena.Interfaces.IAsyncResponse;
import com.example.thearena.Utils.Constants;
import com.example.thearena.Utils.Preferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

public class Authentication {


    private static RequestQueue requestQueue;


    public static void registerNewUser(final Context context, final IAsyncResponse callBack) {
        requestQueue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.AUTH_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callBack.processFinished(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "No Connection", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                LinkedHashMap<String, String> params = new LinkedHashMap<>();
                params.put("userEmail", Registration.getEmail().trim());
                params.put("userFirstName", Registration.getFirstName().trim());
                params.put("userLastName", Registration.getLastName().trim());
                params.put("userPhoneNumber", Registration.getPhoneNumber().trim());
                params.put("userAge", String.valueOf(Registration.getAge()).trim());
                params.put("userGender", Registration.getIsMale().trim());
                params.put("userIntrestedIn", Registration.getIntrestedInWomen().trim());
                params.put("userScore", String.valueOf(Registration.getScore()).trim());
                params.put("userPassword", Registration.getPassword().trim());

                return params;
            }
        };

        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);
    }


    public static void signIn(final String username, final String password, final Context context, final IAsyncResponse callBack) {
        requestQueue = Volley.newRequestQueue(context);

        if (!username.equals("") && !password.equals("")) {

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.AUTH_URL, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    JSONArray key = response.names();
                    try {
                        assert key != null;
                        callBack.processFinished(response.getString(key.getString(0)),username,password);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("signIn - Error", "onErrorResponse: " + error.getMessage());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() {
                    LinkedHashMap<String, String> params = new LinkedHashMap<>();
                    params.put("email", username);
                    params.put("password", password);

                    return params;
                }

            };
            requestQueue.add(jsonObjectRequest);
        }
    }

    public static void logoff(final Context context, final String userToLogoff){
        requestQueue = Volley.newRequestQueue(context);
        if (userToLogoff != null) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.ONLINE_USER_LOCATION, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("logoff", "onResponse: "+response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("logoff problem", ""+error);
                }
            }){
                @Override
                protected Map<String, String> getParams() {
                    LinkedHashMap<String, String> params = new LinkedHashMap<>();
                    params.put("email", userToLogoff);
                    return params;
                }
            };

            requestQueue.getCache().clear();
            requestQueue.add(stringRequest);
        }
    }


    public static void sendLocation(final Context context, final String currentUserEmail, final Location lastCurrentLocation, @Nullable IAsyncResponse iAsyncResponse) {
        requestQueue = Volley.newRequestQueue(context);
        if (!currentUserEmail.equals("")) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.ONLINE_USER_LOCATION, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("sendLocation - Error", "onErrorResponse: " + error.getMessage());
                }
            }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    LinkedHashMap<String, String> params = new LinkedHashMap<>();
                    params.put("lat", String.valueOf(lastCurrentLocation.getLatitude()));
                    params.put("lng", String.valueOf(lastCurrentLocation.getLongitude()));
                    params.put("mail",currentUserEmail);
                    return params;
                }
            };
            requestQueue.add(stringRequest);
        }
    }
}

