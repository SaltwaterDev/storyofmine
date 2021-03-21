package com.example.dandelion.ui;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.dandelion.instance.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.content.ContentValues.TAG;

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