package com.example.dandelion.ui;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dandelion.R;
import com.example.dandelion.instance.Post;
import com.example.dandelion.instance.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;


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
        public TextView username;
        public TextView title;
        public TextView journal;
        private TextView date;
        public Button comment;
        public Button commentSend;
        public Button editPost;
        public EditText commentContent;
        public Group commentGroup;
        public RecyclerView commentRecyclerView;
        public ConstraintLayout title_and_date;
        public ConstraintLayout expandableLayout;
        private RoundedImageView imageNote;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.textView_title);
            date = (TextView) itemView.findViewById(R.id.date);
            username = (TextView) itemView.findViewById(R.id.username);
            journal = (TextView) itemView.findViewById(R.id.textView_event);
            commentContent = (EditText) itemView.findViewById(R.id.editText_comment);
            commentSend = (Button) itemView.findViewById(R.id.button_send);
            title_and_date = itemView.findViewById(R.id.title_and_date);
            imageNote = itemView.findViewById(R.id.imageNote);
            expandableLayout = itemView.findViewById(R.id.expandableLayout);
            //commentRecyclerView = (RecyclerView) itemView.findViewById(R.id.recyclerView_comment);
            //commentGroup.setVisibility(View.GONE);
            title_and_date.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Post post = postList.get(getAdapterPosition());
                    post.setExpanded(!post.isExpanded());
                    notifyItemChanged(getAdapterPosition());
                }
            });

            Log.d("PostsAdapter", "go in posts adapter");
        }

        /*private void resetComment(boolean enabled) {
            commentSend.setEnabled(enabled);
            commentContent.setEnabled(enabled);
            commentContent.setText(null);
        }*/

        /*private void fetchComment(String pid, ChildEventListener listener){
            Query ref = mDatabase.child("post-comments").child(pid);
            ref.addChildEventListener(listener);
        }*/
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
        //final List<Comment> commentList = new ArrayList<>(); todo

        holder.title.setText(post.getTitle());

        if(post.getImagePath() != null){
            holder.imageNote.setImageBitmap(BitmapFactory.decodeFile(post.getImagePath()));
            holder.imageNote.setVisibility(View.VISIBLE);
        }else{
            holder.imageNote.setVisibility(View.GONE);
        }

        holder.journal.setText(post.getJournal());
        try {
            holder.date.setText(post.getCreatedDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        boolean isExpanded = postList.get(position).isExpanded();
        holder.expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        setAuthor(post, holder);
        if(!post.getUid().matches(uid)){
            holder.username.setVisibility(View.VISIBLE);
        }else{
            holder.username.setVisibility(View.GONE);
        }

        /*if(post.getUid().matches(uid)){
            holder.editPost.setVisibility(View.VISIBLE);
        }else{
            holder.editPost.setVisibility(View.GONE);
        }
        holder.editPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Post
            }
        });*/ // todo...edit button

        /*holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.commentGroup.getVisibility() == View.GONE) {
                    holder.commentGroup.setVisibility(View.VISIBLE);
                }else {
                    holder.commentGroup.setVisibility(View.GONE);
                }
            }
        });*/
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
                if (value != null && value.exists()) {
                    User user = value.toObject(User.class);
                    assert user != null;
                    holder.username.setText(user.getUsername());
                } else {
                    System.out.print("Current data: null");
                }
            }
        });
    }


    public String getLastItemDate(){
        String lastItemDate;
        try {
            lastItemDate = postList.get(postList.size()-1).getCreatedDateTime();
        } catch (Exception e) {
            Log.e("Post Adapter Exception", String.valueOf(e));
            return "";
        }
        return postList.get(postList.size()-1).getCreatedDateTime();
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