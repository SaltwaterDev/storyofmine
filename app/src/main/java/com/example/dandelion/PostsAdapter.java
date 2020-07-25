package com.example.dandelion;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder>{

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private List<Post> postList;
    Context context;
    private String uid;

    public PostsAdapter(Context context) {
        this.context = context;
        this.mAuth = FirebaseAuth.getInstance();
        this.uid = mAuth.getUid();
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username;
        public TextView event;
        public TextView thought;
        public TextView action;
        public Button comment;
        public Button commentSend;
        public Button editPost;
        public EditText commentContent;
        public Group commentGroup;
        public RecyclerView commentRecyclerView;
        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            event = (TextView) itemView.findViewById(R.id.textView_event);
            commentSend = (Button) itemView.findViewById(R.id.button_send);
            commentContent = (EditText) itemView.findViewById(R.id.editText_comment);
            commentRecyclerView = (RecyclerView) itemView.findViewById(R.id.recyclerView_comment);
            commentGroup.setVisibility(View.GONE);
        }

        private void resetComment(boolean enabled) {
            commentSend.setEnabled(enabled);
            commentContent.setEnabled(enabled);
            commentContent.setText(null);
        }

        private void fetchComment(String pid, ChildEventListener listener){
            Query ref = mDatabase.child("post-comments").child(pid);
            ref.addChildEventListener(listener);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_post, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Post post = postList.get(position);
        final List<Comment> commentList = new ArrayList<>();
        holder.username.setText(post.getUsername());
        holder.event.setText(post.getText());
        holder.thought.setText(post.getThought());
        holder.action.setText(post.getAction());
        if(post.getUid().matches(uid)){
            holder.editPost.setVisibility(View.VISIBLE);
        }else{
            holder.editPost.setVisibility(View.GONE);
        }
        holder.editPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Post
            }
        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.commentGroup.getVisibility() == View.GONE) {
                    holder.commentGroup.setVisibility(View.VISIBLE);
                }else {
                    holder.commentGroup.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    //todo
    public void sendComment(final String pid, final String content){
        final String key = mDatabase.child("comments").push().getKey();
        final User[] current = new User[1];
        Log.d("COMMENT", uid);
        final String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        mDatabase.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                current[0] = dataSnapshot.getValue(User.class);
                assert current[0] != null;
                Comment comment = new Comment(key, uid, current[0].getUsername(), current[0].getAvatarImageUrl(), content, timeStamp);
                Map<String, Object> commentValues = comment.toMap();
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put("/post-comments/" + pid + "/" + key, commentValues);
                mDatabase.updateChildren(childUpdates);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}