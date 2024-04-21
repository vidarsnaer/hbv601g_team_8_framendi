package com.example.hbv601g_t8

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegisterActivity :AppCompatActivity(){

    private lateinit var newUsername : EditText
    private lateinit var newEmail : EditText
    private lateinit var newPassword : EditText
    private lateinit var registerButton : Button
    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)

        newUsername = findViewById(R.id.username)
        newEmail = findViewById(R.id.email)
        newPassword = findViewById(R.id.password)
        registerButton = findViewById(R.id.registerButton)

        registerButton.setOnClickListener {

            val newUserUsername = newUsername.text.toString()
            val newUserEmail = newEmail.text.toString()
            val newUserPassword = newPassword.text.toString()
/*
            try {
                runBlocking {
                    withContext(Dispatchers.IO) {
                        SupabaseManager.supabase.auth.signUpWith(Email) {
                            email = newUserEmail
                            password = newUserPassword
                        }
                        SupabaseManager.supabase.auth.modifyUser {
                            data {
                                put("name", newUserUsername)
                            }
                        }
                    }
                }
                Toast.makeText(this, "Account Created Successfully!", Toast.LENGTH_SHORT).show()
                saveUser(applicationContext)
                redirectToLogin()
            } catch (e: Exception) {
                Toast.makeText(this,"Account Creation Failed!", Toast.LENGTH_SHORT).show()
                // For example, display an error message or log the exception
                println("Error occurred during sign-in: ${e.message}")
            }
 */
            registerUser(newUserUsername, newUserEmail, newUserPassword)
        }
    }

    private fun registerUser(username: String, email: String, password: String) {
        val user = User(name = username, email = email, password = password)

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val registeredUser = UserService().signupUser(user)
                if (registeredUser != null) {
                    Toast.makeText(applicationContext, "Registration Successful! User ID: ${registeredUser.id}", Toast.LENGTH_SHORT).show()
                    saveUser(registeredUser.id!!) // Save the user ID locally
                    redirectToLogin()
                } else {
                    Toast.makeText(applicationContext, "Registration Failed!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(applicationContext, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun saveUser(userId: Long) {
        val sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putLong(GlobalVariables.USER_ID, userId)
        editor.apply()
    }

    /*
    /**
     * Save user to sharepoint, which will later be a database
     */
    private fun saveUser(context: Context) {
        val user = SupabaseManager.supabase.auth.currentUserOrNull()
        val userid = user?.id
        val sharedPreferences = context.getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString(GlobalVariables.USER_ID.toString(), userid)
        editor.apply()
    }
    */


    /**
     * Opens the DiscActivity and finishes the RegisterActivity
     * @property newUser
     */
    private fun redirectToLogin() {
        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}