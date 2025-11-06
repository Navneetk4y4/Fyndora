package com.example.lostandfound

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EditProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    private lateinit var nameEditText: EditText
    private val emojiImageViews = mutableMapOf<String, ImageView>()

    // Store the initial data to check for changes
    private var initialName: String? = null
    private var initialEmojiName: String? = null
    private var selectedEmojiName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        nameEditText = findViewById(R.id.et_edit_name)
        val saveButton: Button = findViewById(R.id.btn_save_profile)

        // Map emoji names to their ImageViews for easy access
        emojiImageViews["ic_emoji_smile"] = findViewById(R.id.emoji_smile)
        emojiImageViews["ic_emoji_wink"] = findViewById(R.id.emoji_wink)
        emojiImageViews["ic_emoji_cool"] = findViewById(R.id.emoji_cool)
        emojiImageViews["ic_emoji_love"] = findViewById(R.id.emoji_love)
        emojiImageViews["ic_emoji_happy"] = findViewById(R.id.emoji_happy)

        loadInitialUserData()

        // Set click listeners for each emoji to update the selection
        for ((emojiName, imageView) in emojiImageViews) {
            imageView.setOnClickListener {
                selectedEmojiName = emojiName
                updateEmojiSelectionUI()
            }
        }

        saveButton.setOnClickListener {
            saveProfileData()
        }
    }

    private fun loadInitialUserData() {
        val currentUser = auth.currentUser ?: return
        val userRef = database.getReference("users").child(currentUser.uid)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                initialName = snapshot.child("name").getValue(String::class.java)
                nameEditText.setText(initialName)

                initialEmojiName = snapshot.child("profileImage").getValue(String::class.java)
                selectedEmojiName = initialEmojiName
                updateEmojiSelectionUI()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@EditProfileActivity, "Failed to load your data.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Visually indicates which emoji is currently selected
    private fun updateEmojiSelectionUI() {
        for ((emojiName, imageView) in emojiImageViews) {
            imageView.alpha = if (emojiName == selectedEmojiName) 1.0f else 0.5f
        }
    }

    private fun saveProfileData() {
        val newName = nameEditText.text.toString().trim()
        val currentUser = auth.currentUser

        if (currentUser == null) {
            Toast.makeText(this, "Error: Not logged in.", Toast.LENGTH_SHORT).show()
            return
        }

        if (newName.isEmpty()) {
            Toast.makeText(this, "Name cannot be empty.", Toast.LENGTH_SHORT).show()
            return
        }

        val userRef = database.getReference("users").child(currentUser.uid)
        val updates = mutableMapOf<String, Any?>()

        // Check if the name or emoji has actually changed
        val nameChanged = newName != initialName
        val emojiChanged = selectedEmojiName != initialEmojiName

        if (nameChanged) {
            updates["name"] = newName
        }
        if (emojiChanged) {
            updates["profileImage"] = selectedEmojiName
        }

        if (updates.isEmpty()) {
            Toast.makeText(this, "No changes to save.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        Toast.makeText(this, "Saving...", Toast.LENGTH_SHORT).show()
        userRef.updateChildren(updates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Profile Updated!", Toast.LENGTH_SHORT).show()
                finish() // This will now execute correctly
            } else {
                Toast.makeText(this, "Update Failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
