package com.example.exp10

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var btnFetchContacts: Button
    private lateinit var tvStatus: TextView
    private lateinit var listViewContacts: ListView
    private val contactList = ArrayList<String>()
    private lateinit var adapter: ArrayAdapter<String>

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            loadContacts()
        } else {
            tvStatus.text = getString(R.string.permission_denied)
            Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnFetchContacts = findViewById(R.id.btnFetchContacts)
        tvStatus = findViewById(R.id.tvStatus)
        listViewContacts = findViewById(R.id.listViewContacts)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, contactList)
        listViewContacts.adapter = adapter

        btnFetchContacts.setOnClickListener {
            checkAndFetchContacts()
        }
    }

    private fun checkAndFetchContacts() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED -> {
                loadContacts()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
            }
        }
    }

    private fun loadContacts() {
        contactList.clear()

        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        val cursor: Cursor? = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            null,
            null,
            "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"
        )

        cursor?.use {
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (it.moveToNext()) {
                val name = if (nameIndex != -1) it.getString(nameIndex) else "Unknown"
                val number = if (numberIndex != -1) it.getString(numberIndex) else "No Number"
                contactList.add("$name\n$number")
            }
        }

        adapter.notifyDataSetChanged()

        if (contactList.isEmpty()) {
            tvStatus.text = getString(R.string.no_contacts_found)
        } else {
            tvStatus.text = getString(R.string.contacts_loaded_count, contactList.size)
        }
    }
}