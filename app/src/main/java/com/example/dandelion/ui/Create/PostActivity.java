package com.example.dandelion.ui.Create;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.example.dandelion.R;
import com.example.dandelion.instance.Post;
import com.example.dandelion.instance.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class PostActivity extends AppCompatActivity {


    private CreateViewModel createViewModel;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private StorageReference storageReference;

    private EditText journal, title;
    private ImageView imagePost;
    StorageTask uploadTask;

    Date date = new Date();
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat dateTimeFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    private String createdDateTime = dateTimeFormatter.format(date);

    private String setSelectedImagePath;
    Uri selectedImageUri;

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        // init database storing
        createViewModel = new ViewModelProvider(this).get(CreateViewModel.class);
        mAuth = FirebaseAuth.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("posts");

        // init toolbar
        Toolbar post_toolbar = findViewById(R.id.post_toolbar);
        if (post_toolbar != null) {
            setSupportActionBar(post_toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        title = findViewById(R.id.inputPostTitle);
        journal = findViewById(R.id.inputPostContext);
        imagePost = findViewById(R.id.imagePost);

        setSelectedImagePath="";
        initMiscellaneous();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.post_button:
                submitPost();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void submitPost(){
        final String pTitle = title.getText().toString();
        final String pJournal = journal.getText().toString();
        if(pJournal.matches("")){
            Toast.makeText(getApplicationContext(), "You did not complete the post.", Toast.LENGTH_SHORT).show();
            return;
        }
        setEditingEnabled(false);
        Toast.makeText(getApplicationContext(), "Posting...", Toast.LENGTH_SHORT).show();


        // Upload post
        final String uid = mAuth.getUid();
        assert uid != null;

        //upload image
        if (selectedImageUri != null){
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    +"." + getFileExtension(selectedImageUri));

            uploadTask = fileReference.putFile(selectedImageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }

                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Uri downloadUri = (Uri) task.getResult();
                        assert downloadUri != null;
                        setSelectedImagePath = downloadUri.toString();
                        Toast.makeText(PostActivity.this, "setSelectedImagePath: "+ setSelectedImagePath, Toast.LENGTH_SHORT).show();

                        //upload rest of the content
                        DocumentReference docRef = mFirestore.collection("users").document(uid);
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                        User user = document.toObject(User.class);

                                        saveNewPost(uid, user.getUsername(), pTitle, setSelectedImagePath, pJournal, createdDateTime);
                                        setEditingEnabled(true);
                                        finishPosting();
                                        finish();

                                    } else {
                                        Log.d(TAG, "No such document");
                                    }
                                } else {
                                    Log.d(TAG, "get failed with ", task.getException());
                                }
                            }
                        });

                    }else{
                        Toast.makeText(PostActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(this, "No Image Selected!", Toast.LENGTH_SHORT).show();
        }

    }

    // I want to add a feature that user shall add review for their journal
    // by attaching new post underneath, rather than editing the existing post
    private void setEditingEnabled(boolean enabled) {
        title.setEnabled(enabled);
        journal.setEnabled(enabled);
    }

    private void initMiscellaneous(){
        final LinearLayout layoutMiscellaneous = findViewById(R.id.layoutMiscellaneous);
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(layoutMiscellaneous);
        layoutMiscellaneous.findViewById(R.id.textMiscellaneous).setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else{
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        layoutMiscellaneous.findViewById(R.id.layoutAddImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                if(ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            PostActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_STORAGE_PERMISSION
                    );
                }else{
                    selectImage();
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                selectImage();
            }else{
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void selectImage() {
        CropImage.activity()
                .start(PostActivity.this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            assert result != null;
            selectedImageUri = result.getUri();
            if(selectedImageUri != null){
                try{
                    imagePost.setImageURI(selectedImageUri);
                    imagePost.setVisibility(View.VISIBLE);
                }catch (Exception exception){
                    Log.d("onActivityResult", Objects.requireNonNull(exception.getMessage()));
                    Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG).show();
                    finish();
                }
            }else
                Toast.makeText(this, "can't display image because selectedImage is null", Toast.LENGTH_LONG).show();
        }
    }


    private void saveNewPost(String uid, String username, String title, String ImagePath, String journal, String createdDateTime){
        //String key = mDatabase.child("posts").push().getKey();
        Date currentDate = Calendar.getInstance().getTime();
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);

        Post post = new Post(uid, username, journal, createdDateTime);
        if (title != null) {
            Log.d("CREATEFRAGMENT", title);
            post.setTitle(title);
        }
        if (ImagePath != null){
            Log.d("CREATEFRAGMENT", ImagePath);
            post.setImagePath(ImagePath);
        }

        //Map<String, Object> postValues = post.toMap();
        //Map<String, Object> childUpdates = new HashMap<>();

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

    }

    private void finishPosting(){
        title.setText(null);
        journal.setText(null);
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getPathFromUri(Uri contentUri){
        String filePath;
        Cursor cursor = getContentResolver()
                .query(contentUri, null, null, null);
        if(cursor == null){
            filePath = contentUri.getPath();
        }else{
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("data");
            filePath = cursor.getString(index);
            cursor.close();
        }
        return filePath;
    }
}