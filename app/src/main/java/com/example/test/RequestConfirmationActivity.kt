package com.example.test

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class RequestConfirmationActivity : AppCompatActivity() {

    private val requestNotificationPermission =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_request_confirmation)

        Log.d(
            "CollegeHelp",
            "RequestConfirmationActivity: onCreate"
        )

        val studentName =
            intent.getStringExtra("studentName") ?: ""

        val category =
            intent.getStringExtra("category") ?: ""

        val tvStudentName =
            findViewById<TextView>(R.id.tvStudentName)

        val tvCategory =
            findViewById<TextView>(R.id.tvCategory)

        val returnHome =
            findViewById<Button>(R.id.btnReturnHome)

        tvStudentName.text = "Student Name: $studentName"

        tvCategory.text = "Support Category: $category"

        createNotificationChannel()

        askNotificationPermission()

        showNotification(studentName, category)

        returnHome.setOnClickListener {

            val homeIntent = Intent(
                this,
                MainActivity::class.java
            )

            homeIntent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP

            startActivity(homeIntent)

            finish()
        }
    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                "college_help_channel",
                "CollegeHelp Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            val manager =
                getSystemService(
                    Context.NOTIFICATION_SERVICE
                ) as NotificationManager

            manager.createNotificationChannel(channel)
        }
    }

    private fun askNotificationPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (
                checkSelfPermission(
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                requestNotificationPermission.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )
            }
        }
    }

    private fun showNotification(
        studentName: String,
        category: String
    ) {

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val notification =
            NotificationCompat.Builder(
                this,
                "college_help_channel"
            )
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("CollegeHelp")
                .setContentText(
                    "Request submitted for $category"
                )
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(
                            "$studentName's $category request has been submitted successfully."
                        )
                )
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .build()

        NotificationManagerCompat
            .from(this)
            .notify(1001, notification)
    }

    override fun onStart() {
        super.onStart()

        Log.d(
            "CollegeHelp",
            "RequestConfirmationActivity: onStart"
        )
    }

    override fun onResume() {
        super.onResume()

        Log.d(
            "CollegeHelp",
            "RequestConfirmationActivity: onResume"
        )
    }

    override fun onPause() {
        super.onPause()

        Log.d(
            "CollegeHelp",
            "RequestConfirmationActivity: onPause"
        )
    }

    override fun onStop() {
        super.onStop()

        Log.d(
            "CollegeHelp",
            "RequestConfirmationActivity: onStop"
        )
    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d(
            "CollegeHelp",
            "RequestConfirmationActivity: onDestroy"
        )
    }
}