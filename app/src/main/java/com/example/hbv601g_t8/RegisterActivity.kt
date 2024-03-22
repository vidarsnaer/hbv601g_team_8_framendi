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
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.put

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
                redirectToLogin()
            } catch (e: Exception) {
                Toast.makeText(this,"Account Creation Failed!", Toast.LENGTH_SHORT).show()
                // For example, display an error message or log the exception
                println("Error occurred during sign-in: ${e.message}")
            }
        }
    }

    /**
     * Save user to sharepoint, which will later be a database
     */
    private fun saveUser(user: User, context: Context) {
        val sharedPreferences = context.getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putInt(GlobalVariables.USER_ID, user.id)
        editor.apply()
    }

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