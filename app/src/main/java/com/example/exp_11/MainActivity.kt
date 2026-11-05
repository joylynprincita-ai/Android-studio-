package com.example.exp_11

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.IOException
import java.util.Locale

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null
    private var currentMarker: Marker? = null

    // UI Controls
    private lateinit var etStudentName: EditText
    private lateinit var etStudentUsn: EditText
    private lateinit var etSearchLocation: EditText
    private lateinit var btnSearch: Button
    private lateinit var tvMapStatus: TextView
    private lateinit var mapWebView: WebView

    // Preset Location Coordinates
    private val locationBengaluru = LatLng(12.9716, 77.5946)
    private val locationDelhi = LatLng(28.6139, 77.2090)
    private val locationMumbai = LatLng(19.0760, 72.8777)
    private val locationTajMahal = LatLng(27.1751, 78.0421)
    private val locationLondon = LatLng(51.5074, -0.1278)

    private var currentLatLng: LatLng = locationBengaluru
    private var currentLocationName: String = "Bengaluru"
    private var currentMapType: String = "roadmap"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initUI()
        setupMapFragmentSafely()
        setupMapTypeButtons()
        setupSearchAndPresets()
        setupZoomControls()
    }

    private fun initUI() {
        etStudentName = findViewById(R.id.etStudentName)
        etStudentUsn = findViewById(R.id.etStudentUsn)
        etSearchLocation = findViewById(R.id.etSearchLocation)
        btnSearch = findViewById(R.id.btnSearch)
        tvMapStatus = findViewById(R.id.tvMapStatus)
        mapWebView = findViewById(R.id.mapWebView)

        setupWebViewFallback()
    }

    private fun setupMapFragmentSafely() {
        try {
            val mapFragment = SupportMapFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .replace(R.id.mapContainer, mapFragment)
                .commitAllowingStateLoss()
            mapFragment.getMapAsync(this)
        } catch (e: Exception) {
            e.printStackTrace()
            updateWebViewMap(currentMapType)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Configure UI Settings
        mMap?.uiSettings?.isZoomControlsEnabled = false
        mMap?.uiSettings?.isCompassEnabled = true

        // Set Default Location to Bengaluru with USN & Name marker
        navigateToLocation(currentLatLng, currentLocationName)

        // Marker Click Listener
        mMap?.setOnMarkerClickListener { marker ->
            marker.showInfoWindow()
            true
        }
    }

    private fun setupMapTypeButtons() {
        findViewById<Button>(R.id.btnTypeNormal).setOnClickListener {
            mMap?.mapType = GoogleMap.MAP_TYPE_NORMAL
            currentMapType = "roadmap"
            updateWebViewMap(currentMapType)
            Toast.makeText(this, "Map Type: Normal", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnTypeSatellite).setOnClickListener {
            mMap?.mapType = GoogleMap.MAP_TYPE_SATELLITE
            currentMapType = "satellite"
            updateWebViewMap(currentMapType)
            Toast.makeText(this, "Map Type: Satellite", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnTypeTerrain).setOnClickListener {
            mMap?.mapType = GoogleMap.MAP_TYPE_TERRAIN
            currentMapType = "terrain"
            updateWebViewMap(currentMapType)
            Toast.makeText(this, "Map Type: Terrain", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.btnTypeHybrid).setOnClickListener {
            mMap?.mapType = GoogleMap.MAP_TYPE_HYBRID
            currentMapType = "hybrid"
            updateWebViewMap(currentMapType)
            Toast.makeText(this, "Map Type: Hybrid", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupSearchAndPresets() {
        btnSearch.setOnClickListener {
            val query = etSearchLocation.text.toString().trim()
            if (query.isNotEmpty()) {
                searchLocation(query)
            } else {
                Toast.makeText(this, "Please enter a location name", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.chipBengaluru).setOnClickListener {
            navigateToLocation(locationBengaluru, "Bengaluru")
        }

        findViewById<Button>(R.id.chipDelhi).setOnClickListener {
            navigateToLocation(locationDelhi, "New Delhi")
        }

        findViewById<Button>(R.id.chipMumbai).setOnClickListener {
            navigateToLocation(locationMumbai, "Mumbai")
        }

        findViewById<Button>(R.id.chipTajMahal).setOnClickListener {
            navigateToLocation(locationTajMahal, "Taj Mahal")
        }

        findViewById<Button>(R.id.chipLondon).setOnClickListener {
            navigateToLocation(locationLondon, "London")
        }
    }

    private fun setupZoomControls() {
        findViewById<FloatingActionButton>(R.id.fabZoomIn).setOnClickListener {
            mMap?.animateCamera(CameraUpdateFactory.zoomIn())
            mapWebView.evaluateJavascript("if (typeof map !== 'undefined') map.zoomIn();", null)
        }

        findViewById<FloatingActionButton>(R.id.fabZoomOut).setOnClickListener {
            mMap?.animateCamera(CameraUpdateFactory.zoomOut())
            mapWebView.evaluateJavascript("if (typeof map !== 'undefined') map.zoomOut();", null)
        }

        findViewById<FloatingActionButton>(R.id.fabResetLocation).setOnClickListener {
            navigateToLocation(locationBengaluru, "Bengaluru")
        }
    }

    private fun searchLocation(locationName: String) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addressList: List<Address>? = geocoder.getFromLocationName(locationName, 1)
            if (!addressList.isNullOrEmpty()) {
                val address = addressList[0]
                val latLng = LatLng(address.latitude, address.longitude)
                navigateToLocation(latLng, locationName)
            } else {
                Toast.makeText(this, "Location not found: $locationName", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Search error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun navigateToLocation(latLng: LatLng, locationName: String) {
        currentLatLng = latLng
        currentLocationName = locationName

        val name = etStudentName.text.toString().ifEmpty { "Joylyn Fernandes" }
        val usn = etStudentUsn.text.toString().ifEmpty { "25MCAR0099" }

        // Update SDK Map if active
        mMap?.let { map ->
            currentMarker?.remove()

            val markerOptions = MarkerOptions()
                .position(latLng)
                .title("USN: $usn")
                .snippet("Name: $name | Place: $locationName")

            currentMarker = map.addMarker(markerOptions)
            currentMarker?.showInfoWindow()

            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12f))
        }

        // Update Status Banner
        tvMapStatus.text = "Loc: $locationName (${String.format(Locale.US, "%.3f, %.3f", latLng.latitude, latLng.longitude)}) | USN: $usn | $name"

        // Update Web Map view
        updateWebViewMap(currentMapType)
    }

    private fun setupWebViewFallback() {
        val webSettings: WebSettings = mapWebView.settings
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        webSettings.useWideViewPort = true
        webSettings.loadWithOverviewMode = true

        mapWebView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
            }
        }
    }

    private fun updateWebViewMap(mapType: String) {
        val lat = currentLatLng.latitude
        val lng = currentLatLng.longitude
        val usn = etStudentUsn.text.toString().ifEmpty { "25MCAR0099" }
        val name = etStudentName.text.toString().ifEmpty { "Joylyn Fernandes" }

        val html = """
            <!DOCTYPE html>
            <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
                <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
                <style>
                    html, body, #map { height: 100%; margin: 0; padding: 0; }
                    .info-box {
                        position: absolute; bottom: 10px; left: 10px; z-index: 1000;
                        background: rgba(255, 255, 255, 0.9); padding: 8px 12px;
                        border-radius: 6px; font-family: sans-serif; font-size: 12px;
                        box-shadow: 0 2px 6px rgba(0,0,0,0.3);
                    }
                </style>
            </head>
            <body>
                <div id="map"></div>
                <div class="info-box">
                    <b>Google Maps - Exp 11</b><br>
                    <b>USN:</b> $usn | <b>Name:</b> $name<br>
                    <b>Location:</b> $currentLocationName ($lat, $lng)
                </div>
                <script>
                    var map = L.map('map').setView([$lat, $lng], 13);
                    
                    var tileUrl = 'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png';
                    if ('$mapType' === 'satellite' || '$mapType' === 'hybrid') {
                        tileUrl = 'https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}';
                    }
                    
                    L.tileLayer(tileUrl, {
                        attribution: '&copy; OpenStreetMap contributors'
                    }).addTo(map);

                    var marker = L.marker([$lat, $lng]).addTo(map);
                    marker.bindPopup("<b>USN: $usn</b><br>Name: $name<br>Place: $currentLocationName").openPopup();
                </script>
            </body>
            </html>
        """.trimIndent()

        // Show mapWebView for interactive rendering with map tiles & markers
        mapWebView.visibility = View.VISIBLE
        mapWebView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
    }
}