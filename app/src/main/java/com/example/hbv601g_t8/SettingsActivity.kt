package com.example.hbv601g_t8

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity :AppCompatActivity(){

    private lateinit var changeUsername : Button
    private lateinit var changeEmail : Button
    private lateinit var changePassword : Button
    private val USER_ID = "userid"
    private lateinit var loggedInUser : User

    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.settings)

        val prefs = getSharedPreferences(USER_ID, Context.MODE_PRIVATE)
        val userId = prefs.getInt(USER_ID, 0)

        // TODO: Get user from db with this id, will use dummy data now
        loggedInUser = User(1, "user", "user@user.is", "123")

        val username = findViewById<TextView>(R.id.username)
        username.text = loggedInUser.name

        val email = findViewById<TextView>(R.id.email)
        email.text = loggedInUser.email

        changeUsername = findViewById(R.id.change_username)
        changeUsername.paintFlags = changeUsername.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        changeUsername.setOnClickListener{
            showUsernameDialog()
        }

        changeEmail = findViewById(R.id.change_email)
        changeEmail.paintFlags = changeEmail.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        changePassword = findViewById(R.id.change_password)
        changePassword.paintFlags = changePassword.paintFlags or Paint.UNDERLINE_TEXT_FLAG
    }

    private fun startDialog(dialog: Dialog){
        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)
    }

    private fun showUsernameDialog(){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.editname_dialog)

        val saveUsername: Button = dialog.findViewById(R.id.save_username)
        saveUsername.setOnClickListener( View.OnClickListener {
            val usernameEditText = dialog.findViewById<EditText>(R.id.new_username)
            val newName = usernameEditText.text.toString()
            if (newName == ""){
                Toast.makeText(this, "Please give a username", Toast.LENGTH_SHORT).show()
            }
            else{
                // TODO: update username in db
                loggedInUser.name = newName

                val username = findViewById<TextView>(R.id.username)
                username.text = newName

                dialog.dismiss()
                Toast.makeText(this, "Username updated", Toast.LENGTH_SHORT).show()
            }

        })
        startDialog(dialog)
    }
}