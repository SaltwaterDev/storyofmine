package com.unlone.app.ui.access

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.unlone.app.R

class StartupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.fragment_startup)
    }
}