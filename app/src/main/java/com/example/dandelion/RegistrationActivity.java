package com.example.dandelion;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;
import java.util.UUID;

public class RegistrationActivity extends AppCompatActivity {

    private Button avatar;
    private Button register;
    private Uri userUri;
    private EditText username;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        register = (Button) findViewById(R.id.button_register);
        username = (EditText) findViewById(R.id.editText_username);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        register.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View view){
                if(username.getText().toString().matches("")){
                    Toast.makeText(RegistrationActivity.this, "Please input your username",
                            Toast.LENGTH_SHORT).show();
                }
                else{
                    performRegister();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void performRegister() {
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("REGISTRATION", "start perform registration");
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("REGISTRATION", "signInAnonymously:success");
                            progressBar.setVisibility(View.VISIBLE);
                            FirebaseUser user = mAuth.getCurrentUser();
                            saveUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("REGISTRATION", "signInAnonymously:failure", task.getException());
                            Toast.makeText(RegistrationActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void uploadUserToFirebase(){
        userUri = null;
        String filename = UUID.randomUUID().toString();
        final StorageReference ref = FirebaseStorage.getInstance().getReference("/users/"+filename);
        ref.putFile(userUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onSuccess(Uri uri) {
                        saveUser();
                    }
                });
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void saveUser(){
        String uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        Log.d("REGISTRATION", "save user uid: "+uid);
        Log.d("REGISTRATION", "save user username: "+username.getText().toString());
        User user = new User(uid, username.getText().toString());
        mDatabase.child("users").child(uid).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    progressBar.setVisibility(View.GONE);
                    Log.d("REGISTRATION", "user saved");
                    Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                    startActivity(intent);
                    Toast.makeText(RegistrationActivity.this, "Account Created",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    Log.d("REGISTRATION", Objects.requireNonNull(Objects.requireNonNull(task.getException()).getMessage()));
                }
            }
        });
    }
}
