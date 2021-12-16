package com.unlone.app.ui.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.unlone.app.R
import com.unlone.app.databinding.FragmentContactUsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class ContactUsFragment : Fragment() {
    private lateinit var viewModel: ContactUsViewModel

    // This property is only valid between onCreateView and onDestroyView.
    private var _binding: FragmentContactUsBinding? = null
    private val binding get() = _binding!!

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ContactUsViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentContactUsBinding.inflate(inflater, container, false)
        val view = binding.root


        // return button
        binding.cancelButton.setOnClickListener {
            view.findNavController().navigate(R.id.action_contactUsFragment_to_navigation_profile)
        }

        // send button
        binding.sendButton.setOnClickListener {
            val issueType = binding.textField.text.toString()
            val detail = binding.detailEv.text.toString()
            viewModel.uploadIssue(issueType, detail)
            // set confirmation dialog
            MaterialAlertDialogBuilder(requireContext(), R.style.ThemeOverlay_App_MaterialConfirmDialog)
                .setTitle("Confirm")
                .setMessage("Thank you for your feedback. We we try to contact you as soon as possible.")
                .setPositiveButton("Confirm") { dialog, which ->
                    view.findNavController().navigate(R.id.action_contactUsFragment_to_navigation_profile)
                }
                .show()
        }


        return view
    }

    override fun onResume() {
        super.onResume()
        // load type of issues
        val issuesList = viewModel.loadIssueList()
        viewModel.issueList.observe(viewLifecycleOwner, { issues: List<String> ->
            Log.d("TAG", issuesList.toString())
            val adapter = ArrayAdapter(requireContext(), R.layout.list_item, issues)
            binding.textField.setAdapter(adapter)
        })
    }

}