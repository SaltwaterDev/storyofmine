package com.unlone.app.ui.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.unlone.app.R
import com.unlone.app.databinding.FragmentSettingBinding
import com.unlone.app.ui.MainActivity
import com.unlone.app.ui.access.StartupActivity
import com.yariksoffice.lingver.Lingver
import kotlinx.coroutines.InternalCoroutinesApi


class SettingFragment : Fragment() {
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.langTv.setOnClickListener {
            val singleItems =
                arrayOf(resources.getString(R.string.eng), resources.getString(R.string.zh))
            var checkedItem = when (Lingver.getInstance().getLanguage()) {
                LANGUAGE_ZH -> 1
                else -> 0
            }
            context?.let { it1 ->
                MaterialAlertDialogBuilder(it1)
                    .setTitle(resources.getString(R.string.setLang))
                    .setNeutralButton(resources.getString(R.string.cancel)) { _, _ -> }
                    .setPositiveButton(resources.getString(R.string.ok)) { _, _ ->
                        Log.d("TAG", checkedItem.toString())
                        when (checkedItem) {
                            0 -> setNewLocale(LANGUAGE_ENGLISH, LANGUAGE_COUNTRY)
                            1 -> setNewLocale(LANGUAGE_ZH, LANGUAGE_COUNTRY)
                        }
                    }
                    .setSingleChoiceItems(singleItems, checkedItem) { dialog, which ->
                        checkedItem = which
                    }
                    .show()
            }
        }
        binding.themeTv.setOnClickListener {
            val singleItems =
                arrayOf(
                    resources.getString(R.string.dark_theme),
                    resources.getString(R.string.light_theme),
                    resources.getString(R.string.system_default_theme),
                )
            var checkedItem = when (getDefaultNightMode()) {
                MODE_NIGHT_YES -> 0
                MODE_NIGHT_NO -> 1
                MODE_NIGHT_FOLLOW_SYSTEM -> 2
                else -> -1
            }
            context?.let { it1 ->
                MaterialAlertDialogBuilder(it1)
                    .setTitle(resources.getString(R.string.setDarkTheme))
                    .setNeutralButton(resources.getString(R.string.cancel)) { _, _ -> }
                    .setPositiveButton(resources.getString(R.string.ok)) { _, _ ->
                        Log.d("TAG", checkedItem.toString())
                        setDarkMode(checkedItem)
                    }
                    .setSingleChoiceItems(singleItems, checkedItem) { dialog, which ->
                        checkedItem = which
                    }
                    .show()
            }
        }
        binding.logoutTv.setOnClickListener { logout() }

        return view
    }

    private fun setDarkMode(checkedItem: Int) {
        val nightMode = when(checkedItem){
            0 -> MODE_NIGHT_YES
            1 -> MODE_NIGHT_NO
            2 -> MODE_NIGHT_FOLLOW_SYSTEM
            else -> null
        }

        if (nightMode != null) {
            setDefaultNightMode(nightMode)
        }
    }


    private fun setNewLocale(language: String, country: String) {
        context?.let {
            Lingver.getInstance().setLocale(it, language, country)
            restart()
        }
    }


    @OptIn(InternalCoroutinesApi::class, kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    private fun restart() {
        val i = Intent(context, MainActivity::class.java)
        startActivity(i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
        Toast.makeText(context, "Activity restarted", Toast.LENGTH_SHORT).show()
    }


    @OptIn(InternalCoroutinesApi::class, kotlinx.coroutines.ExperimentalCoroutinesApi::class)
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