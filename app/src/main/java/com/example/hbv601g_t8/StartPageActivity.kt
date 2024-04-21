package com.example.hbv601g_t8


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class StartPageActivity:AppCompatActivity(){

    private lateinit var loginButton : Button
    private lateinit var registerButton : Button

    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)


        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()


        if (isLoggedIn()) {
            redirectToHome()
        }
        else{
            setContentView(R.layout.start_screen)
            loginButton = findViewById(R.id.loginButton)
            registerButton = findViewById(R.id.registerButton)

            loginButton.setOnClickListener{
                val intent = Intent(this@StartPageActivity, LoginActivity::class.java)
                startActivity(intent)
            }

            registerButton.setOnClickListener {
                val intent = Intent(this@StartPageActivity, RegisterActivity::class.java)
                startActivity(intent)

            }
        }

    }

    /***
     * checks login state from SharedPreferences
     */
    private fun isLoggedIn(): Boolean {
        val prefs = getSharedPreferences(GlobalVariables.PREFS_NAME, Context.MODE_PRIVATE)
        val loggedIn = prefs.getBoolean(GlobalVariables.KEY_IS_LOGGED_IN, false)
        print("Is logged in: $loggedIn")
        return loggedIn
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
