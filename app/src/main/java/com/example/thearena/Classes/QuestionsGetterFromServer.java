package com.example.thearena.Classes;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.thearena.Interfaces.IAsyncResponse;
import com.example.thearena.Utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class QuestionsGetterFromServer {
    private  Context context;
    ArrayList<Question> questionList = new ArrayList<>();

//                    ----------------------------------------  Here We are using Volley.Get to retrieve the question from the server   ----------------------------------------
 //---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


    public QuestionsGetterFromServer(Context context){
        this.context = context;
    }

    public List<Question> getQuestions(final IAsyncResponse callBack){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, Constants.QUESTIONS_URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject object = response.getJSONObject(0);

                    JSONArray keys = object.names();
                    for (int i = 0; i < keys.length(); ++i) {
                        Question question = new Question();
                        String key = keys.getString(i);
                        JSONArray value = object.getJSONArray(key);
                        question.setQuestion(key);
                        question.setFirstAnswer(value.getString(0));
                        question.setSecondAnswer(value.getString(1));
                        question.setThirdAnswer(value.getString(2));
                        question.setFourthAnswer(value.getString(3));

                        questionList.add(question);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                callBack.processFinished(questionList);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("QC","Error");
            }
        });


        requestQueue.add(jsonArrayRequest);
        return questionList;
    }
}
