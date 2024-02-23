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

class LoginActivity :AppCompatActivity(){

    // Constants for SharedPreferences
    private val PREFS_NAME = "MyPrefs"
    private val KEY_IS_LOGGED_IN = "isLoggedIn"


    private lateinit var username : EditText
    private lateinit var password : EditText
    private lateinit var loginButton : Button
    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        loginButton = findViewById(R.id.loginButton)

        if (isLoggedIn()) {
            redirectToHome()
        }


        loginButton.setOnClickListener(View.OnClickListener {
            if(username.text.toString() == "user" && password.text.toString() == "1234"){
                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
                setLoggedInState(true)
                redirectToHome()
            }else {
                Toast.makeText(this,"Login Failed!", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * Sets login state in SharedPreferences
     */
    private fun setLoggedInState(isLoggedIn: Boolean) {
        val editor = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
        editor.apply()
    }


    /***
     * checks login state from SharedPreferences
     */
    fun isLoggedIn(): Boolean {
        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }


    /**
     * Redirects to the DiscActivity and finishes StartPageActivity
     */
    private fun redirectToHome() {
        val intent = Intent(this@LoginActivity, DiscActivity::class.java)
        startActivity(intent)
        finish() // Finish the current activity to prevent going back to login screen

    }
}