package com.example.unlone.ui.create

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.unlone.R
import com.example.unlone.databinding.FragmentWritePostBinding
import com.example.unlone.utils.dpConvertPx
import com.example.unlone.utils.getImageHorizontalMargin
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.theartofdev.edmodo.cropper.CropImage
import java.util.*


@RequiresApi(Build.VERSION_CODES.O)
class WritePostFragment : Fragment() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var storageReference: StorageReference

    private lateinit var setSelectedImagePath: String
    private var selectedImageUri: Uri? = null
    private var labels = ArrayList<String>()

    private lateinit var labelChipGroup: ChipGroup


    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private var _binding: FragmentWritePostBinding? = null

    private val cropActivityResultContract = object : ActivityResultContract<Any?, Uri?>(){
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage.activity()
                    .getIntent(activity!!)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent)?.uri
        }
    }

    private lateinit var cropActivityResultLauncher: ActivityResultLauncher<Any?>
    private val savedStateModel: SavedStateModel by activityViewModels()
    private lateinit var postData: PostData



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        // Inflate the layout for this fragment
        _binding = FragmentWritePostBinding.inflate(inflater, container, false)
        val view = binding.root

        // init database storing
        mAuth = FirebaseAuth.getInstance()
        mFirestore = FirebaseFirestore.getInstance()
        storageReference = FirebaseStorage.getInstance().getReference("posts")

        // restore UI state if any
        savedStateModel.postData.observe(viewLifecycleOwner, {postData ->
            this.postData = postData
            binding.inputPostTitle.setText(postData.title)
            binding.inputPostContext.setText(postData.journal)
            labels.clear()
            labels.addAll(postData.labels)
            Log.d("TAG", "each label name: " + postData.labels)
            Log.d("TAG", "each label name: $labels")
            for (label in labels) {
                addChipToGroup(label)
            }
            postData.imageUri?.let { displayImage(it) }

        })

        // init toolbar
        binding.cancelButton.setOnClickListener(View.OnClickListener { activity?.finish() })
        val nextButton = binding.nextButton.setOnClickListener {
            if (binding.inputPostContext.text.toString().isEmpty()) {
                Toast.makeText(activity, "You haven't wrote the context", Toast.LENGTH_SHORT).show()
            }else if (binding.inputPostTitle.text.toString().isEmpty()){
                Toast.makeText(activity, "You haven't wrote the Title", Toast.LENGTH_SHORT).show()
            }
            else{
                postData.title = binding.inputPostTitle.text.toString()
                postData.imageUri = selectedImageUri
                postData.journal = binding.inputPostContext.text.toString()
                postData.uid = mAuth.uid!!
                postData.labels.clear()
                postData.labels.addAll(labels)
                Log.d("TAG", "labels in write fragment: $labels")
                Log.d("TAG", "label in postData, write fragment: " + postData.labels.toString())

                savedStateModel.savepostData(postData)
                Navigation.findNavController(view).navigate(R.id.navigateToConfigFragment)
            }
        }

        // init label view
        labelChipGroup = binding.labelChipGroup
        val labelEv = binding.labelEv
        labelEv.setOnEditorActionListener { _, actionId, _ ->
            if(binding.labelEv.text.toString().isNotEmpty()){
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    labels.add(binding.labelEv.text.toString())
                    addChipToGroup(binding.labelEv.text.toString())
                    Log.d("TAG", "label just added: $labels")
                    true
                } else {
                    labels.remove(binding.labelEv.text.toString())
                    false
                }
            }else{
                false
            }
        }

        binding.labelEv.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                binding.labelEv.setText("")
            }
        }

        // init crop image
        cropActivityResultLauncher = registerForActivityResult(cropActivityResultContract){
            it?.let { uri ->
                displayImage(uri)
            }
        }

        initMoreLayout()


        return view
    }

    private fun displayImage(uri: Uri) {
        selectedImageUri = uri
        binding.imagePost.setImageURI(selectedImageUri)
        binding.imagePost.visibility = View.VISIBLE
        val bitmap = (binding.imagePost.getDrawable() as BitmapDrawable).bitmap
        val width = bitmap.width.toFloat()
        val height = bitmap.height.toFloat()
        Log.d("uriiii", selectedImageUri.toString())

        val imageHorizontalMargin = activity?.let { getImageHorizontalMargin(width / height, it) }    // in px
        val imageVerticalMargin = activity?.let { dpConvertPx(10, it) }
        val params = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
        Log.d("margin", imageVerticalMargin.toString())
        if (imageHorizontalMargin != null && imageVerticalMargin != null) {
            params.setMargins(imageHorizontalMargin, imageVerticalMargin, imageHorizontalMargin, 0)
        }
        binding.imagePost.layoutParams = params
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    private fun initMoreLayout() {
        val layoutMore = binding.moreOverlay.layoutMore
        val bottomSheetBehavior = BottomSheetBehavior.from(layoutMore)
        layoutMore.findViewById<View>(R.id.textMore).setOnClickListener {
            if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED)
            }
        }
        layoutMore.findViewById<View>(R.id.layoutAddImage).setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            if (activity?.let { it1 ->
                        ContextCompat.checkSelfPermission(
                                it1.applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    } != PackageManager.PERMISSION_GRANTED) {
                activity?.let { it1 ->
                    ActivityCompat.requestPermissions(
                            it1, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            REQUEST_CODE_STORAGE_PERMISSION
                    )
                }
            } else {
                cropActivityResultLauncher.launch(null)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                cropActivityResultLauncher.launch(null)
            } else {
                Toast.makeText(activity, "Permission Denied!", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun addChipToGroup(label: String) {
        val chip = Chip(activity)
        chip.text = label
        chip.chipIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_launcher_background)
        chip.isChipIconVisible = false
        chip.isCloseIconVisible = true
        chip.gravity = Gravity.TOP
        // necessary to get single selection working
        chip.isClickable = true
        chip.isCheckable = false
        labelChipGroup.addView(chip as View)
        chip.setOnCloseIconClickListener { labelChipGroup.removeView(chip as View) }
        // this will be put into the postData
        Log.d("chip", chip.text.toString())
        binding.labelEv.setText("")
    }


    companion object {
        private const val REQUEST_CODE_STORAGE_PERMISSION = 1
    }

}