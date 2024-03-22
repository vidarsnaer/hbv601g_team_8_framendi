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
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.user.UserInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.put
import javax.json.Json
import javax.json.JsonObject

class SettingsActivity :AppCompatActivity(){

    private lateinit var changeUsername : Button
    private lateinit var changeEmail : Button
    private lateinit var changePassword : Button
    private lateinit var deleteAccount : Button
    private lateinit var loggedInUser : User
    private lateinit var user : Auth

    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.settings)

        val prefs = getSharedPreferences(GlobalVariables.USER_ID, Context.MODE_PRIVATE)
        val userId = prefs.getInt(GlobalVariables.USER_ID, 0)

        runBlocking {
            withContext(Dispatchers.IO) {
                user = SupabaseManager.supabase.auth
            }
        }

        println(user.currentUserOrNull()?.id)

        val username = findViewById<TextView>(R.id.username)
        username.text = user.currentUserOrNull()?.userMetadata?.get("name").toString().removeSurrounding("\"")

        val email = findViewById<TextView>(R.id.email)
        email.text = user.currentUserOrNull()?.email

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
                runBlocking {
                    user.modifyUser {
                        data {
                            put("name", newName)
                        }
                    }
                }

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

        val email_Pattern = "^[A-Za-z](.*)(@)(.+)(\\.)(.+)".toRegex()

        val saveEmail: Button = dialog.findViewById(R.id.save_email)
        saveEmail.setOnClickListener( View.OnClickListener {
            val currentEmail = dialog.findViewById<EditText>(R.id.current_email)
            val newEmail = dialog.findViewById<EditText>(R.id.new_email)
            val currentEmailS = currentEmail.text.toString()
            val newEmailS = newEmail.text.toString()
            if (currentEmailS != user.currentUserOrNull()?.email){
                Toast.makeText(this, "Please give your current email", Toast.LENGTH_SHORT).show()
            }
            else if(!email_Pattern.matches(newEmailS)){
                Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
            }
            else{

                runBlocking {
                    user.modifyUser {
                        email = newEmailS
                    }
                }

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
            val newPassword = dialog.findViewById<EditText>(R.id.new_password1)
            val confirmPassword = dialog.findViewById<EditText>(R.id.new_password2)
            val newPasswordS = newPassword.text.toString()
            val confirmPasswordS = confirmPassword.text.toString()

            if( newPasswordS == "" || confirmPasswordS == ""){
                Toast.makeText(this, "Please enter your new password", Toast.LENGTH_SHORT).show()
            }
            else if ( newPasswordS.length < 6) {
                Toast.makeText(this, "Password should be at least 6 characters", Toast.LENGTH_SHORT).show()
            }
            else if( newPasswordS != confirmPasswordS){
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            }
            else{
                runBlocking {
                    user.modifyUser {
                        password = newPasswordS
                    }
                }

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