package com.example.experiment6

import android.app.AlertDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_register)

        val name =
            findViewById<EditText>(R.id.nameEditText)

        val usn =
            findViewById<EditText>(R.id.usnEditText)

        val email =
            findViewById<EditText>(R.id.emailEditText)

        val password =
            findViewById<EditText>(
                R.id.registerPasswordEditText
            )

        val genderGroup =
            findViewById<RadioGroup>(
                R.id.genderGroup
            )

        val courseSpinner =
            findViewById<Spinner>(
                R.id.courseSpinner
            )

        val java =
            findViewById<CheckBox>(
                R.id.javaCheckBox
            )

        val python =
            findViewById<CheckBox>(
                R.id.pythonCheckBox
            )

        val web =
            findViewById<CheckBox>(
                R.id.webCheckBox
            )

        val registerButton =
            findViewById<Button>(
                R.id.registerSubmitButton
            )


        // Spinner
        val courses = arrayOf(
            "Select Course",
            "MCA",
            "MBA",
            "BCA",
            "B.Tech"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            courses
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        courseSpinner.adapter = adapter


        registerButton.setOnClickListener {

            val studentName =
                name.text.toString().trim()

            val studentUSN =
                usn.text.toString().trim()

            val studentEmail =
                email.text.toString().trim()

            val studentPassword =
                password.text.toString().trim()


            if (
                studentName.isEmpty() ||
                studentUSN.isEmpty() ||
                studentEmail.isEmpty() ||
                studentPassword.isEmpty()
            ) {

                AlertDialog.Builder(this)
                    .setTitle("Registration Failed")
                    .setMessage(
                        "Please fill all required fields."
                    )
                    .setPositiveButton("OK", null)
                    .show()

                return@setOnClickListener
            }


            // Gender
            val genderId =
                genderGroup.checkedRadioButtonId

            val gender =
                if (genderId != -1) {

                    findViewById<RadioButton>(
                        genderId
                    ).text.toString()

                } else {
                    "Not Selected"
                }


            // Skills
            val skills = mutableListOf<String>()

            if (java.isChecked) {
                skills.add("Java")
            }

            if (python.isChecked) {
                skills.add("Python")
            }

            if (web.isChecked) {
                skills.add("Web Development")
            }


            val skillText =
                if (skills.isEmpty()) {
                    "None"
                } else {
                    skills.joinToString(", ")
                }


            val course =
                courseSpinner.selectedItem.toString()


            AlertDialog.Builder(this)
                .setTitle("Registration Successful")
                .setMessage(
                    "Welcome $studentName!\n\n" +
                            "USN: $studentUSN\n" +
                            "Course: $course\n" +
                            "Skills: $skillText"
                )
                .setPositiveButton("OK") {
                        _, _ ->

                    finish()
                }
                .show()
        }
    }
}