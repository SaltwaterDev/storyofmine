package com.example.unlone.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.unlone.R;
import com.example.unlone.instance.Post;
import com.example.unlone.instance.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RequiresApi(api = Build.VERSION_CODES.O)
public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder>{

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private List<Post> postList;
    Context context;
    private String uid;
    protected FirebaseFirestore mFirestore;


    public PostsAdapter(Context context) {
        this.context = context;
        this.mAuth = FirebaseAuth.getInstance();
        this.mFirestore = FirebaseFirestore.getInstance();
        this.uid = mAuth.getUid();
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CardView card;
        public TextView title;
        public TextView journal;
        private TextView date;
        private ImageView imageCover;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card_view);
            title = (TextView) itemView.findViewById(R.id.textView_title);
            date = (TextView) itemView.findViewById(R.id.date);
            journal = (TextView) itemView.findViewById(R.id.textView_journal);
            imageCover = itemView.findViewById(R.id.imageCover);


            Log.d("PostsAdapter", "go in posts adapter");
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Post post = postList.get(position);

        holder.title.setText(post.getTitle());

        // display image
        String image_path = post.getImagePath();
        try{
            Picasso.get().load(image_path).into(holder.imageCover);
            holder.imageCover.setVisibility(View.VISIBLE);
        }catch (Exception e){
            holder.imageCover.setVisibility(View.GONE);
        }

        // display journal text
        holder.journal.setText(post.getJournal());
        //holder.date.setText(post.getCreatedDate());


        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("postId", post.getPid());
                context.startActivity(intent);
            }
        });
    }

    public void setPostList(List<Post> postList) {
        this.postList = postList;
    }

    @Override
    public int getItemCount() {
        if(postList == null)
            return 0;
        return postList.size();
    }

    private void setAuthor(Post post, final ViewHolder holder){
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
            }
        });
    }



    //todo
    public void sendComment(final String pid, final String content){
        final String key = mDatabase.child("comments").push().getKey();
        final User[] current = new User[1];
        Log.d("COMMENT", uid);
        mDatabase.child("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                current[0] = dataSnapshot.getValue(User.class);
                assert current[0] != null;
                //Comment comment = new Comment(key, uid, current[0].getUsername(), /*current[0].getAvatarImageUrl(),*/ content, timeStamp);
                //Map<String, Object> commentValues = comment.toMap();
                Map<String, Object> childUpdates = new HashMap<>();
                //childUpdates.put("/post-comments/" + pid + "/" + key, commentValues);
                mDatabase.updateChildren(childUpdates);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}