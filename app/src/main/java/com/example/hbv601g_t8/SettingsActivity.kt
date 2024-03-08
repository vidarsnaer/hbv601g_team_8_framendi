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
    private lateinit var deleteAccount : Button
    private lateinit var loggedInUser : User

    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.settings)

        val prefs = getSharedPreferences(GlobalVariables.USER_ID, Context.MODE_PRIVATE)
        val userId = prefs.getInt(GlobalVariables.USER_ID, 0)

        // TODO: Get user from db with this id, will use dummy data now
        loggedInUser = User(userId, "user", "user@user.is", "123")

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
        changeEmail.setOnClickListener{
            showEmailDialog()
        }

        changePassword = findViewById(R.id.change_password)
        changePassword.paintFlags = changePassword.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        changePassword.setOnClickListener{
            showPasswordDialog()
        }

        deleteAccount = findViewById(R.id.delete_account)
        deleteAccount.setOnClickListener{
            showDeleteDialog()
        }
    }


    /**
     * opens the dialog which is sent through
     */
    private fun startDialog(dialog: Dialog){
        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)
    }

    /**
     * opens dialog to change username
     */

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

    /**
     * opens the dialog to change email
     */

    private fun showEmailDialog(){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.editemail_dialog)

        val saveEmail: Button = dialog.findViewById(R.id.save_email)
        saveEmail.setOnClickListener( View.OnClickListener {
            val currentEmail = dialog.findViewById<EditText>(R.id.current_email)
            val newEmail = dialog.findViewById<EditText>(R.id.new_email)
            val currentEmailS = currentEmail.text.toString()
            val newEmailS = newEmail.text.toString()
            if (currentEmailS == ""){
                Toast.makeText(this, "Please give your current email", Toast.LENGTH_SHORT).show()
            }
            else if( newEmailS == ""){
                Toast.makeText(this, "Please give your new email", Toast.LENGTH_SHORT).show()
            }
            else{
                // TODO: update email in db
                loggedInUser.name = newEmailS

                val email = findViewById<TextView>(R.id.email)
                email.text = newEmailS

                dialog.dismiss()
                Toast.makeText(this, "Email updated", Toast.LENGTH_SHORT).show()
            }
        })
        startDialog(dialog)
    }

    /**
     * opens the dialog to change your password
     */
    private fun showPasswordDialog(){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.editpassword_dialog)

        val savePassword: Button = dialog.findViewById(R.id.save_password)
        savePassword.setOnClickListener( View.OnClickListener {
            val currentPassword = dialog.findViewById<EditText>(R.id.current_password)
            val newPassword = dialog.findViewById<EditText>(R.id.new_password1)
            val confirmPassword = dialog.findViewById<EditText>(R.id.new_password2)
            val currentPasswordS = currentPassword.text.toString()
            val newPasswordS = newPassword.text.toString()
            val confirmPasswordS = confirmPassword.text.toString()

            if (currentPasswordS == ""){
                Toast.makeText(this, "Please give your current password", Toast.LENGTH_SHORT).show()
            }
            else if( newPasswordS == "" || confirmPasswordS == ""){
                Toast.makeText(this, "Please give your new password", Toast.LENGTH_SHORT).show()
            }
            else if( currentPasswordS != loggedInUser.password){
                Toast.makeText(this, "incorrect password", Toast.LENGTH_SHORT).show()
            }
            else if( newPasswordS != confirmPasswordS){
                Toast.makeText(this, "New password does not match", Toast.LENGTH_SHORT).show()
            }
            else{
                // TODO: update password in db

                dialog.dismiss()
                Toast.makeText(this, "Password updated", Toast.LENGTH_SHORT).show()
            }
        })
        startDialog(dialog)
    }

    /**
     * Opens the dialog to delete your account
     */
    private fun showDeleteDialog(){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.delete_account_dialog)

        val delete: Button = dialog.findViewById(R.id.delete)
        delete.setOnClickListener( View.OnClickListener {
            val password = dialog.findViewById<EditText>(R.id.delete_password)
            val passwordS = password.text.toString()

            if (passwordS != loggedInUser.password){
                Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show()
            }
            else{
                // TODO: delete account in db

                val editor = getSharedPreferences(GlobalVariables.PREFS_NAME, Context.MODE_PRIVATE).edit()
                editor.putBoolean(GlobalVariables.KEY_IS_LOGGED_IN, false)
                editor.apply()

                // go to the start page
                val intent = Intent(this@SettingsActivity, StartPageActivity::class.java)
                startActivity(intent)
                finish()
            }
        })
        startDialog(dialog)
    }
}