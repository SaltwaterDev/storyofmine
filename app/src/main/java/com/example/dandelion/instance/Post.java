package com.example.dandelion.instance;

import android.annotation.SuppressLint;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class Post {
    private String pid;
    private String uid;
    private String username;
    private String title;
    private String imagePath;
    private String journal;
    //private String thought;
    //private String action;
    private String category;
    private String createdDateTime;     // for home fragment sorting
    private String createdDate;
    private Integer likes;
    private boolean expanded;



    //don't delete this or it will cause error
    public Post(){}

    public Post(String uid, String username, String journal, String createdDateTime){
        //this.pid = pid;
        this.uid = uid;
        this.username = username;
        this.journal = journal;
        this.expanded = false;
        this.createdDateTime = createdDateTime;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
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

    public void setUsername(String author_username) {
        this.username = author_username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getJournal() {
        return journal;
    }

    public void setJournal(String journal) {
        this.journal = journal;
    }

/*
    public String getThought() {
        return thought;
    }

    public void setThought(String thought) {
        this.thought = thought;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }*/

    public String getCreatedDateTime() {
        return createdDateTime;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public void setCreatedDateTime(String createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public String getCreatedDate() throws ParseException {

        final String OLD_FORMAT = "dd-MM-yyyy HH:mm:ss";
        final String NEW_FORMAT = "dd-MM-yyyy";

        String oldDateString = getCreatedDateTime();
        String newDateString;

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT);
        Date d = sdf.parse(oldDateString);
        sdf.applyPattern(NEW_FORMAT);
        assert d != null;
        newDateString = sdf.format(d);

        return newDateString;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public Map<String, Object> toMap(){
       HashMap<String, Object> result = new HashMap<>();
       result.put("pid",pid);
       result.put("uid",uid);
       result.put("username",username);
       result.put("createdDateTime",createdDateTime);
       result.put("imagePath",imagePath);
       //result.put("category",category);   todo...ML predict category
       result.put("title",title);
       result.put("journal",journal);
       return result;
    }

}
