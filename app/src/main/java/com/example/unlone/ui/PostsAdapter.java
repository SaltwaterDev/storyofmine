package com.example.unlone.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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
    protected StorageReference storageReference;

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

        // set title
        holder.title.setText(post.getTitle());

        // set text
        holder.journal.setText(post.getJournal());

        // set image
        storageReference = FirebaseStorage.getInstance().getReference("posts");
        String image_path = post.getImagePath();


        // control what to display on post view
        if (!image_path.isEmpty()){

            // load image and resize it
            final int[] image_width = new int[1];
            final int[] image_height = new int[1];

            // action wil be done when load the image
            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                    holder.imageCover.setVisibility(View.VISIBLE);
                    holder.title.setVisibility(View.VISIBLE);
                    holder.journal.setVisibility(View.GONE);
                    holder.title.setGravity(Gravity.CENTER);

                    //get measured image size
                    image_width[0] = bitmap.getWidth();
                    image_height[0] = bitmap.getHeight();
                    holder.imageCover.setImageBitmap(bitmap);
                    Log.d("Bitmap Dimensions: ", image_width[0] + "x" + image_height[0]);

                    LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.gravity=Gravity.CENTER;

                    int image_horizontal_margin = getImageVerticalMargin((float) image_width[0]/image_height[0]);      // in px
                    int image_vertical_margin = (int) dpConvertPx(60);
                    params.setMargins(image_horizontal_margin, image_vertical_margin, image_horizontal_margin,0);
                    holder.imageCover.setLayoutParams(params);

                    // reset bottom margin
                    int textHeight = getHeight(context, holder.title);
                    int text_whitespace = (image_vertical_margin + image_height[0])/3 - textHeight;
                    Log.d("whitespace", String.valueOf(text_whitespace));
                    Log.d("whitespace_text", String.valueOf(textHeight));
                    int textTopMargin = (int) (text_whitespace / (1+1.5));
                    int textBottomMargin = (int) (text_whitespace * 1.5 / (1+1.5));
                    params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(dpConvertPx(18), textTopMargin, dpConvertPx(18), textBottomMargin);
                    holder.title.setLayoutParams(params);
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                }
            };

            holder.imageCover.setTag(target);
            Picasso.get().load(image_path).into(target);

        }else{
            holder.journal.setVisibility(View.VISIBLE);
            holder.imageCover.setVisibility(View.GONE);
            LinearLayout.LayoutParams params;
            if(!post.getTitle().isEmpty()){
                holder.title.setVisibility(View.VISIBLE);
                // journal top margin
                params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(dpConvertPx(16), dpConvertPx(21), dpConvertPx(16), dpConvertPx(41));
            }
            else{
                holder.title.setVisibility(View.GONE);
                // journal top margin
                params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(dpConvertPx(16), dpConvertPx(32), dpConvertPx(16), dpConvertPx(41));
                holder.journal.setMaxLines(6);
            }
            holder.journal.setLayoutParams(params);


        }



        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("postId", post.getPid());
                context.startActivity(intent);
            }
        });
    }

    private int getImageVerticalMargin(float ratio) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float deviceWidth = displayMetrics.widthPixels;

        float slope = ((deviceWidth / 6) - (deviceWidth / 4) * 45/44);
        float margin = slope * (ratio - 4/5f) + deviceWidth/4;
        return (int) margin;

    }

    public void setPostList(List<Post> postList) {
        this.postList = postList;
    }

    private int dpConvertPx(int dp){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (dp * metrics.densityDpi)/160;
    }


    public static int getHeight(Context context, TextView textView) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int deviceHeight = displayMetrics.heightPixels;
        int deviceWidth = displayMetrics.widthPixels;

        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(deviceWidth, View.MeasureSpec.AT_MOST);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        textView.measure(widthMeasureSpec, heightMeasureSpec);
        return textView.getMeasuredHeight();
    }

    @Override
    public int getItemCount() {
        if(postList == null)
            return 0;
        return postList.size();
    }


}