package com.example.lostandfound

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

class Found : AppCompatActivity() {

    private lateinit var imagePreview: ImageView
    private lateinit var itemName: EditText
    private lateinit var itemLocation: EditText
    private lateinit var itemDesc: EditText
    private lateinit var perName: EditText
    private lateinit var perRegNo: EditText
    private lateinit var perDetails: EditText
    private lateinit var uploadButton: Button
    private lateinit var progressBar: ProgressBar

    private var imageUri: Uri? = null

    private val client = OkHttpClient()
    private val imgbbApiKey = "8fd966b7b9fd83c3e0d1765e3f956a6e"
    private lateinit var auth: FirebaseAuth

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageUri = it
            imagePreview.setImageURI(imageUri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_found)

        auth = FirebaseAuth.getInstance()

        imagePreview = findViewById(R.id.imagePreview)
        itemName = findViewById(R.id.itemName)
        itemLocation = findViewById(R.id.itemLocation)
        itemDesc = findViewById(R.id.itemDesc)
        perName = findViewById(R.id.PerName)
        perRegNo = findViewById(R.id.PerRegNo)
        perDetails = findViewById(R.id.PerDetails)
        uploadButton = findViewById(R.id.btnUpload)
        progressBar = findViewById(R.id.progressBar)

        findViewById<Button>(R.id.btnSelectImage).setOnClickListener {
            selectImageLauncher.launch("image/*")
        }

        uploadButton.setOnClickListener {
            uploadData()
        }
    }

    private fun uploadData() {
        val name = itemName.text.toString().trim()
        val loc = itemLocation.text.toString().trim()
        val desc = itemDesc.text.toString().trim()
        val pName = perName.text.toString().trim()
        val pRegNo = perRegNo.text.toString().trim()
        val pDetails = perDetails.text.toString().trim()
        val userId = auth.currentUser?.uid

        if (name.isEmpty() || loc.isEmpty() || desc.isEmpty() || pName.isEmpty() || pRegNo.isEmpty() || pDetails.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show()
            return
        }

        if (userId == null) {
            Toast.makeText(this, "You must be logged in to upload an item.", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            setLoadingState(true)
            try {
                val file = getFileFromUri(imageUri!!)
                val mimeType = contentResolver.getType(imageUri!!)

                val reqBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("key", imgbbApiKey)
                    .addFormDataPart("image", file.name, file.asRequestBody(mimeType?.toMediaTypeOrNull()))
                    .build()

                val request = Request.Builder()
                    .url("https://api.imgbb.com/1/upload")
                    .post(reqBody)
                    .build()

                val imageUrl = withContext(Dispatchers.IO) {
                    val response = client.newCall(request).execute()
                    if (!response.isSuccessful) throw Exception("ImgBB Upload failed: ${response.message}")
                    val json = JSONObject(response.body!!.string())
                    if (!json.getBoolean("success")) throw Exception("ImgBB error: ${json.getJSONObject("error").getString("message")}")
                    json.getJSONObject("data").getString("url")
                }

                val itemMap = mapOf(
                    "name" to name,
                    "location" to loc,
                    "description" to desc,
                    "imageUrl" to imageUrl,
                    "perName" to pName,
                    "perRegNo" to pRegNo,
                    "perDetails" to pDetails,
                    "userId" to userId
                )

                FirebaseDatabase.getInstance().getReference("FoundItems")
                    .push().setValue(itemMap)
                    .addOnSuccessListener {
                        Toast.makeText(this@Found, "Item data saved to Firebase!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this@Found, "Firebase error: ${e.message}", Toast.LENGTH_LONG).show()
                    }

            } catch (e: Exception) {
                Toast.makeText(this@Found, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                setLoadingState(false)
            }
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        uploadButton.isEnabled = !isLoading
    }

    private fun getFileFromUri(uri: Uri): File {
        val destinationFilename = File(cacheDir, "temp_image_${System.currentTimeMillis()}")
        contentResolver.openInputStream(uri).use { ins ->
            FileOutputStream(destinationFilename).use { ous ->
                ins?.copyTo(ous)
            }
        }
        return destinationFilename
    }
}
