package com.example.hbv601g_t8

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hbv601g_t8.SupabaseManager.supabase
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.UUID


class LoginActivity :AppCompatActivity(){

    private lateinit var email : EditText
    private lateinit var password : EditText
    private lateinit var loginButton : Button
    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        loginButton = findViewById(R.id.loginButton)


        if (isLoggedIn()) {
            redirectToHome()
            println("if is logged in method called")
        }



        loginButton.setOnClickListener(View.OnClickListener {

            val userEmail = email.text.toString()
            val userPassword = password.text.toString()

            try {
                runBlocking {
                    withContext(Dispatchers.IO) {
                        supabase.auth.signInWith(Email) {
                            email = userEmail
                            password = userPassword
                        }
                    }
                }
                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
                setLoggedInState(true)
                redirectToHome()
            } catch (e: Exception) {
                Toast.makeText(this,"Login Failed!", Toast.LENGTH_SHORT).show()
                // For example, display an error message or log the exception
                println("Error occurred during sign-in: ${e.message}")
            }
        })
    }

    /**
     * Sets login state in SharedPreferences
     */
    private fun setLoggedInState(isLoggedIn: Boolean) {
        val editor = getSharedPreferences(GlobalVariables.PREFS_NAME, Context.MODE_PRIVATE).edit()
        editor.putBoolean(GlobalVariables.KEY_IS_LOGGED_IN, isLoggedIn)
        val user = supabase.auth.currentUserOrNull()
        val userid = user?.id
        val uuid = UUID.fromString(userid)
        GlobalVariables.USER_ID = uuid
        editor.apply()
    }


    /***
     * checks login state from SharedPreferences
     */
    fun isLoggedIn(): Boolean {
        val prefs = getSharedPreferences(GlobalVariables.PREFS_NAME, Context.MODE_PRIVATE)
        //return prefs.getBoolean(GlobalVariables.KEY_IS_LOGGED_IN, false)
        return false
    }


    /**
     * Redirects to the DiscActivity and finishes StartPageActivity
     */
    private fun redirectToHome() {
        val intent = Intent(this@LoginActivity, DiscActivity::class.java)
        startActivity(intent)
        println("redirectToHome method called")
        finish() // Finish the current activity to prevent going back to login screen

    }
}