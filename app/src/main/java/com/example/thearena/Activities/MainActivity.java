package com.example.thearena.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.thearena.Data.InnerDatabaseHandler;
import com.example.thearena.Fragments.ImageUploadFragment;
import com.example.thearena.Fragments.LoginPage;
import com.example.thearena.Fragments.SplashScreen;
import com.example.thearena.R;

public class MainActivity extends AppCompatActivity {
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    public InnerDatabaseHandler innerDatabaseHandler = new InnerDatabaseHandler(MainActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                // Log.d("savedInstanceState", "onCreate: ");
                return;
            }
            mainFragmentManager(new SplashScreen());
            //      mainFragmentManager(new ImageUploadFragment());

        }
    }

    public void mainFragmentManager(Fragment newFragmentToMoveTo) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newFragmentToMoveTo);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void moveToMap() {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }
}