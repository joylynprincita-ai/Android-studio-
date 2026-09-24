package com.example.myapplication

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val usernameEditText =
            findViewById<EditText>(R.id.usernameEditText)

        val passwordEditText =
            findViewById<EditText>(R.id.passwordEditText)

        val loginButton =
            findViewById<Button>(R.id.loginButton)

        val registerButton =
            findViewById<Button>(R.id.registerButton)

        loginButton.setOnClickListener {

            val username =
                usernameEditText.text.toString().trim()

            val password =
                passwordEditText.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {

                Toast.makeText(
                    this,
                    "Please enter username and password",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                AlertDialog.Builder(this)
                    .setTitle("Login Successful")
                    .setMessage(
                        "Welcome, $username!\n\n" +
                                "You logged in successfully."
                    )
                    .setPositiveButton("OK") { _, _ ->

                        // Explicit Intent
                        val intent =
                            Intent(
                                this,
                                HomeActivity::class.java
                            )

                        // Send username to HomeActivity
                        intent.putExtra(
                            "USERNAME",
                            username
                        )

                        startActivity(intent)
                    }
                    .show()
            }
        }

        // Registration Intent
        registerButton.setOnClickListener {

            val intent =
                Intent(
                    this,
                    RegisterActivity::class.java
                )

            startActivity(intent)
        }
    }
}