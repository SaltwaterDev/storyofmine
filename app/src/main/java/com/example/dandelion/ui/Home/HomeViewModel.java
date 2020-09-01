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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<List<Post>> posts;
    private List<Post> postList;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private String uid;

    public HomeViewModel() {
        posts = new MutableLiveData<>();
        mAuth = FirebaseAuth.getInstance();
        uid = mAuth.getUid();
        postList = new ArrayList<>();
        mFirestore = FirebaseFirestore.getInstance();
    }


    public LiveData<List<Post>> getPosts() {
        return posts;
    }



    public void loadPosts(int numberPost) {
        Task<QuerySnapshot> docRef = mFirestore.collection("users").document(uid).collection("user-posts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Post post = document.toObject(Post.class);
                                if (!postList.contains(post)){
                                    postList.add(post);
                                    posts.setValue(postList);
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

}