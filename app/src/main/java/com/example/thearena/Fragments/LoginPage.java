package com.example.thearena.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.thearena.Activities.MainActivity;
import com.example.thearena.Classes.Authentication;
import com.example.thearena.Classes.Registration;
import com.example.thearena.Classes.User;
import com.example.thearena.Data.InnerDatabaseHandler;
import com.example.thearena.Interfaces.IAsyncResponse;
import com.example.thearena.R;
import com.example.thearena.Utils.Encryption;
import com.example.thearena.Utils.Preferences;

import org.json.JSONObject;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginPage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginPage extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private TextView userEmail;
    private TextView password;
    private IAsyncResponse iAsyncResponse;


    private String mParam1;
    private String mParam2;

    public LoginPage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginPage.
     */

    public static LoginPage newInstance(String param1, String param2) {
        LoginPage fragment = new LoginPage();
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
        View v = inflater.inflate(R.layout.fragment_login_page, container, false);
        userEmail = v.findViewById(R.id.login_userEmail_textbox);
        password = v.findViewById(R.id.login_password_textbox);
        Button signIn = v.findViewById(R.id.login_singIn_button);
        Button register = v.findViewById(R.id.login_register_button);
        Button resetPassword = v.findViewById(R.id.login_passwordReset_button);

        resetPassword.setOnClickListener(this);
        signIn.setOnClickListener(this);
        register.setOnClickListener(this);

        String sharedMail = Preferences.getMail(Objects.requireNonNull(getContext()));
        String sharedPassword = Preferences.getPassword(getContext());

        iAsyncResponse = new IAsyncResponse() {
            @Override
            public <T> void processFinished(T response) {

                try {
                    JSONObject res = new JSONObject(response.toString());

                    if(res.has("Success")){
                        String pass = Encryption.encryptThisString(password.getText().toString()); //encrypt password
                        if (sharedMail.equals("")){
                            Preferences.saveMailAndPassword(userEmail.getText().toString(), pass, Objects.requireNonNull(getContext()));
                            Preferences.saveUserId(res.getString("userId"),Objects.requireNonNull(getContext()));
                        }
                        MainActivity mainActivity = (MainActivity) getActivity();
                        assert mainActivity != null;
                        mainActivity.moveToMap();
                    }  else {
                        Log.d("TAG", "Failed "+response);
                    }
                } catch (Throwable t) {
                    Log.d("My App", "Could not parse malformed JSON: \"" + response.toString() + "\"");
                }
            }
        };

        if (!sharedMail.equals("") && !sharedPassword.equals(""))
            Authentication.signIn(getContext(),sharedMail, sharedPassword,  iAsyncResponse);
        return v;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_singIn_button:
                String email = userEmail.getText().toString();
                final String pass = password.getText().toString();
                if (!email.equals("") && !pass.equals("")) {
                    new Thread(() -> Authentication.signIn(getContext(),userEmail.getText().toString(), password.getText().toString(),  iAsyncResponse)).start();
                } else {
                    Toast.makeText(getContext(), "Hmm...Something is missing...", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.login_register_button:
                new Thread(() -> {
                    MainActivity mainActivity = (MainActivity) getActivity();
                    assert mainActivity != null;
                    mainActivity.mainFragmentManager(new RegisterFragment());
                }).start();
                break;
            case R.id.login_passwordReset_button:
                MainActivity mainActivity = (MainActivity) getActivity();
                assert mainActivity != null;
                mainActivity.mainFragmentManager(new PasswordResetFragment());
                break;
        }
    }
}