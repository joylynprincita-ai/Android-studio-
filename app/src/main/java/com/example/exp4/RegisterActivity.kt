package com.example.exp4

import android.app.AlertDialog
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val name = findViewById<EditText>(R.id.registerName)
        val username = findViewById<EditText>(R.id.registerUsername)
        val password = findViewById<EditText>(R.id.registerPassword)

        val registerButton =
            findViewById<MaterialButton>(R.id.registerButton)

        registerButton.setOnClickListener {

            val fullName = name.text.toString().trim()
            val user = username.text.toString().trim()
            val pass = password.text.toString().trim()

            if (
                fullName.isEmpty() ||
                user.isEmpty() ||
                pass.isEmpty()
            ) {

                AlertDialog.Builder(this)
                    .setTitle("Registration Failed")
                    .setMessage("Please fill all fields.")
                    .setPositiveButton("OK", null)
                    .show()

            } else {

                AlertDialog.Builder(this)
                    .setTitle("Registration Successful")
                    .setMessage("Account created successfully!")
                    .setPositiveButton("OK") { _, _ ->
                        finish()
                    }
                    .show()
            }
        }
    }
}