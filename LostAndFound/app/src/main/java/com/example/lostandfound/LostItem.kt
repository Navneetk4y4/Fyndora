package com.example.lostandfound

// This data class is used to model the items retrieved from Firebase.
// The field names here MUST match the keys in your Firebase database.
data class LostItem(
    val name: String? = null,
    val location: String? = null,
    val description: String? = null,
    val imageUrl: String? = null,
    val perName: String? = null,
    val perRegNo: String? = null,
    val perDetails: String? = null,
    val userId: String? = null // Added to track the user who uploaded the item
)
