package com.example.unlone.ui;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.unlone.instance.Post;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class DetailedPostViewModel extends ViewModel {

    private MutableLiveData<Post> post;
    private List<Post> postList;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private String uid;

    public DetailedPostViewModel() {
        post = new MutableLiveData<>();
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getUid();
        postList = new ArrayList<>();
        mFirestore = FirebaseFirestore.getInstance();
    }


    public LiveData<Post> getObservablePost() {
        return post;
    }



    public void loadPost(String pid) {
        mFirestore.collection("posts")
                .document(pid)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Post p = documentSnapshot.toObject(Post.class);
                            post.setValue(p);
                    }
                });
    }


    //todo load comment

}