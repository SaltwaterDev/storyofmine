package com.example.dandelion.ui.Create;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.example.dandelion.Post;
import com.example.dandelion.R;
import com.example.dandelion.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CreateFragment extends Fragment {

    private CreateViewModel createViewModel;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private EditText journal, remind_day;
    private Button submit;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        createViewModel = new ViewModelProvider(this).get(CreateViewModel.class);
        View root = inflater.inflate(R.layout.fragment_create, container, false);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        journal = root.findViewById(R.id.editText_journal);
        remind_day = root.findViewById(R.id.editText_remind_day);
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
        final String pEvent = journal.getText().toString();
        final int pRemind_day = Integer.parseInt(remind_day.getText().toString());
        if(pEvent.matches("")){
            Toast.makeText(getActivity(), "You did not complete the post.", Toast.LENGTH_SHORT).show();
            return;
        }
        setEditingEnabled(false);
        Toast.makeText(getActivity(), "Posting...", Toast.LENGTH_SHORT).show();
        final String userId = mAuth.getUid();
        assert userId != null;
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user == null) {
                            Log.d("CREATEFRAGMENT", "User " + userId + " is unexpectedly null");
                            Toast.makeText(getActivity(),
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            writeNewPost(userId, user.getUsername(), pEvent, pRemind_day);
                        }
                        setEditingEnabled(true);
                        finishPosting();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w("CREATEFRAGMENT", "getUser:onCancelled", databaseError.toException());
                        setEditingEnabled(true);
                    }
                });
    }

    // I want to add a feature that user shall add review for their journal by attaching new post underneath
    private void setEditingEnabled(boolean enabled) {
        journal.setEnabled(enabled);
        submit.setEnabled(enabled);
    }

    private void writeNewPost(String uid, String username, String journal, int remindDay){
        String key = mDatabase.child("posts").push().getKey();
        Date currentDate = Calendar.getInstance().getTime();
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        c.add(Calendar.DATE, remindDay);
        @SuppressLint("SimpleDateFormat") String remindDate = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date(c.getTimeInMillis()));
        Post post = new Post(key, uid, username, journal);
        Map<String, Object> postValues = post.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        //childUpdates.put("/posts/" + category + "/" + key, postValues);   todo...
        childUpdates.put("/posts/" + key, postValues);
        childUpdates.put("/user-posts/" + uid + "/" + key, postValues);
        mDatabase.updateChildren(childUpdates);
    }

    private void finishPosting(){
        journal.setText(null);
        remind_day.setText(null);
    }

}