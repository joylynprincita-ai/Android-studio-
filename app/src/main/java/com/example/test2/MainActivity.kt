package com.example.test2

import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import java.util.Locale
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private var googleMap: GoogleMap? = null
    private val placesList = mutableListOf<PlaceItem>()
    private var plannedRoute = listOf<PlaceItem>()
    private lateinit var adapter: PlaceAdapter

    private var currentPolyline: Polyline? = null

    // UI references
    private lateinit var listViewPlaces: ListView
    private lateinit var btnAddPlace: Button
    private lateinit var btnPlanDay: Button
    private lateinit var etSearchPlace: EditText
    private lateinit var btnSearch: Button
    private lateinit var placeMapCanvas: PlaceMapCanvas

    // Portrait view containers / tab buttons
    private var containerList: View? = null
    private var containerMap: View? = null
    private var btnTabList: Button? = null
    private var btnTabMap: Button? = null

    private val executor = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI elements
        listViewPlaces = findViewById(R.id.listViewPlaces)
        btnAddPlace = findViewById(R.id.btnAddPlace)
        btnPlanDay = findViewById(R.id.btnPlanDay)
        etSearchPlace = findViewById(R.id.etSearchPlace)
        btnSearch = findViewById(R.id.btnSearch)
        placeMapCanvas = findViewById(R.id.placeMapCanvas)

        containerList = findViewById(R.id.containerList)
        containerMap = findViewById(R.id.containerMap)
        btnTabList = findViewById(R.id.btnTabList)
        btnTabMap = findViewById(R.id.btnTabMap)

        setupDefaultPlaces()

        // Set up List Adapter
        adapter = PlaceAdapter(
            context = this,
            places = placesList,
            onItemChecked = { _, _ ->
                updateMapMarkers()
            },
            onItemDeleted = { item ->
                placesList.remove(item)
                adapter.notifyDataSetChanged()
                updateMapMarkers()
                Toast.makeText(this, "Removed ${item.name}", Toast.LENGTH_SHORT).show()
            }
        )
        listViewPlaces.adapter = adapter

        // Setup Search Box
        btnSearch.setOnClickListener {
            performPlaceSearch()
        }
        etSearchPlace.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performPlaceSearch()
                true
            } else {
                false
            }
        }

        // Setup Buttons
        btnAddPlace.setOnClickListener {
            showAddPlaceDialog()
        }

        btnPlanDay.setOnClickListener {
            planBestRoute()
        }

        // Setup Canvas Map interactions
        placeMapCanvas.onMapLongClickListener = { lat, lng ->
            showAddPlaceDialog(LatLng(lat, lng))
        }

        // Setup Tabs (Portrait Mode)
        btnTabList?.setOnClickListener {
            showPortraitTab(showList = true)
        }

        btnTabMap?.setOnClickListener {
            showPortraitTab(showList = false)
        }

        // Initialize Google Map Fragment
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        updateMapMarkers()
    }

    private fun setupDefaultPlaces() {
        if (placesList.isEmpty()) {
            placesList.add(PlaceItem(name = "Eiffel Tower", latitude = 48.8584, longitude = 2.2945))
            placesList.add(PlaceItem(name = "Louvre Museum", latitude = 48.8606, longitude = 2.3376))
            placesList.add(PlaceItem(name = "Arc de Triomphe", latitude = 48.8738, longitude = 2.2950))
            placesList.add(PlaceItem(name = "Notre-Dame Cathedral", latitude = 48.8530, longitude = 2.3499))
        }
    }

    private fun performPlaceSearch() {
        val query = etSearchPlace.text.toString().trim()
        if (query.isEmpty()) {
            Toast.makeText(this, "Enter a place name to search", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(this, "Searching location for: '$query'...", Toast.LENGTH_SHORT).show()

        executor.execute {
            var foundLat: Double? = null
            var foundLng: Double? = null
            var foundName = query

            try {
                val geocoder = Geocoder(this@MainActivity, Locale.getDefault())
                val addresses: List<Address>? = geocoder.getFromLocationName(query, 1)

                if (!addresses.isNullOrEmpty()) {
                    val addr = addresses[0]
                    foundLat = addr.latitude
                    foundLng = addr.longitude
                    foundName = addr.featureName ?: addr.getAddressLine(0) ?: query
                }
            } catch (_: Exception) {
                // Ignore geocoder exception and fall through to fallback lookup
            }

            // Fallback dictionary for famous locations if geocoder offline
            if (foundLat == null || foundLng == null) {
                val fallback = lookupKnownLocation(query)
                if (fallback != null) {
                    foundLat = fallback.latitude
                    foundLng = fallback.longitude
                    foundName = fallback.name
                }
            }

            runOnUiThread {
                if (foundLat != null && foundLng != null) {
                    val newPlace = PlaceItem(
                        name = foundName,
                        latitude = foundLat,
                        longitude = foundLng
                    )
                    placesList.add(newPlace)
                    adapter.notifyDataSetChanged()
                    updateMapMarkers()

                    // Move and Zoom Google Map camera to searched location
                    googleMap?.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(LatLng(foundLat, foundLng), 14f)
                    )

                    etSearchPlace.text.clear()
                    Toast.makeText(this@MainActivity, "Added: $foundName", Toast.LENGTH_SHORT).show()
                    showPortraitTab(showList = false)
                } else {
                    Toast.makeText(this@MainActivity, "Location '$query' not found. Try entering a famous place name.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun lookupKnownLocation(query: String): PlaceItem? {
        val q = query.lowercase(Locale.getDefault())
        return when {
            q.contains("taj mahal") -> PlaceItem(name = "Taj Mahal, India", latitude = 27.1751, longitude = 78.0421)
            q.contains("eiffel") -> PlaceItem(name = "Eiffel Tower, Paris", latitude = 48.8584, longitude = 2.2945)
            q.contains("statue of liberty") || q.contains("liberty") -> PlaceItem(name = "Statue of Liberty, NY", latitude = 40.6892, longitude = -74.0445)
            q.contains("times square") -> PlaceItem(name = "Times Square, NY", latitude = 40.7580, longitude = -73.9855)
            q.contains("colosseum") -> PlaceItem(name = "Colosseum, Rome", latitude = 41.8902, longitude = 12.4922)
            q.contains("big ben") || q.contains("london") -> PlaceItem(name = "Big Ben, London", latitude = 51.5007, longitude = -0.1246)
            q.contains("tokyo tower") || q.contains("tokyo") -> PlaceItem(name = "Tokyo Tower, Japan", latitude = 35.6586, longitude = 139.7454)
            q.contains("sydney opera") || q.contains("sydney") -> PlaceItem(name = "Sydney Opera House", latitude = -33.8568, longitude = 151.2153)
            q.contains("burj khalifa") || q.contains("dubai") -> PlaceItem(name = "Burj Khalifa, Dubai", latitude = 25.1972, longitude = 55.2744)
            q.contains("great wall") || q.contains("china") -> PlaceItem(name = "Great Wall of China", latitude = 40.4319, longitude = 116.5704)
            q.contains("pyramid") || q.contains("giza") -> PlaceItem(name = "Pyramids of Giza, Egypt", latitude = 29.9792, longitude = 31.1342)
            else -> null
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Enable full zoom & map gestures controls!
        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isZoomGesturesEnabled = true
        map.uiSettings.isScrollGesturesEnabled = true
        map.uiSettings.isRotateGesturesEnabled = true
        map.uiSettings.isTiltGesturesEnabled = true
        map.uiSettings.isCompassEnabled = true
        map.uiSettings.isMapToolbarEnabled = true

        if (placesList.isNotEmpty()) {
            val first = placesList.first()
            map.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(first.latitude, first.longitude),
                    13f
                )
            )
        }

        map.setOnMapLongClickListener { latLng ->
            showAddPlaceDialog(latLng)
        }

        map.setOnMarkerClickListener { marker ->
            marker.showInfoWindow()
            false
        }

        updateMapMarkers()
    }

    private fun updateMapMarkers() {
        placeMapCanvas.setPlaces(placesList, plannedRoute)

        val map = googleMap ?: return
        map.clear()
        currentPolyline = null

        if (placesList.isEmpty()) return

        val builder = LatLngBounds.Builder()

        for (place in placesList) {
            val pos = LatLng(place.latitude, place.longitude)
            builder.include(pos)

            val markerOptions = MarkerOptions()
                .position(pos)
                .title(place.name)

            when {
                place.isFoodStop -> {
                    markerOptions.snippet("Food Stop 🍔")
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                }
                place.isRestStop -> {
                    markerOptions.snippet("Rest Stop ☕")
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                }
                place.isDone -> {
                    markerOptions.snippet("Completed ✓")
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                }
                else -> {
                    markerOptions.snippet("Sightseeing Spot")
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                }
            }

            map.addMarker(markerOptions)
        }

        try {
            val bounds = builder.build()
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 120))
        } catch (_: Exception) {
            // Handle single marker bounds
        }
    }

    private fun planBestRoute() {
        if (placesList.isEmpty()) {
            Toast.makeText(this, "Add places to plan a route!", Toast.LENGTH_SHORT).show()
            return
        }

        val plannedResult = RoutePlanner.planDay(placesList)

        placesList.clear()
        placesList.addAll(plannedResult.orderedPlaces)
        plannedRoute = plannedResult.orderedPlaces
        adapter.notifyDataSetChanged()

        updateMapMarkers()

        val map = googleMap
        if (map != null && plannedResult.routePolylinePoints.isNotEmpty()) {
            val polylineOptions = PolylineOptions()
                .addAll(plannedResult.routePolylinePoints)
                .color(Color.BLUE)
                .width(14f)
                .geodesic(true)

            currentPolyline = map.addPolyline(polylineOptions)
        }

        Toast.makeText(this, "Day Planned! Best Route drawn with Food 🍔 & Rest ☕ stops.", Toast.LENGTH_LONG).show()

        // Automatically switch portrait tab to Map View
        showPortraitTab(showList = false)
    }

    private fun showAddPlaceDialog(latLng: LatLng? = null) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_place, null)
        val etPlaceName = dialogView.findViewById<EditText>(R.id.etPlaceName)
        val etLatitude = dialogView.findViewById<EditText>(R.id.etLatitude)
        val etLongitude = dialogView.findViewById<EditText>(R.id.etLongitude)

        if (latLng != null) {
            etLatitude.setText(latLng.latitude.toString())
            etLongitude.setText(latLng.longitude.toString())
            etPlaceName.hint = "Name for location (${String.format(Locale.US, "%.4f", latLng.latitude)}, ${String.format(Locale.US, "%.4f", latLng.longitude)})"
        } else {
            etLatitude.setText("48.8566")
            etLongitude.setText("2.3522")
        }

        AlertDialog.Builder(this)
            .setTitle("Add Sightseeing Place")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val nameInput = etPlaceName.text.toString().trim()
                val latInput = etLatitude.text.toString().toDoubleOrNull() ?: 48.8566
                val lngInput = etLongitude.text.toString().toDoubleOrNull() ?: 2.3522

                val name = nameInput.ifEmpty { "Sightseeing Spot #${placesList.size + 1}" }

                val newPlace = PlaceItem(
                    name = name,
                    latitude = latInput,
                    longitude = lngInput
                )

                placesList.add(newPlace)
                adapter.notifyDataSetChanged()
                updateMapMarkers()

                Toast.makeText(this, "Added: $name", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showPortraitTab(showList: Boolean) {
        if (containerList != null && containerMap != null) {
            if (showList) {
                containerList?.visibility = View.VISIBLE
                containerMap?.visibility = View.GONE
                btnTabList?.setTypeface(null, android.graphics.Typeface.BOLD)
                btnTabMap?.setTypeface(null, android.graphics.Typeface.NORMAL)
            } else {
                containerList?.visibility = View.GONE
                containerMap?.visibility = View.VISIBLE
                btnTabList?.setTypeface(null, android.graphics.Typeface.NORMAL)
                btnTabMap?.setTypeface(null, android.graphics.Typeface.BOLD)
                updateMapMarkers()
            }
        }
    }
}
