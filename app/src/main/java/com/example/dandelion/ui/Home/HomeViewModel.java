package com.example.dandelion.ui.Home;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.dandelion.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<List<Post>> posts;
    private List<Post> postList;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String uid;

    public HomeViewModel() {
        posts = new MutableLiveData<>();
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getUid();
        postList = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }


    public LiveData<List<Post>> getPosts() {
        return posts;
    }



    public void loadPosts(int numberPost) {
        final int[] i = {0};
        Query ref = mDatabase.child("user-posts").child(uid).orderByChild("createdDateTime").limitToFirst(numberPost);
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Post post = dataSnapshot.getValue(Post.class);
                if (!postList.contains(post)) {
                    postList.add(post);
                    posts.setValue(postList);
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void addNewPost(String lastUpdateDate){
        Query ref = mDatabase.child("user-posts").child(uid).orderByChild("createdDateTime").startAt(lastUpdateDate).limitToFirst(10);
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    Post post = dataSnapshot.getValue(Post.class);
                    postList.add(post);
                    posts.setValue(postList);

                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}