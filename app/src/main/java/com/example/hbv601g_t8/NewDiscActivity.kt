package com.example.hbv601g_t8

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import java.io.File

class NewDiscActivity : AppCompatActivity() {

    private lateinit var buttonPhoto: Button
    private lateinit var btnChoose: Button
    private lateinit var viewImage: ImageView
    private lateinit var filePhoto: File
    private lateinit var stateSpinner: Spinner
    private lateinit var typeSpinner: Spinner
    private lateinit var submitNewDiscButton: Button
    private lateinit var selectedType: String
    private lateinit var selectedState: String
    private lateinit var titleText: EditText
    private lateinit var colorText: EditText
    private lateinit var priceText: EditText
    private lateinit var descriptionText: EditText
    private lateinit var quantityText: EditText
    private val FILE_NAME = "photo.jpg"
    private val IMAGE_CHOOSE = 1000
    private val PERMISSION_CODE = 1001
    private val REQUEST_CODE = 7

    /**
     * Called when the activity is first created.
     * Initializes the activity layout and UI elements.
     * Sets click listeners for the buttons.
     * Requests permissions if necessary.
     *
     * @param savedInstanceState The saved instance state Bundle, if any.
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.newdisc)

        buttonPhoto = findViewById(R.id.buttonPhoto)
        btnChoose = findViewById(R.id.btnChoose)
        viewImage = findViewById(R.id.viewImage)
        submitNewDiscButton = findViewById(R.id.submitNewDiscButton)
        titleText = findViewById(R.id.newDiscTitle)
        colorText = findViewById(R.id.newDiscColor)
        priceText = findViewById(R.id.newDiscPrice)
        descriptionText = findViewById(R.id.newDiscDescription)
        quantityText = findViewById(R.id.newDiscQuantity)

        buttonPhoto.setOnClickListener {
            val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            filePhoto = getPhotoFile(FILE_NAME)
            val providerFile = FileProvider.getUriForFile(this, "com.example.androidcamera.fileprovider", filePhoto)
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, providerFile)
            startActivityForResult(takePhotoIntent, REQUEST_CODE)
        }

        btnChoose.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                ) {
                    val permissions = arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    requestPermissions(permissions, PERMISSION_CODE)
                } else {
                    chooseImageGallery()
                }
            } else {
                chooseImageGallery()
            }
        }

        // Populate the state spinner
        val states = arrayOf("New", "Used")
        val stateAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, states)
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        //stateSpinner.adapter = stateAdapter

        val stateSpinner: Spinner = findViewById(R.id.stateSpinner)
        stateSpinner.adapter = stateAdapter

        // Set selected state when an item is selected
        stateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedState = states[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // do nothing
            }
        }

        // Populate the state spinner
        val discTypes = arrayOf("Putter", "Mid-Range", "Fairway Driver", "Distance Driver")
        val typeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, discTypes)
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        //stateSpinner.adapter = stateAdapter

        val typeSpinner: Spinner = findViewById(R.id.typeSpinner)
        typeSpinner.adapter = typeAdapter

        // Set selected state when an item is selected
        typeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedType = discTypes[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        suspend fun insertProductIntoSupabase(newDiscCreation: NewDiscCreation) {
            withContext(Dispatchers.IO) {
                SupabaseManager.supabase.from("discs").insert(newDiscCreation)
            }
        }

        submitNewDiscButton.setOnClickListener {
            val description = descriptionText.text.toString()
            val title = titleText.text.toString()
            val color = colorText.text.toString()
            val price = priceText.text.toString().toInt()
            val quantity = quantityText.text.toString().toInt()
            val state = selectedState
            val type = selectedType

            val newDisc = NewDiscCreation(
                price,
                state,
                description,
                title,
                type,
                color,
                "0"
            )

            runBlocking {
                insertProductIntoSupabase(newDisc)
            }

            Toast.makeText(this, "Disc successfully added", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Creates a temporary file to store the photo.
     *
     * @param fileName The name of the photo file.
     * @return The temporary file for the photo.
     */
    private fun getPhotoFile(fileName: String): File {
        val dirStorage = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", dirStorage)
    }


    /**
     * Called when an activity that was started with startActivityForResult completes.
     * Handles the result of the camera activity and the image gallery activity.
     *
     * @param requestCode The request code passed to startActivityForResult().
     * @param resultCode The result code returned by the child activity.
     * @param data An Intent with the result data.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_CHOOSE && resultCode == Activity.RESULT_OK) {
            val selectedImageUri = data?.data
            viewImage.setImageURI(selectedImageUri)
        } else if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val capPhoto = BitmapFactory.decodeFile(filePhoto.absolutePath)
            viewImage.setImageBitmap(capPhoto)
        }
    }


    /**
     * Opens the image gallery to choose an image.
     */
    private fun chooseImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_CHOOSE)
    }




    /**
     * Called when the user responds to a permission request dialog.
     * Checks if the requested permissions are granted.
     * If permissions are granted, opens the image gallery.
     * If permissions are denied, displays a toast message.
     *
     * @param requestCode The request code passed to requestPermissions().
     * @param permissions The requested permissions.
     * @param grantResults The grant results for the corresponding permissions.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    chooseImageGallery()
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}
