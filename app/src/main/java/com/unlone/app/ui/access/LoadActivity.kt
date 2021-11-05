package com.unlone.app.ui.access

import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.os.Bundle
import com.unlone.app.R
import android.view.WindowManager
import android.content.Intent
import android.os.Handler
import android.util.Log
import android.view.View
import com.google.firebase.firestore.DocumentSnapshot
import android.widget.Toast
import com.unlone.app.instance.User
import com.unlone.app.ui.MainActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class LoadActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private var mFirestore: FirebaseFirestore? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth =  Firebase.auth
        mFirestore = Firebase.firestore
        setContentView(R.layout.activity_load)

        // hide action bar
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        val decorView = window.decorView
        val uiOption =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        decorView.systemUiVisibility = uiOption
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth!!.currentUser
        if (currentUser == null) {
            Log.d("LOADACTIVITY", "first time login")
            Handler().postDelayed({
                startActivity(Intent(this, FirstAccessActivity::class.java))
                finish()
            }, 1000)
        } else {
            Log.d("LOADACTIVITY", "isEmailVerified = "+currentUser.isEmailVerified.toString())
            Log.d("LOADACTIVITY", "uid: ${currentUser.uid}")
            if(!currentUser.isEmailVerified){
                Toast.makeText(
                    this, "You have not verify the email",
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(Intent(this, FirstAccessActivity::class.java))
                finish()
            }
            mFirestore!!.collection("users").document(currentUser.uid).get()
                .addOnSuccessListener { documentSnapshot: DocumentSnapshot ->
                    Log.d("LOADACTIVITY", "GET USER")
                    val current = documentSnapshot.toObject<User>()!!
                    Toast.makeText(
                        this, "Welcome Back " + current.username,
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.d("LOADACTIVITY", "login: " + currentUser.uid)
                    Handler().postDelayed({
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }, 1000)
                }
                .addOnFailureListener { e ->
                    Log.d("LOADACTIVITY", "exception: \n$e")
                }
        }
    }
}