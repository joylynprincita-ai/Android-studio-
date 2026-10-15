package com.example.login_db

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var loginButton: Button

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Connect to activity_main.xml
        setContentView(R.layout.activity_main)

        // Connect XML views
        username = findViewById(R.id.etUsername)
        password = findViewById(R.id.etPassword)
        loginButton = findViewById(R.id.btnLogin)

        // Create SQLite database
        dbHelper = DatabaseHelper(this)

        // Create SharedPreferences
        val preferences = getSharedPreferences(
            "LoginPrefs",
            Context.MODE_PRIVATE
        )

        // Retrieve previously saved username
        val savedUsername =
            preferences.getString("username", "")

        // Display saved username
        username.setText(savedUsername)

        // Save username whenever user types
        username.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {

                preferences.edit()
                    .putString(
                        "username",
                        s.toString()
                    )
                    .apply()
            }

            override fun afterTextChanged(
                s: Editable?
            ) {
            }
        })

        // Login button
        loginButton.setOnClickListener {

            val user = username.text.toString()
            val pass = password.text.toString()

            if (user.isEmpty() || pass.isEmpty()) {

                Toast.makeText(
                    this,
                    "Please enter username and password",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                // Save username in SharedPreferences
                preferences.edit()
                    .putString("username", user)
                    .apply()

                // Store username and password in SQLite
                val result = dbHelper.insertUser(
                    user,
                    pass
                )

                if (result) {

                    Toast.makeText(
                        this,
                        "Login successful\nData stored in SQLite",
                        Toast.LENGTH_LONG
                    ).show()

                } else {

                    Toast.makeText(
                        this,
                        "Failed to store data",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}