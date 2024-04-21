package com.example.hbv601g_t8

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

class EditDiscActivity : AppCompatActivity() {

    private var discId : Long = 0
    private lateinit var updatedDiscTitle : String
    private lateinit var updatedDiscColor : String
    private var updatedDiscPrice : Int = 0
    private lateinit var updatedDiscDescription : String
    private lateinit var updatedDiscCondition : String
    private lateinit var updatedDiscType : String
    private lateinit var currentDisc : Disc
    private lateinit var updatedDisc : Disc

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.newdisc)

        val bundle = intent.extras
        if (bundle != null) {
            discId = bundle.getLong("discId")
        }

        println(discId)

        val updateButton = findViewById<Button>(R.id.submitNewDiscButton)
        updateButton.text = getString(R.string.update_info)

        runBlocking {
            selectCurrentDiscFromDatabase()
        }

        // Set text fields
        findViewById<EditText>(R.id.newDiscTitle).setText(currentDisc.name)
        findViewById<EditText>(R.id.newDiscColor).setText(currentDisc.colour)
        findViewById<EditText>(R.id.newDiscPrice).setText(currentDisc.price.toString())
        findViewById<EditText>(R.id.newDiscDescription).setText(currentDisc.description)

        // Set spinner selections
        val stateSpinner = findViewById<Spinner>(R.id.stateSpinner)
        val states = resources.getStringArray(R.array.disc_states)
        val predefinedState = currentDisc.condition
        val predefinedStateIndex = states.indexOf(predefinedState)
        stateSpinner.setSelection(predefinedStateIndex)

        val typeSpinner = findViewById<Spinner>(R.id.typeSpinner)
        val types = resources.getStringArray(R.array.disc_types)
        val predefinedType = currentDisc.type
        val predefinedTypeIndex = types.indexOf(predefinedType)
        typeSpinner.setSelection(predefinedTypeIndex)

        updateButton.setOnClickListener {

            updatedDiscType = typeSpinner.selectedItem.toString()
            updatedDiscCondition = stateSpinner.selectedItem.toString()
            updatedDiscTitle = findViewById<EditText>(R.id.newDiscTitle).text.toString()
            updatedDiscColor = findViewById<EditText>(R.id.newDiscColor).text.toString()
            updatedDiscPrice = findViewById<EditText>(R.id.newDiscPrice).text.toString().toInt()
            updatedDiscDescription = findViewById<EditText>(R.id.newDiscDescription).text.toString()

            updatedDisc = Disc(
                discId = currentDisc.discId,
                condition = updatedDiscCondition,
                description = updatedDiscDescription,
                name = updatedDiscTitle,
                price = updatedDiscPrice,
                type = updatedDiscType,
                userId = currentDisc.userId,
                colour = updatedDiscColor,
                latitude = Random.nextDouble(64.00, 66.00),
                longitude = Random.nextDouble(-22.00, -14.00)
            )

            Toast.makeText(this, "Disc Info Updated", Toast.LENGTH_SHORT).show()
            runBlocking {
                updateCurrentDiscToDatabase()
            }
            val intent = Intent(this@EditDiscActivity, MyDiscsActivity::class.java)
            startActivity(intent)
        }

    }

    private suspend fun selectCurrentDiscFromDatabase () {
        currentDisc = DiscService().getDisc(discId)!!
        /*
        withContext(Dispatchers.IO) {
            currentDisc = SupabaseManager.supabase.from("discs").select {
                filter {
                    eq("discid", discId)
                }
            }.decodeSingle()
        }
         */
    }

    private suspend fun updateCurrentDiscToDatabase () {
        DiscService().updateDisc(discId, updatedDisc)
        /*
        withContext(Dispatchers.IO) {
            SupabaseManager.supabase.from("discs").update (updatedDisc) {
                filter {
                    eq("discid", discId)
                }
            }
        }
         */
        println(updatedDisc)
    }

    private fun getCurrentUserId(): Long {
        val sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        return sharedPreferences.getLong(GlobalVariables.USER_ID, -1)  // Return -1 or another invalid value as default if not found
    }

}