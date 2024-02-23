package com.example.hbv601g_t8

import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ViewUtils
import com.example.hbv601g_t8.databinding.ActivityMainBinding

class UserActivity :AppCompatActivity(){

    private lateinit var binding: ActivityMainBinding

    private lateinit var username : EditText
    private lateinit var password : EditText
    private lateinit var loginButton : Button
    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        loginButton = findViewById(R.id.loginButton)


        loginButton.setOnClickListener(View.OnClickListener {
            if(username.toString() == "user" && password.toString() == "1234"){
                Toast.makeText(this,"Login Successful!", Toast.LENGTH_SHORT).show()
            }else {
                Toast.makeText(this,"Login Failed!", Toast.LENGTH_SHORT).show()
            }
        })
    }
}