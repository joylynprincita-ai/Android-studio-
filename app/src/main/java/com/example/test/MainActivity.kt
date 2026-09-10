package com.example.test

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var homeScreen: View
    private lateinit var fragmentContainer: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        Log.d("CollegeHelp", "MainActivity: onCreate")

        homeScreen = findViewById(R.id.homeScreen)
        fragmentContainer = findViewById(R.id.fragmentContainer)

        val btnAcademic = findViewById<Button>(R.id.btnAcademic)
        val btnTechnical = findViewById<Button>(R.id.btnTechnical)
        val btnLibrary = findViewById<Button>(R.id.btnLibrary)

        btnAcademic.setOnClickListener {
            openSupportFragment("Academic Support")
        }

        btnTechnical.setOnClickListener {
            openSupportFragment("Technical Support")
        }

        btnLibrary.setOnClickListener {
            openSupportFragment("Library Support")
        }
    }

    private fun openSupportFragment(category: String) {

        // HIDE HOME SCREEN
        homeScreen.visibility = View.GONE

        // SHOW FRAGMENT CONTAINER
        fragmentContainer.visibility = View.VISIBLE

        val fragment = SupportFragment.newInstance(category)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onBackPressed() {

        if (fragmentContainer.visibility == View.VISIBLE) {

            supportFragmentManager.popBackStack()

            fragmentContainer.visibility = View.GONE
            homeScreen.visibility = View.VISIBLE

        } else {
            super.onBackPressed()
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d("CollegeHelp", "MainActivity: onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("CollegeHelp", "MainActivity: onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("CollegeHelp", "MainActivity: onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("CollegeHelp", "MainActivity: onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("CollegeHelp", "MainActivity: onDestroy")
    }
}