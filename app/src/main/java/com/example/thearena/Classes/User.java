package com.example.thearena.Classes;

import android.graphics.Bitmap;

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
    private double latitude;
    private double longitude;

    private Bitmap profilePic;


    public double getUserLatitude() {
        return latitude;
    }

    public void setUserLatitude(double latitude) { this.latitude = latitude; }

    public double getUserLongitude() {
        return longitude;
    }

    public void setUserLongitude(double longitude) { this.longitude = longitude; }



    public void setProfilePic(Bitmap profilePic) { this.profilePic = profilePic; }

    public Bitmap getProfilePic() {
        return profilePic;
    }

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
        return this.userGender;
    }

    public void setUserGender(String gender) {
        this.userGender = gender;
    }

    public String getUserInterestedIn(){
        return this.userInterestedIn;
    }

    public void setUserInterestedIn(String interestedInWomen) {
        this.userInterestedIn = interestedInWomen;
    }


    public User(int userId, String firstName,String lastName, String userEmail, String userPhoneNumber, int userAge, String userGender, String userInterestedIn, LatLng coordinates) {
        setUserId(userId);
        setFirstName(firstName);
        setLastName(lastName);
        setUserEmail(userEmail);
        setUserPhoneNumber(userPhoneNumber);
        setUserAge(userAge);
        setUserGender(userGender);
        setUserInterestedIn(userInterestedIn);
        this.coordinates = coordinates;
    }
    public User() {

    }
    public User(int userId, String userEmail, LatLng coordinates) {
        setUserId( userId);
        setUserEmail( userEmail);
        this.coordinates = coordinates;
    }

    public User(String userEmail) {
        setUserEmail(userEmail);
    }

    public User(int userId, String firstName, String lastName, String userEmail, String userPhoneNumber, int userAge, LatLng coordinates) {
        setUserId( userId);
        setFirstName( firstName);
        setLastName( lastName);
        setUserEmail(userEmail);
        setUserPhoneNumber( userPhoneNumber);
        setUserAge( userAge);
        this.coordinates = coordinates;
    }

    public User(int userId, String firstName, String lastName, String userEmail, String userPhoneNumber, int userAge, String userGender, String userInterestedIn) {
        setUserId(userId);
        setFirstName(firstName);
        setLastName(lastName);
        setUserEmail(userEmail);
        setUserPhoneNumber(userPhoneNumber);
        setUserAge(userAge);
        setUserGender(userGender);
        setUserInterestedIn( userInterestedIn);
    }

}
