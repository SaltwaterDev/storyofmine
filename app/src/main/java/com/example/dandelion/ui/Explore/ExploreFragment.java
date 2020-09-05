package com.example.dandelion.ui.Explore;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dandelion.R;
import com.example.dandelion.instance.Post;
import com.example.dandelion.ui.PostsAdapter;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class ExploreFragment extends Fragment {
    private ExploreViewModel exploreViewModel;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ExplorePostsAdapter explorepostsAdapter;
    private int mPosts = 10;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_explore, container, false);
        mAuth = FirebaseAuth.getInstance();
        recyclerView = root.findViewById(R.id.recycleview_posts);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        explorepostsAdapter = new ExplorePostsAdapter(getActivity());
        recyclerView.setAdapter(explorepostsAdapter);

        exploreViewModel = new ViewModelProvider(this).get(ExploreViewModel.class);
        exploreViewModel.loadPosts(mPosts);
        exploreViewModel.getPosts().observe(getViewLifecycleOwner(), new Observer<List<Post>>() {
            @Override
            public void onChanged(@Nullable List<Post> postList) {
                explorepostsAdapter.setPostList(postList);
                explorepostsAdapter.notifyDataSetChanged();
            }
        });

        return root;
    }
}