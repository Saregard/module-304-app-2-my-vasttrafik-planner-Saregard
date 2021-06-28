package com.example.simpleplanner.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.simpleplanner.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.title = getString(R.string.nearby_stops)
    }
}