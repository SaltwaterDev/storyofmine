package com.example.unlone.ui.lounge;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.unlone.instance.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<List<Post>> posts;
    private final List<Post> postList;
    private final FirebaseFirestore mFirestore;
    private DocumentSnapshot lastVisible = null;

    public HomeViewModel() {
        posts = new MutableLiveData<>();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String uid = mAuth.getUid();
        postList = new ArrayList<>();
        mFirestore = FirebaseFirestore.getInstance();
    }


    public LiveData<List<Post>> getPosts() {
        return posts;
    }



    public void loadPosts(int numberPost) {
        //postList.clear();

        if(lastVisible == null){
            Log.d(TAG, "First load");
            mFirestore.collection("posts")
                    .orderBy("createdTimestamp", Query.Direction.DESCENDING)
                    .limit(numberPost)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            lastVisible = Objects.requireNonNull(task.getResult()).getDocuments()
                                    .get(task.getResult().size() -1);
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    Post post = document.toObject(Post.class);
                                    post.setPid(document.getId());
                                    if (!postList.contains(post)) {
                                        postList.add(post);
                                        posts.setValue(postList);
                                    }

                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }else{
            mFirestore.collection("posts")
                    .orderBy("createdTimestamp", Query.Direction.DESCENDING)
                    .startAfter(lastVisible)
                    .limit(numberPost)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if(task.getResult().size()>0){
                                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        Post post = document.toObject(Post.class);
                                        post.setPid(document.getId());
                                        if (!postList.contains(post)) {
                                            postList.add(post);
                                            posts.setValue(postList);
                                        }
                                    }
                                    lastVisible = Objects.requireNonNull(task.getResult()).getDocuments()
                                            .get(task.getResult().size() -1);
                                }else{
                                    Log.d(TAG, "End of posts");
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
        }
    }

}