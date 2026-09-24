package com.example.myapplication

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register)

        val nameEditText =
            findViewById<EditText>(R.id.nameEditText)

        val usnEditText =
            findViewById<EditText>(R.id.usnEditText)

        val emailEditText =
            findViewById<EditText>(R.id.emailEditText)

        val passwordEditText =
            findViewById<EditText>(
                R.id.registerPasswordEditText
            )

        val registerButton =
            findViewById<Button>(
                R.id.registerSubmitButton
            )

        registerButton.setOnClickListener {

            val name =
                nameEditText.text.toString().trim()

            val usn =
                usnEditText.text.toString().trim()

            val email =
                emailEditText.text.toString().trim()

            val password =
                passwordEditText.text.toString().trim()

            if (
                name.isEmpty() ||
                usn.isEmpty() ||
                email.isEmpty() ||
                password.isEmpty()
            ) {

                AlertDialog.Builder(this)
                    .setTitle("Registration")
                    .setMessage("Please fill all fields.")
                    .setPositiveButton("OK", null)
                    .show()

            } else {

                AlertDialog.Builder(this)
                    .setTitle("Registration Successful")
                    .setMessage(
                        "Welcome $name!\n\n" +
                                "USN: $usn"
                    )
                    .setPositiveButton("OK") { _, _ ->
                        finish()
                    }
                    .show()
            }
        }
    }
}