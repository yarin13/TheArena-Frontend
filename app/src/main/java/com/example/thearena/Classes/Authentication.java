package com.example.thearena.Classes;

import android.content.Context;
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
import com.example.thearena.Interfaces.IAsyncResponse;
import com.example.thearena.Utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
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
            public byte[] getBody() {
                JSONObject params = new JSONObject();
                try {
                    params.put("userEmail", Registration.getEmail().trim());
                    params.put("userFirstName", Registration.getFirstName().trim());
                    params.put("userLastName", Registration.getLastName().trim());
                    params.put("userPhoneNumber", Registration.getPhoneNumber().trim());
                    params.put("userAge", String.valueOf(Registration.getAge()).trim());
                    params.put("userGender", Registration.getIsMale().trim());
                    params.put("userIntrestedIn", Registration.getIntrestedInWomen().trim());
                    params.put("userScore", String.valueOf(Registration.getScore()).trim());
                    params.put("userPassword", Registration.getPassword().trim());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return params.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
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
                        callBack.processFinished(response.getString(key.getString(0)), username, password);
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
                public byte[] getBody() {
                    JSONObject body = new JSONObject();
                    try {
                        body.put("email",username);
                        body.put("password",password);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return body.toString().getBytes();
                }

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }

            };
            requestQueue.add(jsonObjectRequest);
        }
    }

    public static void logoff(final Context context, final String userToLogoff) {
        requestQueue = Volley.newRequestQueue(context);
        if (userToLogoff != null) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.ONLINE_USER_LOCATION, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("logoff", "onResponse: " + response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("logoff problem", "" + error);
                }
            }) {
                @Override
                public byte[] getBody() {
                    JSONObject body = new JSONObject();
                    try {
                        body.put("email",userToLogoff);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return body.toString().getBytes();
                }

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }
            };

            requestQueue.getCache().clear();
            requestQueue.add(stringRequest);
        }
    }


    public static void sendLocation(final Context context, final String currentUserEmail, final Location lastCurrentLocation, @Nullable final IAsyncResponse iAsyncResponse) {
        requestQueue = Volley.newRequestQueue(context);
        if (!currentUserEmail.equals("")) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.ONLINE_USER_LOCATION, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    assert iAsyncResponse != null;
                    iAsyncResponse.processFinished(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("sendLocation - Error", "onErrorResponse: " + error.getMessage());
                }
            }) {
                @Override
                public byte[] getBody() {
                    JSONObject body = new JSONObject();
                    try {
                        body.put("lat",lastCurrentLocation.getLatitude());
                        body.put("lng",lastCurrentLocation.getLongitude());
                        body.put("email",currentUserEmail);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return body.toString().getBytes();
                }

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }
            };
            requestQueue.add(stringRequest);
        }
    }

    public static void sendPasswordResetRequest(final Context context, final String currentUserEmail, final String newPassword, final IAsyncResponse iAsyncResponse) {
        requestQueue = Volley.newRequestQueue(context);
        if (!currentUserEmail.equals("")) {
            StringRequest request = new StringRequest(Request.Method.PUT, Constants.AUTH_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    iAsyncResponse.processFinished(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context, "Could not reset your password, Please try again later.", Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                public byte[] getBody() {
                    JSONObject body = new JSONObject();
                    try {
                        body.put("email",currentUserEmail);
                        body.put("newPassword",newPassword);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return body.toString().getBytes();
                }

                @Override
                public String getBodyContentType() {
                    return "application/json";
                }
            };
            requestQueue.getCache().clear();
            requestQueue.add(request);
        }
    }
}

