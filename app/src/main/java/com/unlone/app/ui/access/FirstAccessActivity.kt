package com.unlone.app.ui.access

import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.unlone.app.R

class FirstAccessActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY)
        setContentView(R.layout.activity_first_access)
    }
}