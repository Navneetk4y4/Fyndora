package com.example.studypomodoro

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment

class ProfileFragment : Fragment() {

    private lateinit var userNameEditText: EditText
    private lateinit var saveButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        userNameEditText = view.findViewById(R.id.et_user_name)
        saveButton = view.findViewById(R.id.btn_save_profile)

        loadUserName()

        saveButton.setOnClickListener {
            saveUserName()
        }

        return view
    }

    private fun loadUserName() {
        val sharedPreferences = requireActivity().getSharedPreferences("profile", Context.MODE_PRIVATE)
        val userName = sharedPreferences.getString("USER_NAME", "David King")
        userNameEditText.setText(userName)
    }

    private fun saveUserName() {
        val userName = userNameEditText.text.toString()
        if (userName.isNotEmpty()) {
            val sharedPreferences = requireActivity().getSharedPreferences("profile", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("USER_NAME", userName)
            editor.apply()
            Toast.makeText(requireContext(), "Profile saved", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "Please enter your name", Toast.LENGTH_SHORT).show()
        }
    }
}