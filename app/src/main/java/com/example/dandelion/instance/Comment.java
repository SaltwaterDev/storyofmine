package com.example.dandelion.instance;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@RequiresApi(api = Build.VERSION_CODES.O)
public class Comment {
    private String cid;
    private String author_uid;
    private String author_username;
    private String content;
    private String timestamp;
    private float score;

    public Comment(){
        this.timestamp = (String) LocalDateTime.now().toString();
    }

    public Comment(String author_uid, String author_username, String content) {
        this.author_uid = author_uid;
        this.author_username = author_username;
        this.content = content;
        this.timestamp = (String) LocalDateTime.now().toString();
    }



    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getAuthor_uid() {
        return author_uid;
    }

    public void setAuthor_uid(String author_uid) {
        this.author_uid = author_uid;
    }

    public String getAuthor_username() {
        return author_username;
    }

    public void setAuthor_username(String author_username) {
        this.author_username = author_username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

        public Map<String, Object> toMap(){
            HashMap<String, Object> result = new HashMap<>();
            result.put("cid", cid);
            result.put("author_uid", author_uid);
            result.put("author_username", author_username);
            result.put("content", content);
            result.put("timestamp", timestamp);
        return result;
    }
}
