package com.unlone.app.ui.lounge.common

import android.content.Context
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.unlone.app.R
import com.unlone.app.databinding.FragmentPostDetailBinding
import com.unlone.app.utils.ViewModelFactory


class PostDetailFragment : Fragment() {
    private val args: PostDetailFragmentArgs by navArgs()
    private val pid by lazy { args.pid }
    private var _binding: FragmentPostDetailBinding? = null
    private val binding get() = _binding!!

    // declare viewModel
    private lateinit var viewModelFactory: ViewModelFactory
    private val detailedPostViewModel: DetailedPostViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[DetailedPostViewModel::class.java]
    }

    // detail of the post
    private val commentsAdapter by lazy {
        CommentsAdapter(
            detailedPostViewModel,
            this,
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPostDetailBinding.inflate(inflater, container, false)
        val view = binding.root
        viewModelFactory = ViewModelFactory(pid)
        binding.viewModel = detailedPostViewModel
        binding.lifecycleOwner = this

        // init toolbar
        initToolbar()

        // load info
        loadPostInfo()
        val saveButton = binding.topAppBar.menu.findItem(R.id.actionSave)
        isSaved(saveButton)

        // load comment
        detailedPostViewModel.loadUiComments()
        binding.recycleview.adapter = commentsAdapter
        detailedPostViewModel.uiComments.observe(
            this, {
                it?.let {
                    Log.d("TAG", "comments in post detail activity: $it")
                    commentsAdapter.submitList(it)
                }
            })

        // listen to the subComment call
        detailedPostViewModel.commentEditTextFocused.observe(this, {
            detailedPostViewModel.parentCommenter?.let { it1 ->
                Log.d("tag", "commentEditTextFocused = $it")
                focusEdittextToSubComment(it1)
            }
        })

        // send comment button click
        binding.sendBtn.setOnClickListener {
            postComment()
            binding.commentEt.text.clear()
            val imm: InputMethodManager? =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
            binding.moreCommentButton.visibility = View.VISIBLE
        }

        binding.commentEt.doAfterTextChanged {
            if (detailedPostViewModel.parentCid != null && detailedPostViewModel.parentCommenter.toString() !in binding.commentEt.text) {
                // replying prefix is destroyed, remove the prefix directly
                val arr = binding.commentEt.text.split(" ").toTypedArray()
                val trimmedContent = arr.filterNot { it == arr[0] }     // the content with the "@user" prefix"
                val replaceText = if (trimmedContent.isEmpty()) "" else trimmedContent[0]
                binding.commentEt.setText(replaceText)
                detailedPostViewModel.clearSubCommentPrerequisite()
            }
        }
        return view
    }

    private fun initToolbar() {
        val fragmentManager = (activity as FragmentActivity).supportFragmentManager
        binding.topAppBar.setNavigationOnClickListener {
            fragmentManager.popBackStack()
        }
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.actionSave -> {
                    // User check the "Saving" item, save the post...
                    detailedPostViewModel.savePost()
                    true
                }
                R.id.actionReport -> {
                    // write the pid into the Report collection in Firestore
                    val singleItems: Array<String> =
                        detailedPostViewModel.singleItems.map { getString(it) }.toTypedArray()
                    var checkedItem = 1

                    // show dialog
                    MaterialAlertDialogBuilder(
                        requireContext(),
                        R.style.ThemeOverlay_App_MaterialAlertDialog
                    )
                        .setTitle(getString(R.string.why_report))
                        .setNeutralButton(getString(R.string.cancel)) { _, _ -> }
                        .setPositiveButton(getString(R.string.report)) { dialog, which ->
                            // Respond to positive button press
                            Log.d("TAG", singleItems[checkedItem])
                            detailedPostViewModel.reportPost(checkedItem)
                            showConfirmation()
                        }// Single-choice items (initialized with checked item)
                        .setSingleChoiceItems(singleItems, checkedItem) { dialog, which ->
                            // Respond to item chosen
                            Log.d("TAG", which.toString())
                            checkedItem = which
                        }
                        .show()
                    true
                }
                R.id.actionDelete -> {
                    // delete the post
                    context?.let {
                        MaterialAlertDialogBuilder(it)
                            .setTitle(getString(R.string.delete_alert))
                            .setMessage(getString(R.string.report_alert_context))
                            .setPositiveButton(getString(R.string.action_delete)) { dialog, which ->
                                // Respond to positive button press
                                detailedPostViewModel.deletePost(pid)
                                fragmentManager.popBackStack()
                            }
                            .setNeutralButton(getString(R.string.cancel)) { dialog, which ->
                            }
                            .show()
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun showConfirmation() {
        val fragmentManager = (activity as FragmentActivity).supportFragmentManager
        context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle(resources.getString(R.string.thank_you))
                .setMessage(getString(R.string.report_text))
                .setPositiveButton(getString(R.string.confirm)) { dialog, which ->
                    // Respond to positive button press
                    fragmentManager.popBackStack()
                }
                .show()
        }
    }

    private fun loadPostInfo() {
        detailedPostViewModel.observablePost.observe(this, { p ->
            p?.let {
                // enable or disable the permission of deleting post
                val deleteMenuItem = binding.topAppBar.menu.findItem(R.id.actionDelete)
                deleteMenuItem.isVisible = detailedPostViewModel.isSelfPost

                // enable or disable save button
                val saveButton = binding.topAppBar.menu.findItem(R.id.actionSave)
                if (!p.save) {
                    saveButton.isEnabled = false
                    saveButton.icon.mutate().alpha = 135
                }

                // others will be implemented BY data binding
            }
        })
    }

    private fun postComment() {
        val content = binding.commentEt.text.toString().trim { it <= ' ' }
        if (!TextUtils.isEmpty(content)) {
            detailedPostViewModel.uploadComment(content)
            detailedPostViewModel.loadUiComments(false)
            return
        }
        // no value is entered, shouldn't go here if have comment
        Toast.makeText(requireContext(), "Comment is empty", Toast.LENGTH_SHORT).show()
    }

    private fun isSaved(saveButton: MenuItem) {
        val c: Context = requireContext()
        detailedPostViewModel.isPostSaved.observe(this, { saved ->
            saveButton.icon = when (saved) {
                true -> ContextCompat.getDrawable(c, R.drawable.ic_baseline_bookmark_24)
                else -> ContextCompat.getDrawable(c, R.drawable.ic_baseline_bookmark_border_24)
            }
        })
    }

    private fun focusEdittextToSubComment(username: String) {
        val editText = binding.commentEt

        // add prefix
        val prefix = "@$username "
        val prefixToSpan: Spannable = SpannableString(prefix)
        prefixToSpan.setSpan(
            ForegroundColorSpan(resources.getColor(R.color.labelled)),
            0,
            prefix.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        editText.setText(prefixToSpan, TextView.BufferType.EDITABLE)

        // set focus and open the soft keyboard
        editText.requestFocus()
        val imm: InputMethodManager? =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)

        // move the cursor to the end
        editText.setSelection(editText.length())
    }

    companion object {
        private const val TAG = "PostDetailedActivity"
    }

}
