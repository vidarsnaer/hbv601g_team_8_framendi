package com.example.hbv601g_t8

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.hbv601g_t8.GlobalVariables.CUSTOMER_SERVICE_ID
import com.example.hbv601g_t8.databinding.ActivityMainBinding
import com.google.android.material.button.MaterialButton
import io.agora.rtc2.IRtcEngineEventHandler
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.UUID

class DiscActivity : AppCompatActivity(), FilterListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var searchButton: MaterialButton
    private lateinit var popupWindow: PopupWindow
    private lateinit var applyFiltersButton: Button
    private lateinit var clearFilterButton: MaterialButton

    private var currentUserId: Long = -1

    private lateinit var conversationService: ConversationService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        conversationService = ConversationService()

        val prefs = getSharedPreferences(GlobalVariables.PREFS_NAME, Context.MODE_PRIVATE)
        currentUserId = getCurrentUserId()

        val REQUESTED_PERMISSIONS = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
        )

        /*
        if (GlobalVariables.USER_ID == null) {
            val user = supabase.auth.currentUserOrNull()
            val userid = user?.id
            println(userid)
            val uuid = UUID.fromString(userid)
            GlobalVariables.USER_ID = uuid
        }

         */

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, 22)

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

        binding.fab.setOnClickListener {
            val intent = Intent(this@DiscActivity, ChatOverviewActivity::class.java)

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
            R.id.nav_contact_service -> {
                    showContactServicePopup()
                    return true
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


    private fun showContactServicePopup() {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.popup_contact_service, null)
        val popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        popupWindow.isFocusable = true
        popupWindow.update()
        popupWindow.showAtLocation(binding.root, Gravity.CENTER, 0, 0)

        val etSubject = popupView.findViewById<EditText>(R.id.etSubject)
        val etMessage = popupView.findViewById<EditText>(R.id.etMessage)
        val btnSend = popupView.findViewById<Button>(R.id.btnSend)
        val btnCancel = popupView.findViewById<Button>(R.id.btnCancel)

        btnCancel.setOnClickListener { popupWindow.dismiss() }
        btnSend.setOnClickListener {
            val subjectText = etSubject.text.toString()
            val messageText = etMessage.text.toString()
            if (messageText.isNotEmpty() and subjectText.isNotEmpty()) {
                sendMessageToCustomerService(subjectText, messageText)
                popupWindow.dismiss()
            } else if (messageText.isEmpty()) {
                Toast.makeText(this, "Message cannot be empty", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Subject cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }





    private fun sendMessageToCustomerService(messageSubject: String, initialMessage: String) {
        lifecycleScope.launch {
            val conversationId = createNewConversationWithCustomerService(messageSubject)
            if (conversationId != (-1).toLong()) {
                sendMessageToConversation(conversationId, initialMessage)
                redirectToChat(conversationId)
            } else {
                Toast.makeText(this@DiscActivity, "Unable to start conversation with customer service.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun createNewConversationWithCustomerService(conversationSubject: String): Long {
        val result : Conversation
        runBlocking {
            result = conversationService.createConversation(sellerId = CUSTOMER_SERVICE_ID, title = "Customer service: ${conversationSubject}")!!
        }
        return result.conversationID!!
    }

    private suspend fun sendMessageToConversation(conversationId: Long, message: String) {
        //val timestamp = ZonedDateTime.now().toString()
        //var newMessage = Message(conversationID = conversationId, senderID = currentUserId, message = message, sentAt = timestamp, read = false)
        conversationService.sendMessage(conversationId = conversationId, messageText = message)
    }

    private fun redirectToChat(conversationId: Long) {
        val intent = Intent(this, ChatActivity::class.java).apply {
            putExtra("CHAT_ID", conversationId)
        }
        startActivity(intent)
    }

    private fun getCurrentUserId(): Long {
        val sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        return sharedPreferences.getLong(GlobalVariables.USER_ID, -1)  // Return -1 or another invalid value as default if not found
    }

}