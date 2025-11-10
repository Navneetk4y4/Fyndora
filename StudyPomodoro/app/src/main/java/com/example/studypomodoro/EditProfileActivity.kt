package com.example.studypomodoro

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class EditProfileActivity : AppCompatActivity() {

    private lateinit var userNameEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        userNameEditText = findViewById(R.id.et_user_name)
        saveButton = findViewById(R.id.btn_save_profile)
        toolbar = findViewById(R.id.toolbar_edit_profile)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        loadUserName()

        saveButton.setOnClickListener {
            saveUserName()
        }
    }

    private fun loadUserName() {
        val sharedPreferences = getSharedPreferences("profile", Context.MODE_PRIVATE)
        val userName = sharedPreferences.getString("USER_NAME", "David King")
        userNameEditText.setText(userName)
    }

    private fun saveUserName() {
        val userName = userNameEditText.text.toString()
        if (userName.isNotEmpty()) {
            val sharedPreferences = getSharedPreferences("profile", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("USER_NAME", userName)
            editor.apply()
            Toast.makeText(this, "Profile saved", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
