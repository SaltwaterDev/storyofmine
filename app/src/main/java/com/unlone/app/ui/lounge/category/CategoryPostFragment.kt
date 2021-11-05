package com.unlone.app.ui.lounge.category

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.unlone.app.R
import com.unlone.app.databinding.FragmentCategoryPostBinding
import com.unlone.app.instance.Post
import com.unlone.app.ui.create.PostActivity
import com.unlone.app.ui.lounge.common.PostsAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CategoryPostFragment : Fragment() {
    private val args: CategoryPostFragmentArgs by navArgs()

    private val category by lazy {
        args.category
    }
    private var categoryViewModel: CategoriesViewModel? = null
    private var postsAdapter: PostsAdapter? = null
    private val mPosts = 100
    private var isLoading = false

    val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    val mFirestore: FirebaseFirestore = FirebaseFirestore.getInstance()


    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private var _binding: FragmentCategoryPostBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This callback will only be called when CategoryPostFragment is at least Started.
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            // Handle the back button event
            view?.let { Navigation.findNavController(it).navigate(R.id.navigateToCategoryListFragment) }
        }
        // The callback can be enabled or disabled here or in the lambda
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        _binding = FragmentCategoryPostBinding.inflate(inflater, container, false)
        val view = binding.root


        // create "writing post" button
        val fab: FloatingActionButton = binding.fab
        fab.tooltipText = resources.getString(R.string.write_a_post)
        fab.setOnClickListener {
            val intent = Intent(context, PostActivity::class.java)
            startActivity(intent)
        }

        // set category title
        categoryViewModel = ViewModelProvider(this).get(CategoriesViewModel::class.java)
        categoryViewModel!!.getCategoryTitle(args.category)
        categoryViewModel!!.categoryTitle.observe(viewLifecycleOwner){ title ->
            binding.categoryTitleTv.text = title
        }
        isFollowing(binding.isFollowingTv)
        binding.isFollowingTv.setOnClickListener{
            if (binding.isFollowingTv.tag == "follow"){
                // set follow to true to follow this category
                categoryViewModel!!.followCategory(category, true)
                binding.isFollowingTv.tag = "following"
                binding.isFollowingTv.text = getString(R.string.following)
            } else{
                // set follow to false to unfollow this category
                categoryViewModel!!.followCategory(category, false)
                binding.isFollowingTv.tag = "follow"
                binding.isFollowingTv.text = getString(R.string.follow)
            }
        }

        // load posts
        val recyclerView: RecyclerView = binding.recycleviewPosts
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        postsAdapter = PostsAdapter(requireActivity())
        recyclerView.adapter = postsAdapter
        categoryViewModel!!.loadPosts(category, mPosts, false)
        categoryViewModel!!.posts.observe(
            viewLifecycleOwner
        ) { postList: List<Post> -> postsAdapter!!.setPostList(postList) }


        // set refresh layout
        val swipeRefreshLayout: SwipeRefreshLayout = binding.swipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = true
            categoryViewModel!!.loadPosts(category, mPosts, false)
            categoryViewModel!!.posts.observe(viewLifecycleOwner) { postList: List<Post> ->
                Log.d("TAG", postList.toString())
                postsAdapter!!.setPostList(postList)
            }
            swipeRefreshLayout.isRefreshing = false
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                //super.onScrolled(recyclerView, dx, dy);
                /*
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                val totalItem = linearLayoutManager!!.itemCount
                val lastVisible = linearLayoutManager.findLastCompletelyVisibleItemPosition()
                if (totalItem < lastVisible + 3) {
                    if (!isLoading) {
                        isLoading = true
                        // load more posts
                        categoryViewModel!!.loadPosts(category, mPosts, true)
                        categoryViewModel!!.posts.observe(
                            viewLifecycleOwner
                        ) { postList: List<Post> -> postsAdapter!!.setPostList(postList) }
                    }
                }
                 */
                isLoading = false
            }
        })

        // init search bar
        binding.inputSearch.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                categoryViewModel!!.searchPost(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {}
        })

        return view
    }


    private fun isFollowing(followingTv: TextView){
        mFirestore.collection("users").document(mAuth.uid!!).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    val categories = document.data?.get("followingCategories")
                    if (category in categories as ArrayList<*>) {
                        followingTv.text = getString(R.string.following)
                        followingTv.tag = "following"
                    } else {
                            followingTv.text = getString(R.string.follow)
                            followingTv.tag = "follow"
                    }

                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }

    }


    companion object {
        fun newInstance(arg: String) =
            CategoryPostFragment().apply {
                arguments = Bundle().apply {
                    putString("category", arg)
                }
            }

    }
}