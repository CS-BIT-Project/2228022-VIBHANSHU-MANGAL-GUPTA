package com.example.plan_your_day

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.*

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var destinationInput: EditText
    private lateinit var startNavigation: Button
    private var currentLatLng: LatLng? = null
    private val client = OkHttpClient()
    private val apiKey = "YOUR_GOOGLE_MAPS_API_KEY"  // Replace with your API Key

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        destinationInput = view.findViewById(R.id.destinationInput)
        startNavigation = view.findViewById(R.id.startNavigation)

        val mapFragment = childFragmentManager.findFragmentById(R.id.mapView) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        startNavigation.setOnClickListener {
            val destination = destinationInput.text.toString()
            if (destination.isNotEmpty()) {
                navigateToDestination(destination)
            }
        }

        return view
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.uiSettings.isZoomControlsEnabled = true

        requestLocationPermission()
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation()
        } else {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val newLatLng = LatLng(location.latitude, location.longitude) // Immutable local variable
                    currentLatLng = newLatLng  // Update the mutable property
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLatLng, 15f))
                }
            }
        }
    }


    private fun navigateToDestination(destination: String) {
        if (currentLatLng == null) {
            return // Exit the function if current location is not available
        }

        val geocoder = Geocoder(requireContext(), Locale.getDefault())

        Thread {
            try {
                val addresses = geocoder.getFromLocationName(destination, 1)

                if (!addresses.isNullOrEmpty()) {
                    val destinationLatLng = LatLng(addresses[0].latitude, addresses[0].longitude)

                    // Using a local immutable reference for origin location
                    val originLatLng = currentLatLng!!

                    activity?.runOnUiThread {
                        drawRoute(originLatLng, destinationLatLng)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }



    private fun drawRoute(origin: LatLng, destination: LatLng) {
        val url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=${origin.latitude},${origin.longitude}" +
                "&destination=${destination.latitude},${destination.longitude}" +
                "&mode=driving" +
                "&key=$apiKey"

        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val jsonResponse = response.body?.string()
                val jsonObject = JSONObject(jsonResponse!!)
                val routes = jsonObject.getJSONArray("routes")

                if (routes.length() > 0) {
                    val points = ArrayList<LatLng>()
                    val legs = routes.getJSONObject(0).getJSONArray("legs")
                    val steps = legs.getJSONObject(0).getJSONArray("steps")

                    for (i in 0 until steps.length()) {
                        val polyline = steps.getJSONObject(i).getJSONObject("polyline").getString("points")
                        points.addAll(decodePolyline(polyline))
                    }

                    activity?.runOnUiThread {
                        val polylineOptions = PolylineOptions().addAll(points).width(10f).color(ContextCompat.getColor(requireContext(), android.R.color.holo_blue_dark))
                        googleMap.addPolyline(polylineOptions)
                        googleMap.addMarker(MarkerOptions().position(destination).title("Destination"))
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destination, 12f))
                    }
                }
            }
        })
    }

    private fun decodePolyline(encoded: String): List<LatLng> {
        val polyline = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1F shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if ((result and 1) != 0) (result shr 1).inv() else (result shr 1)
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1F shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if ((result and 1) != 0) (result shr 1).inv() else (result shr 1)
            lng += dlng

            val point = LatLng(lat.toDouble() / 1E5, lng.toDouble() / 1E5)
            polyline.add(point)
        }
        return polyline
    }
}
