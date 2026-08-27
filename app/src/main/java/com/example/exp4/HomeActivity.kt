package com.example.exp4

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val welcomeText =
            findViewById<TextView>(R.id.welcomeText)

        val username =
            intent.getStringExtra("USERNAME")

        welcomeText.text = "Welcome, $username! "
    }
}