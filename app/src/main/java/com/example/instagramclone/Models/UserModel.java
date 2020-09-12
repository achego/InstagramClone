package com.example.instagramclone.Models;

public class UserModel {

    private String username, fullname ,email, id, bio, profileImage;

    public UserModel(String username, String fullname, String email, String id, String bio, String profileImage) {
        this.username = username;
        this.fullname = fullname;
        this.email = email;
        this.id = id;
        this.bio = bio;
        this.profileImage = profileImage;
    }

    public UserModel() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
