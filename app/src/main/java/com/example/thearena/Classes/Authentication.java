package com.example.thearena.Classes;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

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


    public static void signIn(final String username, final String Password, final Context context, final IAsyncResponse callBack) {
        requestQueue = Volley.newRequestQueue(context);

        if (username != null || Password != null) {

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, Constants.AUTH_URL, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    JSONArray key = response.names();
                    try {
                        callBack.processFinished(response.getString(key.getString(0)));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("signIn - Error", "onErrorResponse: " + error.getMessage());
                }
            }

            ) {
                @Override
                public Map<String, String> getHeaders() {
                    LinkedHashMap<String, String> params = new LinkedHashMap<>();
                    params.put("email", username);
                    params.put("password", Password);

                    return params;
                }

            };
            requestQueue.add(jsonObjectRequest);
        } else {
            Log.d("blank", "onClick: username or password is blank");
        }
    }

    public static void logoff(final Context context, User userToLogoff, final IAsyncResponse callBack){
        if (userToLogoff != null) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.LOGOFF_USER_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    callBack.processFinished(response);
                    //Toast.makeText(context,response,Toast.LENGTH_LONG).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context, "PROBLEM!!!!", Toast.LENGTH_SHORT).show();
                }
            });

            requestQueue.getCache().clear();
            requestQueue.add(stringRequest);
        }
    }
}

