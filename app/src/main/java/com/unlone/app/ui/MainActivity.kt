package com.unlone.app.ui

import android.os.Bundle
import android.os.IBinder
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.unlone.app.R
import com.unlone.app.data.database.PostDao
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var postFao: PostDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_startup_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val navBottomBar = findViewById<BottomNavigationView>(R.id.nav_view)
        navBottomBar.setupWithNavController(navController)

        // hide the bottom navigation bar when they are not lounge and profile
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_home -> navBottomBar.visibility = View.VISIBLE
                R.id.navigation_profile -> navBottomBar.visibility = View.VISIBLE
                else -> navBottomBar.visibility = View.GONE
            }
        }

        // make sure empty the database
        lifecycleScope.launch(Dispatchers.Default) {
            postFao.nukeTable()
        }

    }
}