package com.example.dandelion;

import android.annotation.SuppressLint;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.O)
public class Post {
    private String pid;
    private String uid;
    private String username;
    private String journal;
    private String thought;
    private String action;
    private String category;

    Date date = new Date();
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
    private String createdDateTime = dateTimeFormatter.format(date);
    private String createdDate = dateFormatter.format(date);


    //don't delete this or it will cause error
    public Post(){}


    public Post(String pid, String uid, String username, String journal){
        this.pid = pid;
        this.uid = uid;
        this.username = username;
        this.journal = journal;
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

    public String getJournal() {
        return journal;
    }

    public void setJournal(String journal) {
        this.journal = journal;
    }

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
    }

    public String getCreatedDateTime() {
        return createdDateTime;
    }

    public void setCreatedDateTime(String createdDateTime) {
        this.createdDateTime = createdDateTime;
    }

    public String getCreatedDate() {
        return createdDate;
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

    public Map<String, Object> toMap(){
       HashMap<String, Object> result = new HashMap<>();
       result.put("pid",pid);
       result.put("uid",uid);
       result.put("username",username);
        result.put("createdDateTime",createdDateTime);
       //result.put("category",category);   todo...ML predict category
       result.put("journal",journal);
       return result;
    }


    public boolean equals(Object obj) {
        Post post = (Post) obj;
        assert post != null;
        return pid.matches(post.getPid());
    }

}
