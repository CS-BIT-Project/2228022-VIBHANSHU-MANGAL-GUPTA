package com.example.plan_your_day

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class ActivityHomepage : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_homepage)

        checkUserAuthentication()
        setupBottomNavigation()
        setupQuickActions()
    }

    override fun onResume() {
        super.onResume()
        showBottomNavigation() // Show navigation panel when returning to homepage
    }

    /**
     * Handles bottom navigation selection
     */
    private fun setupBottomNavigation() {
        bottomNavigationView = findViewById(R.id.bottomNavigation)

        bottomNavigationView.setOnItemSelectedListener { item ->
            showBottomNavigation() // Ensure navigation panel is visible when a nav item is clicked

            when (item.itemId) {
                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                R.id.nav_maps -> {
                    loadFragment(MapFragment())
                    true
                }
                R.id.nav_home -> {
                    // Always restart ActivityHomepage when "Home" is clicked
                    val intent = Intent(this, ActivityHomepage::class.java)
                    startActivity(intent)
                    finish()
                    true
                }

                else -> false
            }
        }
    }

    /**
     * Sealed class for handling Quick Actions
     */
    sealed class QuickAction(val id: Int) {
        data class ActivityAction(val activityClass: Class<*>, val actionId: Int) : QuickAction(actionId)
        data class FragmentAction(val fragmentClass: Class<out Fragment>, val actionId: Int) : QuickAction(actionId)
    }

    /**
     * Set up quick actions for navigation
     */
    private fun setupQuickActions() {
        val quickActions = listOf(
            QuickAction.ActivityAction(ActionItinerary::class.java, R.id.quick_action1),
            QuickAction.ActivityAction(PackingListActivity::class.java, R.id.quick_action2),
            QuickAction.ActivityAction(TodoActivity::class.java, R.id.quick_action3),
            QuickAction.ActivityAction(ActionExpenses::class.java, R.id.quick_action4),
            QuickAction.FragmentAction(WeatherFragment::class.java, R.id.quick_action5),
            QuickAction.ActivityAction(NotesActivity::class.java, R.id.quick_action6),
            QuickAction.FragmentAction(CurrencyConverterFragment::class.java, R.id.quick_action7)

        )

        quickActions.forEach { action ->
            findViewById<View>(action.id)?.setOnClickListener {
                hideBottomNavigation() // Hide navigation when a Quick Action is used
                handleQuickAction(action)
            }
        }
    }

    /**
     * Handles Quick Action clicks for both activities and fragments
     */
    private fun handleQuickAction(action: QuickAction) {
        when (action) {
            is QuickAction.ActivityAction -> {
                startActivity(Intent(this, action.activityClass))
            }
            is QuickAction.FragmentAction -> {
                loadFragment(action.fragmentClass.newInstance())
            }
        }
    }

    /**
     * Loads a fragment into the container
     */
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .addToBackStack(null)
            .commit()
    }

    /**
     * Generates a URL for Google Maps Directions API
     */
    private fun getDirectionsUrl(origin: LatLng, dest: LatLng): String {
        val apiKey = "AIzaSyB0HYXVr-9mDOwLdmscbt7mtP_I39IZ0f4"
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}&destination=${dest.latitude},${dest.longitude}&mode=driving&key=$apiKey"
    }

    /**
     * Checks if the user is authenticated, otherwise redirects to login
     */
    private fun checkUserAuthentication() {
        val firebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuth.currentUser == null) {
            startActivity(Intent(this, ActivityLogin::class.java))
            finish()
        }
    }

    /**
     * Hides the bottom navigation panel
     */
    private fun hideBottomNavigation() {
        bottomNavigationView.visibility = View.GONE
    }

    /**
     * Shows the bottom navigation panel
     */
    private fun showBottomNavigation() {
        bottomNavigationView.visibility = View.VISIBLE
    }
}
