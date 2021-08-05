package com.example.unlone.ui.Explore;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.unlone.ui.PostsAdapter;

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


    private void isLike(String pid, ImageView imageView){


    }
}
