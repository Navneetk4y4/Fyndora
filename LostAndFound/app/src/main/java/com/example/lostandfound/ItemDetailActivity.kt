package com.example.lostandfound

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ItemDetailActivity : AppCompatActivity() {

    private var perName: String? = null
    private var perRegNo: String? = null
    private var perDetails: String? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_detail)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val itemId = intent.getStringExtra("ITEM_ID")
        val itemName = intent.getStringExtra("ITEM_NAME")
        val itemDescription = intent.getStringExtra("ITEM_DESCRIPTION")
        val itemImageUrl = intent.getStringExtra("ITEM_IMAGE_URL")
        val itemLocation = intent.getStringExtra("ITEM_LOCATION")
        // Retrieve uploader's details from the intent
        perName = intent.getStringExtra("PER_NAME")
        perRegNo = intent.getStringExtra("PER_REG_NO")
        perDetails = intent.getStringExtra("PER_DETAILS")

        val imageView: ImageView = findViewById(R.id.item_image_detail)
        val nameTextView: TextView = findViewById(R.id.item_name_detail)
        val descriptionTextView: TextView = findViewById(R.id.item_description_detail)
        val locationTextView: TextView = findViewById(R.id.item_location_detail)
        val fabClaim: ExtendedFloatingActionButton = findViewById(R.id.fab_claim)
        val collapsingToolbar: CollapsingToolbarLayout = findViewById(R.id.collapsing_toolbar)

        collapsingToolbar.title = itemName
        nameTextView.text = itemName
        descriptionTextView.text = itemDescription
        locationTextView.text = itemLocation

        if (itemImageUrl != null) {
            Glide.with(this).load(itemImageUrl).into(imageView)
        } else {
            imageView.setImageResource(R.drawable.ic_baseline_image_24)
        }

        fabClaim.setOnClickListener {
            showClaimConfirmationDialog(itemId)
        }
    }

    private fun showClaimConfirmationDialog(itemId: String?) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_claim_confirmation, null)
        val checkBox: CheckBox = dialogView.findViewById(R.id.checkbox_agree)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Confirm Claim")
            .setPositiveButton("OK", null) // Set to null initially
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnShowListener {
            val okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            okButton.isEnabled = false // Disable OK button initially

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                okButton.isEnabled = isChecked
            }

            okButton.setOnClickListener {
                dialog.dismiss()
                showContactDetailsDialog(itemId)
            }
        }

        dialog.show()
    }

    private fun showContactDetailsDialog(itemId: String?) {
        val currentUser = auth.currentUser
        if (currentUser != null && itemId != null) {
            val foundItemsRef = database.getReference("FoundItems").child(itemId)
            foundItemsRef.child("claimedBy").setValue(currentUser.uid)
        }

        val contactInfo = "Name: $perName\nRegistration No: $perRegNo\nContact: $perDetails"

        AlertDialog.Builder(this)
            .setTitle("Uploader Contact Information")
            .setMessage(contactInfo)
            .setPositiveButton("OK", null)
            .setCancelable(false) // Make the dialog non-cancelable
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
