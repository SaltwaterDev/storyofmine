package com.example.unlone.ui.Create

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.example.unlone.R
import com.example.unlone.databinding.FragmentWritePostBinding
import com.example.unlone.instance.PostData
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
    private lateinit var createViewModel: CreateViewModel
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFirestore: FirebaseFirestore
    private lateinit var storageReference: StorageReference

    private lateinit var journal: EditText
    private lateinit var title: EditText
    private lateinit var labelEv: EditText
    private lateinit var imagePost: ImageView
    private lateinit var setSelectedImagePath: String
    private var selectedImageUri: Uri? = null
    private val labels = ArrayList<String>()

    private lateinit var labelChipGroup: ChipGroup


    private var _binding: FragmentWritePostBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
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

    private val postData: PostData = PostData()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        // Inflate the layout for this fragment
        _binding = FragmentWritePostBinding.inflate(inflater, container, false)
        val view = binding.root

        // init database storing
        createViewModel = ViewModelProvider(this).get(CreateViewModel::class.java)
        mAuth = FirebaseAuth.getInstance()
        mFirestore = FirebaseFirestore.getInstance()
        storageReference = FirebaseStorage.getInstance().getReference("posts")

        // init toolbar
        binding.cancelButton.setOnClickListener(View.OnClickListener { activity?.onBackPressed() })
        title = binding.inputPostTitle
        journal = binding.inputPostContext
        imagePost = binding.imagePost
        val nextButton = binding.nextButton.setOnClickListener {
            if (journal.text.toString().isEmpty()) {
                Toast.makeText(activity, "You did not complete the post.", Toast.LENGTH_SHORT).show()
            }else{
                postData.uid = mAuth.uid!!
                postData.title = title.text.toString()
                postData.journal = journal.text.toString()
                postData.imageUri = selectedImageUri
                postData.labels.addAll(labels)
                Log.d("labelll", postData.labels.toString())

                val action = WritePostFragmentDirections.navigateToConfigFragment(postData)
                Navigation.findNavController(view).navigate(action)
            }
        }

        // init label view
        labelChipGroup = binding.labelChipGroup
        labelEv = binding.labelEv
        labelEv.setOnEditorActionListener { v, actionId, _ ->
            if(!labelEv.text.toString().isEmpty()){
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    addChipToGroup(labelEv.text.toString())
                    true
                } else {
                    labels.remove(labelEv.text.toString())
                    false
                }
            }else{
                false
            }
        }

        labelEv.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                labelEv.setText("")
            }
        }

        // init crop image
        cropActivityResultLauncher = registerForActivityResult(cropActivityResultContract){
            it?.let { uri ->
                selectedImageUri = uri
                imagePost.setImageURI(selectedImageUri)
                imagePost.visibility = View.VISIBLE
                Log.d("uriiii", selectedImageUri.toString())
            }
        }
        initMoreLayout()

        return view
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
        labels.add(labelEv.text.toString())
        labelEv.setText("")
    }



    companion object {
        private const val REQUEST_CODE_STORAGE_PERMISSION = 1
    }

}