package com.example.thearena.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class Preferences {
    public static void saveMailAndPassword(String mail, String Password, Context context){
        SharedPreferences newUser = context.getSharedPreferences("userPrefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = newUser.edit();
        editor.putString("email", mail);
        editor.putString("userPassword",Password);
        editor.apply();
    }

    public static void saveUserId(String userId, Context context){
        SharedPreferences newUser = context.getSharedPreferences("userPrefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = newUser.edit();
        editor.putString("userId", userId);
        editor.apply();
    }
    public static String getUserId(Context context){
        SharedPreferences x = context.getSharedPreferences("userPrefs",MODE_PRIVATE);
        return x.getString("userId","");
    }
    public static String getMail(Context context){
        SharedPreferences x = context.getSharedPreferences("userPrefs",MODE_PRIVATE);
        return x.getString("email","");
    }

    public static String getPassword(Context context){
        SharedPreferences x = context.getSharedPreferences("userPrefs",MODE_PRIVATE);
        return x.getString("userPassword","");
    }
}
