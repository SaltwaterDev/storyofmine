package com.example.unlone.ui.Lounge;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.unlone.R;
import com.example.unlone.instance.Post;
import com.example.unlone.ui.Create.PostActivity;
import com.example.unlone.ui.PostsAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class LoungeFollowingFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayoutManager layoutManager;
    private PostsAdapter postsAdapter;
    private int mPosts = 10;

    public static final int REQUEST_CODE_ADD_POST = 1;


    public LoungeFollowingFragment() {
        // Required empty public constructor
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_lounge_following, container, false);
        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setTooltipText("Write a post");

        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivityForResult(
                        new Intent(getContext(), PostActivity.class), REQUEST_CODE_ADD_POST);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        recyclerView = root.findViewById(R.id.recycleview_posts);
        swipeRefreshLayout = root.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        postsAdapter = new PostsAdapter(getActivity());
        recyclerView.setAdapter(postsAdapter);

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        homeViewModel.loadPosts(mPosts);
        homeViewModel.getPosts().observe(getViewLifecycleOwner(), new Observer<List<Post>>() {
            @Override
            public void onChanged(@Nullable List<Post> postList) {
                postsAdapter.setPostList(postList);
                postsAdapter.notifyDataSetChanged();
            }
        });


        return root;
    }


}