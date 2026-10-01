package com.example.menu_exp8

import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // ==========================================
        // TOOLBAR
        // ==========================================

        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        setSupportActionBar(toolbar)


        // ==========================================
        // WEBVIEW
        // ==========================================

        webView = findViewById(R.id.webView)

        webView.webViewClient = WebViewClient()

        // Enable JavaScript
        webView.settings.javaScriptEnabled = true

        // Enable DOM storage
        webView.settings.domStorageEnabled = true


        // ==========================================
        // POPUP MENU
        // ==========================================

        val popupButton = findViewById<Button>(R.id.btnPopup)

        popupButton.setOnClickListener {

            val popupMenu = PopupMenu(
                this,
                popupButton
            )

            popupMenu.menuInflater.inflate(
                R.menu.menu_popup,
                popupMenu.menu
            )

            popupMenu.setOnMenuItemClickListener { item ->

                when (item.itemId) {

                    R.id.popup_profile -> {

                        Toast.makeText(
                            this,
                            "Profile selected",
                            Toast.LENGTH_SHORT
                        ).show()

                        true
                    }

                    R.id.popup_settings -> {

                        Toast.makeText(
                            this,
                            "Settings selected",
                            Toast.LENGTH_SHORT
                        ).show()

                        true
                    }

                    R.id.popup_help -> {

                        Toast.makeText(
                            this,
                            "Help selected",
                            Toast.LENGTH_SHORT
                        ).show()

                        true
                    }

                    else -> false
                }
            }

            popupMenu.show()
        }


        // ==========================================
        // WEBVIEW BUTTON
        // ==========================================

        val webButton = findViewById<Button>(R.id.btnWebView)

        webButton.setOnClickListener {

            webView.visibility = View.VISIBLE

            webView.loadUrl(
                "https://example.com/"
            )
        }


        // ==========================================
        // CONTEXT MENU
        // ==========================================

        val contextText = findViewById<TextView>(
            R.id.txtContext
        )

        registerForContextMenu(contextText)
    }


    // ==========================================
    // OPTIONS MENU
    // ==========================================

    override fun onCreateOptionsMenu(
        menu: Menu
    ): Boolean {

        menuInflater.inflate(
            R.menu.menu_options,
            menu
        )

        return true
    }


    override fun onOptionsItemSelected(
        item: MenuItem
    ): Boolean {

        return when (item.itemId) {

            R.id.menu_home -> {

                Toast.makeText(
                    this,
                    "Home selected",
                    Toast.LENGTH_SHORT
                ).show()

                true
            }

            R.id.menu_web -> {

                webView.visibility = View.VISIBLE

                webView.loadUrl(
                    "https://example.com/"
                )

                true
            }

            R.id.menu_about -> {

                Toast.makeText(
                    this,
                    "Experiment 8 - Menus and WebView",
                    Toast.LENGTH_LONG
                ).show()

                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


    // ==========================================
    // CONTEXT MENU
    // ==========================================

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {

        super.onCreateContextMenu(
            menu,
            v,
            menuInfo
        )

        menuInflater.inflate(
            R.menu.menu_context,
            menu
        )

        menu.setHeaderTitle(
            "Select Action"
        )
    }


    override fun onContextItemSelected(
        item: MenuItem
    ): Boolean {

        return when (item.itemId) {

            R.id.context_edit -> {

                Toast.makeText(
                    this,
                    "Edit selected",
                    Toast.LENGTH_SHORT
                ).show()

                true
            }

            R.id.context_copy -> {

                Toast.makeText(
                    this,
                    "Copy selected",
                    Toast.LENGTH_SHORT
                ).show()

                true
            }

            R.id.context_delete -> {

                Toast.makeText(
                    this,
                    "Delete selected",
                    Toast.LENGTH_SHORT
                ).show()

                true
            }

            else -> super.onContextItemSelected(item)
        }
    }
}