package com.example.dandelion.ui.Home;


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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dandelion.Post;
import com.example.dandelion.PostsAdapter;
import com.example.dandelion.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import static androidx.navigation.fragment.NavHostFragment.findNavController;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private PostsAdapter postsAdapter;
    private int mPosts = 10;


    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        final NavController navController = findNavController(this);

        FloatingActionButton fab = (FloatingActionButton) root.findViewById(R.id.fab);
        fab.setTooltipText("Create a post");

        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                navController.navigate(R.id.action_navigation_home_to_createFragment);
            }
        });

        mAuth = FirebaseAuth.getInstance();
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        recyclerView = root.findViewById(R.id.recycleview_posts);
        layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        postsAdapter = new PostsAdapter(getActivity());
        homeViewModel.loadPosts(mPosts);
        recyclerView.setAdapter(postsAdapter);
        homeViewModel.getPosts().observe(getViewLifecycleOwner(), new Observer<List<Post>>() {
            @Override
            public void onChanged(@Nullable List<Post> postList) {
                // todo
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int id = layoutManager.findLastCompletelyVisibleItemPosition();
                if(id >= postsAdapter.getItemCount()-1){
                    //todo...
                }
            }
        });
        return root;
    }
}
