package com.example.experiment6

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

        val username =
            findViewById<EditText>(R.id.usernameEditText)

        val password =
            findViewById<EditText>(R.id.passwordEditText)

        val loginButton =
            findViewById<Button>(R.id.loginButton)

        val registerButton =
            findViewById<Button>(R.id.registerButton)


        // LOGIN BUTTON
        loginButton.setOnClickListener {

            val user = username.text.toString().trim()
            val pass = password.text.toString().trim()

            if (user.isEmpty() || pass.isEmpty()) {

                Toast.makeText(
                    this,
                    "Please enter username and password",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                AlertDialog.Builder(this)
                    .setTitle("Login Successful")
                    .setMessage(
                        "Welcome, $user!\nYou logged in successfully."
                    )
                    .setPositiveButton("OK") { _, _ ->

                        // Explicit Intent
                        val intent =
                            Intent(this, HomeActivity::class.java)

                        intent.putExtra(
                            "USERNAME",
                            user
                        )

                        startActivity(intent)
                    }
                    .show()
            }
        }


        // REGISTER BUTTON
        registerButton.setOnClickListener {

            val intent =
                Intent(this, RegisterActivity::class.java)

            startActivity(intent)
        }
    }
}