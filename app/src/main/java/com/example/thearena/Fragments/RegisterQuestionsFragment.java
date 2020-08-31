package com.example.thearena.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.thearena.Activities.MainActivity;
import com.example.thearena.Classes.Authentication;
import com.example.thearena.Classes.PointsSummary;
import com.example.thearena.Classes.Question;
import com.example.thearena.Classes.QuestionsGetterFromServer;
import com.example.thearena.Classes.Registration;
import com.example.thearena.Interfaces.IAsyncResponse;
import com.example.thearena.R;
import com.example.thearena.UI.RecyclerViewAdapter;
import com.example.thearena.Utils.Preferences;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterQuestionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterQuestionsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Button registerButton;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private List<Question> questionList;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RegisterQuestionsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterQuestionsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterQuestionsFragment newInstance(String param1, String param2) {
        RegisterQuestionsFragment fragment = new RegisterQuestionsFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_questions, container, false);

        recyclerView = view.findViewById(R.id.questions_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        questionList = new ArrayList<>();

        // only when the answer from the server returns, send the data to the adapter;

        questionList = new QuestionsGetterFromServer(getContext()).getQuestions(new IAsyncResponse() {
            @Override
            public <T> void processFinished(T questionList) {
                recyclerViewAdapter = new RecyclerViewAdapter(getContext(), (List<Question>) questionList);
                recyclerView.setAdapter(recyclerViewAdapter);
                recyclerViewAdapter.notifyDataSetChanged();
            }

        });


        registerButton = view.findViewById(R.id.questions_submit_button);


        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int sum = 0;
                for (int i = 0; i < PointsSummary.arr.length; i++) {
                    sum += PointsSummary.arr[i];
                }

                Registration.setScore(sum);
                //------------------------------------------------------create new User------------------------------------------

                Authentication.registerNewUser(getContext(), new IAsyncResponse() {
                    @Override
                    public <T> void processFinished(T response) {
                        if(response.toString().equals("{\"Success\":\"New user is created!\"}")) {

                            //send success from server!!
                            Preferences.saveMailAndPassword(Registration.getEmail(), Registration.getPassword(), getActivity().getBaseContext());
                            //Move to map activity

                            MainActivity mainActivity = (MainActivity) getActivity();
                            mainActivity.moveToMap();

                        } else
                            Toast.makeText(getActivity(), "" + response.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        return view;
    }
}