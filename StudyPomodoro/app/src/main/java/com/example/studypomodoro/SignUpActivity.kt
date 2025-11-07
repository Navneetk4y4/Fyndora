package com.example.studypomodoro

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val nameEditText: EditText = findViewById(R.id.et_name_signup)
        val emailEditText: EditText = findViewById(R.id.et_email_signup)
        val passwordEditText: EditText = findViewById(R.id.et_password_signup)
        val confirmPasswordEditText: EditText = findViewById(R.id.et_confirm_password)
        val signUpButton: Button = findViewById(R.id.btn_signup)
        val loginLink: TextView = findViewById(R.id.tv_login_link)

        signUpButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val userId = user!!.uid
                        val userRef = database.getReference("users").child(userId)

                        val userData = HashMap<String, Any>()
                        userData["name"] = name
                        userData["email"] = email

                        userRef.setValue(userData).addOnCompleteListener { dbTask ->
                            if (dbTask.isSuccessful) {
                                startActivity(Intent(this, MainActivity::class.java))
                                finishAffinity()
                            } else {
                                Toast.makeText(baseContext, "Database error: ${dbTask.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(baseContext, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        loginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
