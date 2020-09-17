package com.example.thearena.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.thearena.Activities.MainActivity;
import com.example.thearena.Classes.Registration;
import com.example.thearena.R;
import com.example.thearena.Utils.Encryption;
import com.google.android.gms.tasks.Task;

import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment implements View.OnClickListener {


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private EditText email;
    private EditText firstName;
    private EditText lastName;
    private EditText phoneNumber;
    private EditText age;
    private RadioGroup radioGroup;
    private RadioButton maleRadioButton;
    private RadioButton femaleRadioButton;
    private RadioButton interestedInMaleCheckBox;
    private RadioButton interestedInFemaleCheckBox;
    private EditText password;
    private EditText verifyPassword;
    private Button nextButton;
    private Button backButton;
    private boolean isMale;
    private boolean interestedInWomen;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
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
        View v = inflater.inflate(R.layout.fragment_register, container, false);

        email = v.findViewById(R.id.register_email_textbox);
        firstName = v.findViewById(R.id.register_firstname_textbox);
        lastName = v.findViewById(R.id.register_lastName_textbox);
        phoneNumber = v.findViewById(R.id.register_phoneNumber_textbox);
        age = v.findViewById(R.id.register_age_textbox);
        radioGroup = v.findViewById(R.id.register_gender_radioGroup);
        maleRadioButton = v.findViewById(R.id.register_gender_male_radio);
        femaleRadioButton = v.findViewById(R.id.register_gender_female_radio);
        interestedInMaleCheckBox = v.findViewById(R.id.register_interested_male_chkbox);
        interestedInFemaleCheckBox = v.findViewById(R.id.register_interested_female_chkbox);
        password = v.findViewById(R.id.register_password_first_Textbox);
        verifyPassword = v.findViewById(R.id.register_password_second_textbox);
        nextButton = v.findViewById(R.id.register_next_button);
        backButton = v.findViewById(R.id.register_back_button);
        nextButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_next_button:
                //-------------Checking if all fields in register fragment are not empty-------------
                if (email.getText().toString().equals("") || firstName.getText().toString().equals("") || lastName.getText().toString().equals("") || phoneNumber.getText().toString().equals("")
                        || age.getText().toString().equals("") || radioGroup.getCheckedRadioButtonId() == -1 || password.getText().toString().equals("") || verifyPassword.getText().toString().equals("")
                        || (!interestedInMaleCheckBox.isChecked() && !interestedInFemaleCheckBox.isChecked())) {
                    //please fill all fields!!!!!
                    Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_LONG).show();
                } else {
                    //firstPageValidation - function to check password is correct,what gender and who interested in
                    // and if password is good save tha data from this page and load sec fragment
                    try {
                        firstPageValidation();
                    }catch (Exception e){
                        Log.d("Exception", "onClick - Error: "+e.getMessage());
                    }
                }
                break;
            case R.id.register_back_button:
                returnToLoginFragment();
                break;
        }
    }

    private void returnToLoginFragment() {
        MainActivity mainActivity = (MainActivity) getActivity();
        assert mainActivity != null;
        mainActivity.mainFragmentManager(new LoginPage());
    }

    private void firstPageValidation() {
        if (!password.getText().toString().equals(verifyPassword.getText().toString())) {
            Toast.makeText(getContext(), "Verification password isn't correct", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!emailCheck()) {
            Toast.makeText(getContext(), "Illegal email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Integer.parseInt(age.getText().toString()) < 18){
            Toast.makeText(getContext(), "You are too young", Toast.LENGTH_SHORT).show();
            return;

        }
        isMale = maleRadioButton.isChecked();
        interestedInWomen = interestedInFemaleCheckBox.isChecked();
        Registration.saveFirstPageInfo(email.getText().toString(), firstName.getText().toString(), lastName.getText().toString(), phoneNumber.getText().toString(),
                Integer.parseInt(age.getText().toString()), isMale, interestedInWomen, Encryption.encryptThisString(password.getText().toString()));
        // here move to questions register
        MainActivity mainActivity = (MainActivity) getActivity();
        assert mainActivity != null;
        mainActivity.mainFragmentManager(new RegisterQuestionsFragment());
    }

    private boolean emailCheck() {
        return isValid(email.getText().toString());
    }

    public static boolean isValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }
}