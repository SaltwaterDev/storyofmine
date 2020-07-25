package com.example.dandelion;

import java.util.HashMap;
import java.util.Map;

public class Post {
    private String pid;
    private String uid;
    private String username;
    private Boolean moods;
    private String text;
    private String thought;
    private String action;
    private String remindDate;

    //don't delete this or it will cause error
    public Post(){}

    public Post(String pid, String uid, String username, Boolean moods, String text, String thought, String action, String remindDate){
        this.pid = pid;
        this.uid = uid;
        this.username = username;
        this.moods = moods;
        this.text = text;
        this.thought = thought;
        this.action = action;
        this.remindDate = remindDate;
    }

    public Post(String pid, String uid, String username, String text){
        this.pid = pid;
        this.uid = uid;
        this.username = username;
        this.text = text;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    public String getRemindDate() {
        return remindDate;
    }

    public void setRemindDate(String remindDate) {
        this.remindDate = remindDate;
    }

    public Map<String, Object> toMap(){
       HashMap<String, Object> result = new HashMap<>();
       result.put("pid",pid);
       result.put("uid",uid);
       result.put("username",username);
       result.put("moods",moods);
       result.put("text",text);
       result.put("remindDate",remindDate);
       return result;
    }

    @Override
    public boolean equals(@androidx.annotation.Nullable Object obj) {
        Post post = (Post) obj;
        return pid.matches(post.getPid());
    }

}
