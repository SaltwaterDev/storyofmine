package com.unlone.app.ui.access


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.unlone.app.databinding.FragmentEmailVerificationBinding
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class EmailVerificationFragment : Fragment() {
    private var _binding: FragmentEmailVerificationBinding? = null
    private val binding get() = _binding!!

    val mFirestore = Firebase.firestore
    val mAuth = Firebase.auth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEmailVerificationBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.sendEmailBtn.setOnClickListener{
            val user = mAuth.currentUser
            CoroutineScope(Dispatchers.Main).launch {
                if (user != null) {
                    val url = "https://unlone.page.link/verify?uid=" + user.uid
                    val actionCodeSettings = ActionCodeSettings.newBuilder()
                        .setUrl(url)
                        .setHandleCodeInApp(false)
                        // The default for this is populated with the current android package name.
                        .setAndroidPackageName("com.unlone.app", false, null)
                        .build()
                    mAuth.setLanguageCode(Locale.getDefault().country)
                    withContext(Dispatchers.IO){
                        user.sendEmailVerification(actionCodeSettings).await()
                    }
                }
                // TODO("move the following block to cloud function")
                /* delay(60000L) // wait for one minute
                println("main: I'm tired of waiting!")
                println("main: Now I delete user.")
                // delete account
                user?.delete()?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "User account deleted.")
                    }
                }
                activity?.finish()

                 */
            }
        }

        return view
    }


    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EmailVerificationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}