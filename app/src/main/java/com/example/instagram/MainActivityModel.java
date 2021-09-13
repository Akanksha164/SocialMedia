package com.example.instagram;

public class MainActivityModel {

    // variables for storing our data.
    private String userName, userEmail, userPhone, bio,profilePicture;

    public MainActivityModel() {
        // empty constructor
        // required for Firebase.
    }

    // Constructor for all variables.
    public MainActivityModel(String userName, String userEmail, String userPhone) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
    }

    public MainActivityModel(String bio, String userName) {
        this.bio = bio;
        this.userName = userName;
    }

    public MainActivityModel(String userName, String userEmail, String userPhone,String profilePicture){
        this.profilePicture=profilePicture;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
    }

    public String getuserName() {
        return userName;
    }

    public void setuserName(String userName) {
        this.userName = userName;
    }

    public String getuserEmail() {
        return userEmail;
    }

    public void setuserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getuserPhone() {
        return userPhone;
    }

    public void setuserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getbio() {
        return bio;
    }

    public void setbio(String bio) {
        this.bio = bio;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}


