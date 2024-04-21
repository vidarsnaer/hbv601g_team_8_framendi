package com.example.hbv601g_t8

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var name: EditText
    private lateinit var password: EditText
    private lateinit var loginButton: Button

    private val userService = UserService()  // Initialize UserService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        name = findViewById(R.id.username)
        password = findViewById(R.id.password)
        loginButton = findViewById(R.id.loginButton)

        loginButton.setOnClickListener {
            performLogin()
        }
    }

    private fun performLogin() {
        val userName = name.text.toString().trim()
        val userPassword = password.text.toString().trim()
        Log.d("LoginActivity", "Username: $userName, Password: $userPassword")

        if (userName.isEmpty() || userPassword.isEmpty()) {
            Toast.makeText(this, "Email and password must not be empty.", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val result = userService.loginUser(userName = userName, userPassword = userPassword)
                Log.d("LoginActivity", "Eftir kall, result var $result")
                if (result != null) {
                    Toast.makeText(this@LoginActivity, "Login Successful!", Toast.LENGTH_SHORT).show()
                    setLoggedInState(true)
                    redirectToHome()
                } else {
                    Toast.makeText(this@LoginActivity, "Login Failed!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.d("LoginActivity", "Error logging in: $e")
                Toast.makeText(this@LoginActivity, "Login Failed: Exception occurred.", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun setLoggedInState(isLoggedIn: Boolean) {
        val editor = getSharedPreferences(GlobalVariables.PREFS_NAME, Context.MODE_PRIVATE).edit()
        editor.putBoolean(GlobalVariables.KEY_IS_LOGGED_IN, isLoggedIn)
        editor.apply()
    }

    private fun redirectToHome() {
        val intent = Intent(this, DiscActivity::class.java)
        startActivity(intent)
        finish()
    }
}
