package com.unlone.app.ui.access

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.iterator
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.unlone.app.databinding.FragmentChooseCategoriesBinding
import com.unlone.app.instance.User
import com.unlone.app.ui.MainActivity
import com.unlone.app.ui.lounge.category.CategoriesViewModel
import com.google.android.gms.tasks.Task
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class ChooseCategoriesFragment : Fragment() {

    private val args: ChooseCategoriesFragmentArgs by navArgs()

    private var _binding: FragmentChooseCategoriesBinding? = null
    private val binding get() = _binding!!

    val mFirestore = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentChooseCategoriesBinding.inflate(inflater, container, false)
        val view = binding.root

        // get user argument
        val user: User = args.user

        // add categories to chip group
        val model = ViewModelProvider(this).get(CategoriesViewModel::class.java)
        model.loadCategories()
        model.categories.observe(viewLifecycleOwner, { categories ->
          for (c in categories){
                  addChip(c, binding.chipGroup)
          }
        })

        binding.startBtn.setOnClickListener{

            // save user
            CoroutineScope(Dispatchers.IO).launch {
                saveUser(user, model)?.await()
            }
            Log.d("TAG", "user saved successfully")

            // finish on boarding, start main activity
            startActivity(
                Intent(
                    context,
                    MainActivity::class.java
                )
            )
            activity?.finish()
        }


        return view
    }

    private fun addChip(c: String, chipGroup: ChipGroup) {
        val chip = Chip(context)
        chip.text = c
        chip.isCheckable = true
        chipGroup.addView(chip)
    }

    private fun saveUser(user: User, model: CategoriesViewModel): Task<Void>? {
        Log.d("TAG", "saveUser")
        Log.d("TAG", "save user uid: $user.uid")
        val username = user.username
        Log.d("TAG", "save user username: $username")

        // read categories
        val selectedCategories = ArrayList<String>()
        for (chip in binding.chipGroup){
            if ((chip as Chip).isChecked) {
                val selectedCategoryName = model.retrieveDefaultCategory(chip.text.toString())
                Log.d("TAG", "added $selectedCategoryName")
                if (selectedCategoryName != null) {
                    selectedCategories.add(selectedCategoryName)
                }
            }
        }

        // write user info into Firestore
        user.followingCategories = selectedCategories
        return user.uid?.let { mFirestore.collection("users").document(it).set(user) }
    }

    companion object {
        fun newInstance() {
            }
    }
}