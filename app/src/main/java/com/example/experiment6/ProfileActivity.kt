package com.example.experiment6

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profile)

        val profileName =
            findViewById<TextView>(
                R.id.profileName
            )

        val profileUSN =
            findViewById<TextView>(
                R.id.profileUSN
            )

        val profileCourse =
            findViewById<TextView>(
                R.id.profileCourse
            )

        val profileEmail =
            findViewById<TextView>(
                R.id.profileEmail
            )


        val username =
            intent.getStringExtra("USERNAME")


        profileName.text =
            "Name: $username"

        profileUSN.text =
            "USN: 25MCAR0157"

        profileCourse.text =
            "Course: MCA"

        profileEmail.text =
            "Email: faheem@gmail.com"
    }
}