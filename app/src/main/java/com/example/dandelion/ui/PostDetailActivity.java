package com.example.dandelion.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dandelion.R;
import com.example.dandelion.instance.Comment;
import com.example.dandelion.instance.Post;
import com.example.dandelion.instance.User;
import com.example.dandelion.ui.Home.HomeViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.List;

public class PostDetailActivity extends AppCompatActivity {

    private static final String TAG = "PostDetailedActivity";
    private DetailedPostViewModel detailedPostViewModel;
    protected FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private Post post;
    private Comment comment;

    // detail of user and the post
    String uid, username, pid;

    // views
    public TextView author;
    public TextView title;
    public TextView journal;
    private TextView date;
    private ImageView imageCover;

    //add comment views
    EditText commentEt;
    ImageButton sendBtn;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_post_detail);

        // init database storing
        this.mFirestore = FirebaseFirestore.getInstance();
        detailedPostViewModel = new DetailedPostViewModel();
        mAuth = FirebaseAuth.getInstance();

        // get id of the post using intent
        Intent intent = getIntent();
        pid = intent.getStringExtra("postId");


        //init views
        title = (TextView) findViewById(R.id.textView_title);
        date = (TextView) findViewById(R.id.date);
        author = (TextView) findViewById(R.id.author);
        journal = (TextView) findViewById(R.id.textView_journal);
        imageCover = findViewById(R.id.imageCover);

        commentEt = findViewById(R.id.commentEt);
        sendBtn = findViewById(R.id.sendBtn);

        // load info
        detailedPostViewModel = new ViewModelProvider(this).get(DetailedPostViewModel.class);
        detailedPostViewModel.loadPost(pid);
        loadPostInfo();

        // send comment button click
        sendBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                postComment();
                commentEt.getText().clear();
            }

        });
    }

    private void loadPostInfo() {

        detailedPostViewModel.getObservablePost().observe(this, new Observer<Post>() {

            @Override
            public void onChanged(@Nullable Post p) {

                title.setText(p.getTitle());

                // display image
                String image_path = p.getImagePath();
                try{
                    Picasso.get().load(image_path).into(imageCover);
                    imageCover.setVisibility(View.VISIBLE);
                }catch (Exception e){
                    imageCover.setVisibility(View.GONE);
                }

                // display journal text
                journal.setText(p.getJournal());
                date.setText(p.getCreatedDate());

                //display author
                setAuthor(p);

            }

        });
    }

    private void postComment(){

        //get data from comment edit text
        final String comment_content = commentEt.getText().toString().trim();
        // validate
        if (TextUtils.isEmpty(comment_content)){
            // no value is entered
            Toast.makeText(this, "Comment is empty", Toast.LENGTH_SHORT).show();
            return;
        }



        DocumentReference docRef = mFirestore.collection("users").document(mAuth.getUid());
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    System.err.println("Listen failed: " + error);
                    return;
                }

                if (value != null && value.exists()) {
                    System.out.println("Current data: " + value.getData());
                    User user = value.toObject(User.class);

                    String author_uid = mAuth.getUid();
                    String author_username = user.getUsername();
                    comment = new Comment(author_uid, author_username, comment_content);

                    // add comment to the database
                    mFirestore.collection("posts").document(pid)
                            .collection("comments")
                            .add(comment)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error adding comment\n", e);
                                }
                            });

                } else {
                    System.out.print("Current data: null");
                }
            }
        });




    }
    private void setAuthor(Post post){
        String uid = post.getUid();
        final String[] name = new String[1];
        DocumentReference docRef = this.mFirestore.collection("users").document(uid);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {

            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    System.err.println("Listen failed: " + error);
                    return;
                }
                if (value != null && value.exists()) {
                    User user = value.toObject(User.class);
                    assert user != null;
                    author.setText(user.getUsername());
                } else {
                    System.out.print("Current data: null");
                }
            }
        });
    }
}