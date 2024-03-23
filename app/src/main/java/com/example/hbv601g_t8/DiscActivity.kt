package com.example.hbv601g_t8

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hbv601g_t8.databinding.ActivityMainBinding
import java.util.Date
import java.util.Locale
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.Spinner
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class DiscActivity : AppCompatActivity(), FilterListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var searchButton: Button
    private lateinit var popupWindow: PopupWindow
    private lateinit var applyFiltersButton: Button
    private lateinit var clearFilterButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Instantiate a PopupWindow
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView: View = inflater.inflate(R.layout.filters_layout, null)
        popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // Set attributes for the PopupWindow
        popupWindow.isFocusable = true // Allows the PopupWindow to receive touch events
        //popupWindow.animationStyle = R.style.PopupAnimation // Optional: Set animation style

        // Find Spinners in the custom layout
        val filterSpinnerState: Spinner = popupView.findViewById(R.id.FilterSpinnerState)
        val filterSpinnerType: Spinner = popupView.findViewById(R.id.FilterSpinnerType)

        // Populate Spinners with options
        val states = resources.getStringArray(R.array.filter_states)
        val types = resources.getStringArray(R.array.filter_disc_types)
        filterSpinnerState.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, states)
        filterSpinnerType.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, types)

        applyFiltersButton = popupView.findViewById(R.id.FilterApplyButton)

        applyFiltersButton.setOnClickListener {
            println("Apply button clicked")
            val priceMinEditText = popupView.findViewById<EditText>(R.id.etMinPrice)
            val priceMaxEditText = popupView.findViewById<EditText>(R.id.etMaxPrice)

            val priceMin = priceMinEditText.text.toString()
            val priceMax = priceMaxEditText.text.toString()
            val state = filterSpinnerState.selectedItem.toString()
            val type = filterSpinnerType.selectedItem.toString()

            if (priceMin.isEmpty() || priceMax.isEmpty()) {
                Toast.makeText(this, "Min/Max price must not be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (priceMin.toInt() > priceMax.toInt()) {
                Toast.makeText(this, "Min price cannot be greater than Max price", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            onFiltersApplied(priceMin, priceMax, state, type)

            popupWindow.dismiss()
        }

        setSupportActionBar(binding.toolbar)


        // Find the NavController using the correct ID
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val navController = navHostFragment.navController

        // Setup the ActionBar with the NavController
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Set click listener for the search bar
        searchButton = findViewById(R.id.search_Button)

        searchButton.setOnClickListener {
            toggleFilterOptionsVisibility()
            println("You click the searchButton")
        }

        clearFilterButton = findViewById(R.id.clearFilter_Button)

        clearFilterButton.setOnClickListener {

            val fragment = navHostFragment.childFragmentManager.fragments.firstOrNull { it is DiscListFragment }
            if (fragment != null) {
                // Fragment found, you can proceed with updating it
                if (fragment is DiscListFragment) {
                    fragment.clearFilters()
                    clearFilterButton.visibility = View.GONE
                }
            } else {
                // Fragment not found
                println("Fragment: DiscListFragment not found in clearFilterButton on click listener")
            }
        }

        //binding.fab.setOnClickListener { view ->
        //    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        //        .setAction("Action", null).show()
        //}
        binding.fab.setOnClickListener {
            val intent = Intent(this@DiscActivity, ChatOverviewActivity::class.java)
                //.apply {putExtra("USER_ID", currentUserId) }
            startActivity(intent)

            Notification.showNotification(
                this,
                "Message Notification",
                "content"
            )

        }

        binding.addNewDiscButton.setOnClickListener {
            val intent = Intent(this@DiscActivity, NewDiscActivity::class.java)
            startActivity(intent)
        }

        runBlocking {
            withContext(Dispatchers.IO) {
                val identities = SupabaseManager.supabase.auth.currentUserOrNull()?.id
                println("IDENTITIES: $identities")
            }
        }

    }

    private fun toggleFilterOptionsVisibility() {
        if (popupWindow.isShowing) {
            popupWindow.dismiss()
        } else {
            popupWindow.showAsDropDown(searchButton)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.nav_settings -> {
                // Perform action when your menu item is clicked
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            R.id.nav_logout -> {
                val editor = getSharedPreferences(GlobalVariables.PREFS_NAME, Context.MODE_PRIVATE).edit()
                editor.putBoolean(GlobalVariables.KEY_IS_LOGGED_IN, false)
                editor.apply()
                val intent = Intent(this@DiscActivity, StartPageActivity::class.java)
                runBlocking {
                    withContext(Dispatchers.IO) {
                        SupabaseManager.supabase.auth.signOut()
                    }
                }
                startActivity(intent)
                true
            }
            R.id.nav_favorites -> {
                // Perform action when your menu item is clicked
                startActivity(Intent(this, FavoriteActivity::class.java))
                true
            }
            R.id.nav_my_discs -> {
                startActivity(Intent(this, MyDiscsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun onFiltersApplied(priceMin: String, priceMax: String, state: String, type: String) {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val fragment = navHostFragment.childFragmentManager.fragments.firstOrNull { it is DiscListFragment }
        if (fragment != null) {
            // Fragment found, you can proceed with updating it
            if (fragment is DiscListFragment) {
                fragment.updateFilters(priceMin, priceMax, state, type)
                fragment.filterAndRefreshView()
                clearFilterButton.visibility = View.VISIBLE
            }
        } else {
            // Fragment not found
            println("Fragment: DiscListFragment not found in onFiltersApplied method")
        }
        /*println("onFiltersApplied method called")
        val discListFragment = supportFragmentManager.findFragmentById(R.id.cv_cardview) as? DiscListFragment
        discListFragment?.apply {
            updateFilters(priceMin, priceMax, state, type)
            recreateView()
        } ?: run {
            println("Fragment not found")
        }*/
    }
}