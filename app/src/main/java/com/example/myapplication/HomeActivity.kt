package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)

        val studentImage =
            findViewById<ImageView>(R.id.studentImage)

        val welcomeText =
            findViewById<TextView>(R.id.welcomeText)

        val optionsListView =
            findViewById<ListView>(R.id.optionsListView)

        val profileButton =
            findViewById<Button>(R.id.profileButton)

        val logoutButton =
            findViewById<Button>(R.id.logoutButton)

        // Get username from Intent
        val username =
            intent.getStringExtra("USERNAME")

        welcomeText.text =
            "Welcome, $username!"

        // ListView items
        val options = arrayOf(
            "View Dashboard",
            "View Projects",
            "View Tasks",
            "View Profile",
            "Logout"
        )

        // Adapter
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            options
        )

        optionsListView.adapter = adapter

        // ListView click
        optionsListView.setOnItemClickListener {
                _, _, position, _ ->

            when (position) {

                0 -> {
                    welcomeText.text =
                        "Welcome, $username!\nDashboard"
                }

                1 -> {
                    welcomeText.text =
                        "Welcome, $username!\nProjects"
                }

                2 -> {
                    welcomeText.text =
                        "Welcome, $username!\nTasks"
                }

                3 -> {

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

                4 -> {
                    logout()
                }
            }
        }

        // Profile button
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
            logout()
        }
    }

    private fun logout() {

        val intent =
            Intent(
                this,
                MainActivity::class.java
            )

        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_NEW_TASK

        startActivity(intent)

        finish()
    }
}