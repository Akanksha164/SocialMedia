package com.example.instagram;

public class ProfilePictureModel {

    private String profilePicture;

    public ProfilePictureModel() {
        // empty constructor
        // required for Firebase.
    }

    // Constructor for all variables.
    public ProfilePictureModel(String profilePicture) {
        this.profilePicture = profilePicture;
         }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

}
