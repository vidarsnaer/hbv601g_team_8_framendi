package com.example.hbv601g_t8


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ViewUtils
import com.example.hbv601g_t8.databinding.ActivityMainBinding

class StartPageActivity:AppCompatActivity(){

    private lateinit var loginButton : Button
    private lateinit var registerButton : Button

    private val PREFS_NAME = "MyPrefs"
    private val KEY_IS_LOGGED_IN = "isLoggedIn"

    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)

        if (isLoggedIn()) {
            redirectToHome()
        }
        else{
            setContentView(R.layout.start_screen)
        }

        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerButton)
/*

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()*/



        loginButton.setOnClickListener{
            val intent = Intent(this@StartPageActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        registerButton.setOnClickListener {
            val intent = Intent(this@StartPageActivity, RegisterActivity::class.java)
            startActivity(intent)

        }
    }

    /***
     * checks login state from SharedPreferences
     */
    private fun isLoggedIn(): Boolean {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    /**
     * Redirects to the DiscActivity and finishes StartPageActivity
     */
    private fun redirectToHome() {
        val intent = Intent(this@StartPageActivity, DiscActivity::class.java)
        startActivity(intent)
        finish() // Finish the current activity to prevent going back to login screen

    }
}
