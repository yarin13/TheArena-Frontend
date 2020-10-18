package com.example.thearena.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thearena.Activities.MainActivity;
import com.example.thearena.Classes.Authentication;
import com.example.thearena.Data.InnerDatabaseHandler;
import com.example.thearena.Interfaces.IAsyncResponse;
import com.example.thearena.R;
import com.example.thearena.Utils.Encryption;
import com.example.thearena.Utils.Preferences;

import org.json.JSONObject;

import java.util.Objects;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SplashScreen#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SplashScreen extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private TextView userEmail;
    private TextView password;
    private IAsyncResponse iAsyncResponse;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SplashScreen() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SplashScreen.
     */
    // TODO: Rename and change types and number of parameters
    public static SplashScreen newInstance(String param1, String param2) {
        SplashScreen fragment = new SplashScreen();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_splash_screen, container, false);
        final String sharedMail = Preferences.getMail(Objects.requireNonNull(getContext()));
        String sharedPassword = Preferences.getPassword(getContext());

        iAsyncResponse = new IAsyncResponse() {
            @Override
            public <T> void processFinished(T response) {
                try {
                    JSONObject res = new JSONObject(response.toString());
                    MainActivity mainActivity = (MainActivity) getActivity();
                    assert mainActivity != null;
                    if (res.has("Success")) {
                        String pass = Encryption.encryptThisString(password.getText().toString()); //encrypt password

                        Preferences.saveMailAndPassword(userEmail.getText().toString(), pass, Objects.requireNonNull(getContext()));
                        Preferences.saveUserId(res.getString("userId"), Objects.requireNonNull(getContext()));

                        if (!sharedMail.equals("")) {
                            mainActivity.innerDatabaseHandler.addUser(sharedMail, pass);
                        } else if (!userEmail.getText().toString().equals("")) {
                            mainActivity.innerDatabaseHandler.addUser(userEmail.getText().toString(), pass);
                        } else {
                            return;
                        }
                        mainActivity.moveToMap();
                    } else {
                        mainActivity.mainFragmentManager(new LoginPage());
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        };


        InnerDatabaseHandler innerDatabaseHandler = new InnerDatabaseHandler(getContext());
        String email = innerDatabaseHandler.getUserEmail();
        if (!email.equals("") && !sharedPassword.equals("")) {
            Authentication.signIn(getContext(), email, sharedPassword, iAsyncResponse);
        } else if (!sharedMail.equals("") && !sharedPassword.equals(""))
            Authentication.signIn(getContext(), sharedMail, sharedPassword, iAsyncResponse);
        else {
            MainActivity mainActivity = (MainActivity) getActivity();
            assert mainActivity != null;
            mainActivity.mainFragmentManager(new LoginPage());
        }

        return v;
    }
}