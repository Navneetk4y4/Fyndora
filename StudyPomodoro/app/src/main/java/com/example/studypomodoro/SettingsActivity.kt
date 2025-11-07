package com.example.studypomodoro

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class SettingsActivity : AppCompatActivity() {

    private lateinit var focusDurationEditText: EditText
    private lateinit var breakDurationEditText: EditText
    private lateinit var longBreakDurationEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        focusDurationEditText = findViewById(R.id.et_focus_duration)
        breakDurationEditText = findViewById(R.id.et_break_duration)
        longBreakDurationEditText = findViewById(R.id.et_long_break_duration)
        saveButton = findViewById(R.id.btn_save_settings)
        toolbar = findViewById(R.id.toolbar_settings)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        loadSettings()

        saveButton.setOnClickListener {
            saveSettings()
        }
    }

    private fun loadSettings() {
        val sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
        val focusDuration = sharedPreferences.getInt("focus_duration", 25)
        val breakDuration = sharedPreferences.getInt("break_duration", 5)
        val longBreakDuration = sharedPreferences.getInt("long_break_duration", 15)

        focusDurationEditText.setText(focusDuration.toString())
        breakDurationEditText.setText(breakDuration.toString())
        longBreakDurationEditText.setText(longBreakDuration.toString())
    }

    private fun saveSettings() {
        val focusDuration = focusDurationEditText.text.toString().toIntOrNull()
        val breakDuration = breakDurationEditText.text.toString().toIntOrNull()
        val longBreakDuration = longBreakDurationEditText.text.toString().toIntOrNull()

        if (focusDuration != null && breakDuration != null && longBreakDuration != null) {
            val sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putInt("focus_duration", focusDuration)
            editor.putInt("break_duration", breakDuration)
            editor.putInt("long_break_duration", longBreakDuration)
            editor.apply()
            Toast.makeText(this, "Settings saved", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Invalid input", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
