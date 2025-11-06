package com.example.lostandfound

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.database.FirebaseDatabase

class ReportFraudActivity : AppCompatActivity() {

    private lateinit var editTextName: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var buttonSubmit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_fraud)

        val topAppBar: MaterialToolbar = findViewById(R.id.topAppBar)
        topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        editTextName = findViewById(R.id.editTextName)
        editTextEmail = findViewById(R.id.editTextEmail)
        buttonSubmit = findViewById(R.id.buttonSubmit)

        buttonSubmit.setOnClickListener {
            submitReport()
        }
    }

    private fun submitReport() {
        val name = editTextName.text.toString().trim()
        val email = editTextEmail.text.toString().trim()

        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val database = FirebaseDatabase.getInstance()
        val reportsRef = database.getReference("reports")

        val reportId = reportsRef.push().key
        val report = Report(name, email)

        if (reportId != null) {
            reportsRef.child(reportId).setValue(report)
                .addOnSuccessListener {
                    Toast.makeText(this, "Report submitted successfully", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to submit report", Toast.LENGTH_SHORT).show()
                }
        }
    }

    data class Report(val name: String, val email: String)
}