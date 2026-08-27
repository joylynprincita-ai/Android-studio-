package com.example.experiment6

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)

        val welcomeText =
            findViewById<TextView>(
                R.id.welcomeText
            )

        val profileButton =
            findViewById<Button>(
                R.id.profileButton
            )

        val logoutButton =
            findViewById<Button>(
                R.id.logoutButton
            )


        // Receive username
        val username =
            intent.getStringExtra("USERNAME")

        welcomeText.text =
            "Welcome, $username! 👋"


        // Profile Intent
        profileButton.setOnClickListener {

            val intent =
                Intent(
                    this,
                    ProfileActivity::class.java
                )

            intent.putExtra(
                "USERNAME",
                username
            )

            startActivity(intent)
        }


        // Logout
        logoutButton.setOnClickListener {

            val intent =
                Intent(
                    this,
                    MainActivity::class.java
                )

            intent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP

            startActivity(intent)

            finish()
        }
    }
}