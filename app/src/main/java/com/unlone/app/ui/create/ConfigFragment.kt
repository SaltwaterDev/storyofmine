package com.unlone.app.ui.create

import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.unlone.app.R
import com.unlone.app.databinding.FragmentConfigBinding
import com.unlone.app.utils.dpConvertPx
import com.unlone.app.utils.getImageHorizontalMargin
import com.unlone.app.viewmodel.SavedStateModel
import kotlinx.parcelize.Parcelize
import java.util.ArrayList

@Parcelize
/* This class is used to contain the data during creating the post
 */
data class PostData(
    var title: String = "",
    var imageUri: Uri? = null,
    var journal: String = "",
    var uid: String = "",
    var labels: ArrayList<String> = ArrayList<String>(),
    var category: String = "",
    var comment: Boolean = true,
    var save: Boolean = true
) : Parcelable


class ConfigFragment : Fragment() {

    private val savedStateModel: SavedStateModel by activityViewModels()
    private var postData: PostData? = null
    private lateinit var _binding: FragmentConfigBinding

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfigBinding.inflate(inflater, container, false)
        val view = binding.root

        savedStateModel.postData.observe(viewLifecycleOwner) { postData ->
            this.postData = postData
            Log.d("TAG", "config fragment, postData: $postData")
            binding.commentSwitch.isChecked = postData.comment
            binding.saveSwitch.isChecked = postData.save
            binding.textField.setText(postData.category)

            // Preview
            postData.imageUri?.let { displayImage(it) }
            _binding.category = savedStateModel.categoryTitle.value
            _binding.post = savedStateModel.createPostObject(postData)
            _binding.layoutPost.labelGroup.isClickable = false
        }


        val commentSwitch = binding.commentSwitch.setOnCheckedChangeListener { _, isChecked ->
            postData?.comment = !isChecked
        }
        val saveSwitch = binding.saveSwitch.setOnCheckedChangeListener { _, isChecked ->
            postData?.save = !isChecked
        }
        val backButton = binding.backButton.setOnClickListener {
            postData?.category = binding.textField.text.toString()
            postData?.let { it1 -> savedStateModel.savepostData(it1) }
            Navigation.findNavController(view).navigate(R.id.navigateToWritePostFragment)
        }
        val postButton = binding.postButton.setOnClickListener {
            binding.postButton.isEnabled = false
            binding.backButton.isEnabled = false
            postData?.category = binding.textField.text.toString()
            Toast.makeText(activity, "Posting...", Toast.LENGTH_SHORT).show()
            postData?.let { it1 ->
                if (it1.category.isEmpty()) {
                    Toast.makeText(activity, "You Haven't set the category", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    savedStateModel.submitPost(it1)
                }
            }
            binding.postButton.isEnabled = true
            binding.backButton.isEnabled = true
        }

        savedStateModel.navBack.observe(viewLifecycleOwner) {
            if (it) {
                findNavController().navigate(R.id.post_create_to_lounge)
            }
        }

        return view
    }


    override fun onResume() {
        super.onResume()
        // load categories
        savedStateModel.loadCategories()
        savedStateModel.categories.observe(viewLifecycleOwner) { categories ->
            Log.d("TAG category config", categories.toString())
            val adapter = ArrayAdapter(requireContext(), R.layout.list_item, categories)
            binding.textField.setAdapter(adapter)
        }
    }

    private fun displayImage(uri: Uri) {
        _binding.layoutPost.imageCover.setImageURI(uri)
        _binding.layoutPost.imageCover.visibility = View.VISIBLE
        val bitmap = (_binding.layoutPost.imageCover.drawable as BitmapDrawable).bitmap
        val width = bitmap.width.toFloat()
        val height = bitmap.height.toFloat()
        Log.d("uri", uri.toString())

        val imageHorizontalMargin =
            activity?.let { getImageHorizontalMargin(width / height, it) }    // in px
        val imageVerticalMargin = activity?.let { dpConvertPx(10, it) }
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        Log.d("margin", imageVerticalMargin.toString())
        if (imageHorizontalMargin != null && imageVerticalMargin != null) {
            params.setMargins(imageHorizontalMargin, imageVerticalMargin, imageHorizontalMargin, 0)
        }
        _binding.layoutPost.imageCover.layoutParams = params
    }

}