package com.example.thearena.Fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thearena.Activities.MainActivity;
import com.example.thearena.Classes.Authentication;
import com.example.thearena.Interfaces.IAsyncResponse;
import com.example.thearena.R;
import com.example.thearena.Utils.Preferences;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PasswordResetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PasswordResetFragment extends Fragment implements View.OnClickListener {
    private TextView email;
    private TextView passwordOne;
    private TextView passwordTwo;
    public IAsyncResponse iAsyncResponse;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PasswordResetFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PasswordResetFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PasswordResetFragment newInstance(String param1, String param2) {
        PasswordResetFragment fragment = new PasswordResetFragment();
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
        View view = inflater.inflate(R.layout.fragment_password_reset, container, false);
        iAsyncResponse = new IAsyncResponse() {
            @Override
            public <T> void processFinished(T questionList, @Nullable String mail, @Nullable String pass) {}

            @Override
            public <T> void processFinished(T response) {
                if (response.equals("success")) {
                    Preferences.saveMailAndPassword(email.getText().toString(), passwordOne.getText().toString(), Objects.requireNonNull(getContext()));
                    MainActivity mainActivity = (MainActivity) getActivity();
                    assert mainActivity != null;
                    mainActivity.mainFragmentManager(new LoginPage());
                }
            }
        };
        email = view.findViewById(R.id.passwordReset_email_textView);
        passwordOne = view.findViewById(R.id.passwordReset_passwordOne_textView);
        passwordTwo = view.findViewById(R.id.passwordReset_passwordTwo_textView);
        Button back = view.findViewById(R.id.passwordReset_back_button);
        back.setOnClickListener(this);
        Button reset = view.findViewById(R.id.passwordReset_resetUserPassword_button);
        reset.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
    switch (view.getId()){
        case R.id.passwordReset_back_button:
                MainActivity mainActivity = (MainActivity)getActivity();
            assert mainActivity != null;
            mainActivity.mainFragmentManager(new LoginPage());
            break;
        case R.id.passwordReset_resetUserPassword_button:
            if (RegisterFragment.isValid(email.getText().toString())){
                if (passwordOne.getText().toString().equals(passwordTwo.getText().toString())){
                    Authentication.sendPasswordResetRequest(getContext(),email.getText().toString(),passwordOne.getText().toString(),iAsyncResponse);
                }
                else
                    Toast.makeText(getContext(),"Passwords are not match",Toast.LENGTH_LONG).show();
            }
            break;

    }
    }

}