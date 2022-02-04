package com.unlone.app.ui.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.android.datatransport.runtime.backends.BackendResponse.ok
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.unlone.app.R
import com.unlone.app.databinding.FragmentSettingBinding
import com.unlone.app.ui.access.StartupActivity
import com.unlone.app.viewmodel.SettingViewModel
import com.yariksoffice.lingver.Lingver
import kotlinx.coroutines.InternalCoroutinesApi


class SettingFragment : Fragment() {
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SettingViewModel by lazy {
        ViewModelProvider(this)[SettingViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("TAG", "Lingver Language: " + Lingver.getInstance().getLanguage())

        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.langTv.setOnClickListener {
            val singleItems =
                arrayOf(resources.getString(R.string.eng), resources.getString(R.string.zh))
            val checkedItem = 1
            context?.let { it1 ->
                MaterialAlertDialogBuilder(it1)
                    .setTitle(resources.getString(R.string.title))
                    .setNeutralButton(resources.getString(R.string.cancel)) { dialog, which -> }
                    .setPositiveButton(resources.getString(R.string.ok)) { dialog, which ->
                        when (which) {
                            0 -> setNewLocale(LANGUAGE_ENGLISH, LANGUAGE_COUNTRY)
                            1 -> setNewLocale(LANGUAGE_ZH, LANGUAGE_COUNTRY)
                        }
                    }
                    .setSingleChoiceItems(singleItems, checkedItem) { dialog, which -> }
                    .show()
            }
        }


        binding.logoutTv.setOnClickListener {
            logout()
        }


        return view
    }


    private fun setNewLocale(language: String, country: String) {
        context?.let { Lingver.getInstance().setLocale(it, language, country) }
        restart()
    }

    private fun restart() {
        TODO("Not yet implemented")
    }


    @OptIn(InternalCoroutinesApi::class)
    private fun logout() {
        context?.let {
            MaterialAlertDialogBuilder(it, R.style.ThemeOverlay_App_MaterialAlertDialog)
                .setTitle(resources.getString(R.string.delete_alert))
                .setMessage(resources.getString(R.string.logout_alert))
                .setPositiveButton(resources.getString(R.string.logout)) { _, _ ->
                    val user = FirebaseAuth.getInstance()
                    user.signOut()
                    val intent = Intent(context, StartupActivity::class.java)
                    it.startActivity(intent)
                    (context as Activity).finish()
                }
                .show()
        }
    }


    companion object {
        const val LANGUAGE_ENGLISH = "en"
        const val LANGUAGE_ZH = "zh"
        const val LANGUAGE_COUNTRY = "HK"
    }
}