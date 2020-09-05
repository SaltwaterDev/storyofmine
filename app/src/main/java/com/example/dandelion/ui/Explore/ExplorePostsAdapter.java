package com.example.dandelion.ui.Explore;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dandelion.instance.Post;
import com.example.dandelion.ui.PostsAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

import static android.content.ContentValues.TAG;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ExplorePostsAdapter extends PostsAdapter{


    public ExplorePostsAdapter(Context context) {
        super(context);
    }

    @Override
    public void onBindViewHolder(@NonNull PostsAdapter.ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        holder.username.setVisibility(View.VISIBLE);
    }



}
