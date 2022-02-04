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
import com.unlone.app.viewmodel.ContactUsViewModel


class ContactUsFragment : Fragment() {
    // This property is only valid between onCreateView and onDestroyView.
    private var _binding: FragmentContactUsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ContactUsViewModel by lazy{
        ViewModelProvider(this).get(ContactUsViewModel::class.java)
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
            view.findNavController().popBackStack()
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
                    view.findNavController().popBackStack()
                }
                .show()
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        // load type of issues
        val issuesList = viewModel.loadIssueList()
        viewModel.issueList.observe(viewLifecycleOwner) { issues: List<String> ->
            Log.d("TAG", issuesList.toString())
            val adapter = ArrayAdapter(requireContext(), R.layout.list_item, issues)
            binding.textField.setAdapter(adapter)
        }
    }

}