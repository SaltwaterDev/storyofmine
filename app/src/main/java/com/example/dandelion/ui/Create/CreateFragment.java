package com.example.dandelion.ui.Create;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.dandelion.instance.Post;
import com.example.dandelion.R;
import com.example.dandelion.instance.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;


public class CreateFragment extends Fragment {

    private CreateViewModel createViewModel;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private EditText journal, remind_day, title;
    private Button submit;

    Date date = new Date();
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private String createdDateTime = dateTimeFormatter.format(date);

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        createViewModel = new ViewModelProvider(this).get(CreateViewModel.class);
        View root = inflater.inflate(R.layout.fragment_create, container, false);
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        title = root.findViewById(R.id.editText_title);
        journal = root.findViewById(R.id.editText_journal);
        //remind_day = root.findViewById(R.id.editText_remind_day);
        submit = root.findViewById(R.id.button_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPost();
            }
        });
        return root;
    }

    private void submitPost(){
        final String pTitle = title.getText().toString();
        final String pEvent = journal.getText().toString();
        //final int pRemind_day = Integer.parseInt(remind_day.getText().toString());
        if(pEvent.matches("")){
            Toast.makeText(getActivity(), "You did not complete the post.", Toast.LENGTH_SHORT).show();
            return;
        }
        setEditingEnabled(false);
        Toast.makeText(getActivity(), "Posting...", Toast.LENGTH_SHORT).show();
        final String uid = mAuth.getUid();
        assert uid != null;
        DocumentReference docRef = mFirestore.collection("users").document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        User user = document.toObject(User.class);
                        saveNewPost(uid, user.getUsername(), pTitle, pEvent, createdDateTime);
                        setEditingEnabled(true);
                        finishPosting();
                        requireActivity().onBackPressed();

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    // I want to add a feature that user shall add review for their journal
    // by attaching new post underneath, rather than editing the existing post
    private void setEditingEnabled(boolean enabled) {
        title.setEnabled(enabled);
        journal.setEnabled(enabled);
        submit.setEnabled(enabled);
    }


    private void saveNewPost(String uid, String username, String title, String journal, String createdDateTime){
        //String key = mDatabase.child("posts").push().getKey();
        Date currentDate = Calendar.getInstance().getTime();
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        //c.add(Calendar.DATE, remindDay);

        Post post = new Post(uid, username, journal, createdDateTime);
        if (title != null)
            Log.d("CREATEFRAGMENT", title);
            post.setTitle(title);
        Map<String, Object> postValues = post.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        //childUpdates.put("/posts/" + category + "/" + key, postValues);   todo...
        mFirestore.collection("posts").add(post).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        setEditingEnabled(true);
                    }
                });

        mFirestore.collection("users").document(uid).collection("user-posts").add(post).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        setEditingEnabled(true);
                    }
                });
    }

    private void finishPosting(){
        title.setText(null);
        journal.setText(null);
    }

}