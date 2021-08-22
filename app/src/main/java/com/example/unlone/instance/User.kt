package com.example.unlone.instance;

import java.util.ArrayList;

public class User {
    private String uid;
    private String username;
    private String avatarImageUrl;
    private ArrayList<String> personae;
    private String persona;

    public User() {
    }

    public User(String uid, String username){
        this.uid = uid;
        this.username = username;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatarImageUrl() {
        return avatarImageUrl;
    }

    public void setAvatarImageUrl(String avatarImageUrl) {
        this.avatarImageUrl = avatarImageUrl;
    }

    public ArrayList<String> getPersonae() {
        return personae;
    }

    public void setPersonae(ArrayList<String> personae) {
        this.personae = personae;
    }

    public String getPersona() {
        return persona;
    }

    public void setPersona(String persona) {
        this.persona = persona;
    }

}
