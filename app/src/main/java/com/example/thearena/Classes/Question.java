package com.example.thearena.Classes;

import android.util.Log;

public class Question {

    public String question;

    public String firstAnswer;
    public String secondAnswer;
    public String thirdAnswer;
    public String fourthAnswer;

    @Override
    public String toString(){

        Log.d("QQ","q "+getQuestion());
        return null;
    }


    public Question(){

    }
//                          --------------------------                   This class is used to save a Question object                   --------------------------
//                          --------------------------                   THe questions are coming from the DataBase                     --------------------------


    public Question(String question,String firstAnswer,String secondAnswer,String thirdAnswer,String fourthAnswer){
        this.question = question;
        this.firstAnswer = firstAnswer;
        this.secondAnswer = secondAnswer;
        this.thirdAnswer = thirdAnswer;
        this.fourthAnswer = fourthAnswer;
    }

    public String getQuestion() {
        if(question == null)
            return " ";
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getFirstAnswer() {
        if(firstAnswer == "null")
            return null;
        return firstAnswer;
    }

    public void setFirstAnswer(String firstAnswer) {
        this.firstAnswer = firstAnswer;
    }

    public String getSecondAnswer() {
        if(secondAnswer == "null")
            return null;
        return secondAnswer;
    }

    public void setSecondAnswer(String secondAnswer) {
        this.secondAnswer = secondAnswer;
    }

    public String getThirdAnswer() {
        if(thirdAnswer == "null")
            return null;
        return thirdAnswer;
    }

    public void setThirdAnswer(String thirdAnswer) {
        this.thirdAnswer = thirdAnswer;
    }

    public String getFourthAnswer() {
        if(fourthAnswer == "null")
            return null;
        return fourthAnswer;
    }

    public void setFourthAnswer(String fourthAnswer) {
        this.fourthAnswer = fourthAnswer;
    }
}
