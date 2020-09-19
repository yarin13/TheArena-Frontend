package com.example.thearena.Classes;

import com.google.android.gms.maps.model.LatLng;

public class User {
//-----------according to database---------------------
    private int userId;
    private String firstName;
    private String lastName;
    private String userEmail;
    private String userPhoneNumber;
    private int userAge;
    private String userGender;
    private String userInterestedIn;
    private LatLng coordinates;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LatLng getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(LatLng coordinates) {
        this.coordinates = coordinates;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public int getUserAge() {
        return userAge;
    }

    public void setUserAge(int userAge) {
        this.userAge = userAge;
    }

    public String getUserGender() {
        return userGender;
    }

    public void setUserGender(String userGender) {
        this.userGender = userGender;
    }

    public String getUserInterestedIn() {
        return userInterestedIn;
    }

    public void setUserInterestedIn(String userInterestedIn) {
        this.userInterestedIn = userInterestedIn;
    }

    public User(int userId, String firstName,String lastName, String userEmail, String userPhoneNumber, int userAge, String userGender, String userInterestedIn, LatLng coordinates) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userEmail = userEmail;
        this.userPhoneNumber = userPhoneNumber;
        this.userAge = userAge;
        this.userGender = userGender;
        this.userInterestedIn = userInterestedIn;
        this.coordinates = coordinates;
    }

    public User(int userId, String userEmail, LatLng coordinates) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.coordinates = coordinates;
    }

    public User(String userEmail) {
        this.userEmail = userEmail;
    }

    public User(int userId, String firstName, String lastName, String userEmail, String userPhoneNumber, int userAge, LatLng coordinates) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userEmail = userEmail;
        this.userPhoneNumber = userPhoneNumber;
        this.userAge = userAge;
        this.coordinates = coordinates;
    }

    public User(int userId, String firstName, String lastName, String userEmail, String userPhoneNumber, int userAge, String userGender, String userInterestedIn) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userEmail = userEmail;
        this.userPhoneNumber = userPhoneNumber;
        this.userAge = userAge;
        this.userGender = userGender;
        this.userInterestedIn = userInterestedIn;
    }
}
