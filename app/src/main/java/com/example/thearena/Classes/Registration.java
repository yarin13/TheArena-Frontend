package com.example.thearena.Classes;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


//-----------------------------                 This class is used to save all the registration data that the user fill in both register fragments      -----------------------
//-----------------------------                 Later we send this data to the server to create a new user                                              -------------------------

public class Registration {

    private static String email;
    private static String firstName;
    private static String lastName;
    private static String phoneNumber;
    private static int age;
    private static boolean isMale;
    private static boolean intrestedInWomen;
    private static String password;

    private static int score;



    public static void saveFirstPageInfo(String email,String firstName,String lastName,String phoneNumber,int age,boolean male,boolean intrestedInWomen,String password){
        setEmail(email);
        setFirstName(firstName);
        setLastName(lastName);
        setPhoneNumber(phoneNumber);
        setAge(age);
        seIstMale(male);
        setIntrestedInWomen(intrestedInWomen);
        setPassword(password);
    }


    public Registration() {
    }

    public Registration(String email,String firstName,String lastName,String phoneNumber,int age,boolean male,boolean intrestedInWomen,String password){
        setEmail(email);
        setFirstName(firstName);
        setLastName(lastName);
        setPhoneNumber(phoneNumber);
        setAge(age);
        seIstMale(male);
        setIntrestedInWomen(intrestedInWomen);
        setPassword(password);
    }

    public static int getScore() {
        return score;
    }

    public static void setScore(int sum) { score = sum;}

    public static int getAge() {
        return age;
    }

    public static void setAge(int age) { Registration.age = age; }


    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        Registration.email = email;
    }

    public static String getFirstName() {
        return firstName;
    }

    public static void setFirstName(String firstName) {
        Registration.firstName = firstName;
    }

    public static String getLastName() {
        return lastName;
    }

    public static void setLastName(String lastName) {
        Registration.lastName = lastName;
    }

    public static String getPhoneNumber() {
        return phoneNumber;
    }

    private static void setPhoneNumber(String phoneNumber) {   Registration.phoneNumber = phoneNumber;  }

    public static String getIsMale() {
        if(Registration.isMale)
            return "Male";
        return "Female";
    }

    public static void seIstMale(boolean male) {
        isMale = male;
    }

    public static String getIntrestedInWomen(){
        if(intrestedInWomen)
            return "Female";
        else
            return "Male";
    }

    public static void setIntrestedInWomen(boolean intrestedInWomen) {
        Registration.intrestedInWomen = intrestedInWomen;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        Registration.password = password;
    }


}
