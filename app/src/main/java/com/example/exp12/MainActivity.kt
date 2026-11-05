package com.example.exp12

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var etUrl: EditText
    private lateinit var btnFetch: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        etUrl = findViewById(R.id.etUrl)
        btnFetch = findViewById(R.id.btnFetch)
        progressBar = findViewById(R.id.progressBar)
        tvResult = findViewById(R.id.tvResult)

        btnFetch.setOnClickListener {
            val urlString = etUrl.text.toString().trim()
            if (urlString.isNotEmpty()) {
                fetchWebServiceData(urlString)
            } else {
                tvResult.text = getString(R.string.msg_enter_url)
            }
        }
    }

    private fun fetchWebServiceData(urlString: String) {
        progressBar.visibility = View.VISIBLE
        btnFetch.isEnabled = false
        tvResult.text = getString(R.string.msg_connecting, urlString)

        lifecycleScope.launch(Dispatchers.IO) {
            val resultText: String = try {
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 10000
                connection.readTimeout = 10000
                connection.setRequestProperty("Accept", "application/json")
                connection.setRequestProperty("User-Agent", "Android-App")

                val responseCode = connection.responseCode
                val responseMessage = connection.responseMessage

                val inputStream = if (responseCode in 200..299) {
                    connection.inputStream
                } else {
                    connection.errorStream
                }

                val reader = BufferedReader(InputStreamReader(inputStream))
                val responseBody = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    responseBody.append(line).append("\n")
                }
                reader.close()
                connection.disconnect()

                buildString {
                    append("=== HTTP RESPONSE ===\n")
                    append("Status Code: $responseCode $responseMessage\n")
                    append("Content Type: ${connection.contentType}\n")
                    append("Content Length: ${connection.contentLength}\n\n")
                    append("=== RESPONSE BODY ===\n")
                    append(responseBody.toString().ifEmpty { "(Empty Response Body)" })
                }
            } catch (e: Exception) {
                "=== ERROR ===\nFailed to access web service:\n${e.localizedMessage ?: e.toString()}"
            }

            withContext(Dispatchers.Main) {
                progressBar.visibility = View.GONE
                btnFetch.isEnabled = true
                tvResult.text = resultText
            }
        }
    }
}
