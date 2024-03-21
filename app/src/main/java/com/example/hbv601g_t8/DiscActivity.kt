package com.example.hbv601g_t8

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hbv601g_t8.databinding.ActivityMainBinding

class DiscActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setSupportActionBar(binding.toolbar)


        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)


        //binding.fab.setOnClickListener { view ->
        //    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        //        .setAction("Action", null).show()
        //}
        binding.fab.setOnClickListener {
            val intent = Intent(this@DiscActivity, ChatOverviewActivity::class.java)
                //.apply {putExtra("USER_ID", currentUserId) }
            startActivity(intent)
        }

        binding.addNewDiscButton.setOnClickListener {
            val intent = Intent(this@DiscActivity, NewDiscActivity::class.java)
            startActivity(intent)
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
                startActivity(intent)
                true
            }
            R.id.nav_favorites -> {
                // Perform action when your menu item is clicked
                startActivity(Intent(this, FavoriteActivity::class.java))
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
}