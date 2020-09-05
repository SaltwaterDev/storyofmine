package com.example.dandelion.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dandelion.R;
import com.example.dandelion.instance.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoadActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        setContentView(R.layout.activity_load);

        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        View decorView = getWindow().getDecorView();
        int uiOption = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOption);

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            Log.d("LOADACTIVITY", "first time login");
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    startActivity(new Intent(LoadActivity.this, FirstAccessActivity.class));
                    finish();
            }
        }, 2000);
    }
        else{
            mFirestore.collection("users").document(currentUser.getUid()).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Log.d("LOADACTIVITY", "GET USER");
                            User current = documentSnapshot.toObject(User.class);
                            assert current != null;
                            Toast.makeText(LoadActivity.this, "Welcome Back "+ current.getUsername(),
                                    Toast.LENGTH_SHORT).show();

                            Log.d("LOADACTIVITY", "login: "+currentUser.getUid());
                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    startActivity(new Intent(LoadActivity.this, MainActivity.class));
                                    finish();
                                }
                            }, 2000);
                        }
                    });

        }
    }

}
