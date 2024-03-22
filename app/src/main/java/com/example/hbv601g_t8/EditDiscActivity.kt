package com.example.hbv601g_t8

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.contextaware.withContextAvailable
import androidx.appcompat.app.AppCompatActivity
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class EditDiscActivity : AppCompatActivity() {

    private var discId : Int = 0
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
            discId = bundle.getInt("discId")
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
                currentDisc.discid,
                updatedDiscCondition,
                updatedDiscDescription,
                updatedDiscTitle,
                updatedDiscPrice,
                updatedDiscType,
                currentDisc.user_id,
                updatedDiscColor
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
        withContext(Dispatchers.IO) {
            currentDisc = SupabaseManager.supabase.from("discs").select {
                filter {
                    eq("discid", discId)
                }
            }.decodeSingle()
        }
    }

    private suspend fun updateCurrentDiscToDatabase () {
        withContext(Dispatchers.IO) {
            SupabaseManager.supabase.from("discs").update (updatedDisc) {
                filter {
                    eq("discid", discId)
                }
            }
        }
        println(updatedDisc)
    }

}