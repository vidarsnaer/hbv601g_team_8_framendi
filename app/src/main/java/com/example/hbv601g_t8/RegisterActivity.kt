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

class RegisterActivity :AppCompatActivity(){

    private lateinit var username : EditText
    private lateinit var email : EditText
    private lateinit var password : EditText
    private lateinit var registerButton : Button
    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)



        username = findViewById(R.id.username)
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        registerButton = findViewById(R.id.registerButton)


        registerButton.setOnClickListener {
            val enteredUsername = username.text.toString()
            val enteredEmail = email.text.toString()
            val enteredPassword = password.text.toString()

            // Simulate storing the registration data in SharedPreferences
            val newUser = User(enteredUsername, enteredEmail, enteredPassword)
            saveUser(newUser, applicationContext)

            Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show()
            redirectToHome(newUser)
            // Here you can navigate to another activity, or perform any other action upon successful registration
        }
    }

    /**
     * Save user to sharepoint, which will later be a database
     */
    fun saveUser(user: User, context: Context) {
        val sharedPreferences = context.getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("username", user.name)
        editor.putString("email", user.email)
        editor.putString("password", user.password)
        editor.apply()
    }

    /**
     * Opens the DiscActivity and finishes the RegisterActivity
     * @property newUser
     */
    private fun redirectToHome(newUser: User) {
        val intent = Intent(this@RegisterActivity, DiscActivity::class.java)
        intent.putExtra("username", newUser.name)
        startActivity(intent)
        finish()

    }
}