package com.example.unlone.instance;

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
    private String label;
    private String createdTimestamp;     // for home fragment sorting
    private String createdDate;
    private Boolean comment;
    private Boolean share;


    //don't delete this or it will cause error
    public Post(){}

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Post(String uid, String username, String journal) throws ParseException {
        this.uid = uid;
        this.username = username;
        this.journal = journal;
        setCreatedTimestamp();
        setCreatedDate();
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

    public String getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(String createdDateTime) {
        this.createdTimestamp = createdDateTime;
    }

    public void setCreatedTimestamp() {
        Long tsLong = System.currentTimeMillis();
        this.createdTimestamp = tsLong.toString();
    }

    public String getCreatedDate(){
        return this.createdDate;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setCreatedDate()  throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        this.createdDate = formatter.format(new Date(Long.parseLong(this.getCreatedTimestamp())));
        }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Boolean getComment() {
        return comment;
    }

    public void setComment(Boolean comment) {
        this.comment = comment;
    }

    public Boolean getShare() {
        return share;
    }

    public void setShare(Boolean share) {
        this.share = share;
    }
}
