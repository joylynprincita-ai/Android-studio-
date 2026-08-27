package com.example.exp4

import android.Manifest
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.material.button.MaterialButton
import android.widget.EditText
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private val notificationChannelId = "login_channel"
    private val notificationId = 1001
    private val notificationPermissionCode = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChannel()

        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<MaterialButton>(R.id.loginButton)
        val registerText = findViewById<TextView>(R.id.registerText)

        loginButton.setOnClickListener {

            val user = username.text.toString().trim()
            val pass = password.text.toString().trim()

            if (user.isEmpty() || pass.isEmpty()) {

                AlertDialog.Builder(this)
                    .setTitle("Login Failed")
                    .setMessage("Please enter username and password.")
                    .setPositiveButton("OK", null)
                    .show()

            } else {

                // Request notification permission on Android 13+
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                    if (checkSelfPermission(
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {

                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                            notificationPermissionCode
                        )
                    }
                }

                // Popup message
                AlertDialog.Builder(this)
                    .setTitle("Login Successful")
                    .setMessage("Welcome, $user!\nYou logged in successfully.")
                    .setPositiveButton("OK") { _, _ ->

                        // Show notification
                        showLoginNotification(user)

                        // Explicit Intent
                        val intent =
                            Intent(this, HomeActivity::class.java)

                        // Send username
                        intent.putExtra("USERNAME", user)

                        startActivity(intent)
                    }
                    .show()
            }
        }

        // Intent to Registration Activity
        registerText.setOnClickListener {

            val intent =
                Intent(this, RegisterActivity::class.java)

            startActivity(intent)
        }
    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                notificationChannelId,
                "Login Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            channel.description = "Notifications for successful login"

            val notificationManager =
                getSystemService(NotificationManager::class.java)

            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showLoginNotification(username: String) {

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val notification = NotificationCompat.Builder(
            this,
            notificationChannelId
        )
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Login Successful")
            .setContentText("Welcome, $username! You logged in successfully.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat
            .from(this)
            .notify(notificationId, notification)
    }
}