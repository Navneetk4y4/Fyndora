package com.example.lostandfound

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    private lateinit var itemsFoundCountTextView: TextView
    private lateinit var itemsClaimedCountTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val profileImage: CircleImageView = findViewById(R.id.profile_image)
        val nameTextView: TextView = findViewById(R.id.tv_profile_name)
        val emailTextView: TextView = findViewById(R.id.tv_profile_email)
        val editProfileButton: Button = findViewById(R.id.btn_edit_profile)

        itemsFoundCountTextView = findViewById(R.id.tv_items_found_count)
        itemsClaimedCountTextView = findViewById(R.id.tv_items_claimed_count)

        val currentUser = auth.currentUser

        if (currentUser != null) {
            emailTextView.text = currentUser.email ?: "N/A"

            val userRef = database.getReference("users").child(currentUser.uid)
            userRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val name = snapshot.child("name").getValue(String::class.java)
                    nameTextView.text = name ?: "N/A"

                    val imageName = snapshot.child("profileImage").getValue(String::class.java)
                    imageName?.let {
                        val resId = resources.getIdentifier(it, "drawable", packageName)
                        if (resId != 0) { // Safeguard
                            profileImage.setImageResource(resId)
                        } else {
                            profileImage.setImageResource(R.drawable.ic_profile_avatar) // Default
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    nameTextView.text = "Error loading name"
                }
            })

            fetchDashboardStats(currentUser.uid)
        } else {
            finish()
        }

        editProfileButton.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }
    }

    private fun fetchDashboardStats(userId: String) {
        val foundItemsRef = database.getReference("FoundItems")

        // Fetch Items Found Count
        foundItemsRef.orderByChild("userId").equalTo(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val itemsFoundCount = snapshot.childrenCount
                itemsFoundCountTextView.text = itemsFoundCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                itemsFoundCountTextView.text = "0"
            }
        })

        // Fetch Items Claimed Count
        foundItemsRef.orderByChild("claimedBy").equalTo(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val itemsClaimedCount = snapshot.childrenCount
                itemsClaimedCountTextView.text = itemsClaimedCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                itemsClaimedCountTextView.text = "0"
            }
        })
    }
}
