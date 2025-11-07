package com.example.lostandfound

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.ai.client.generativeai.GenerativeModel
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.launch

class ChatbotActivity : AppCompatActivity() {

    private val messages = mutableListOf<Pair<String, Boolean>>()
    private lateinit var adapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_chatbot)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewChat)
        val inputMessage: EditText = findViewById(R.id.editTextMessage)
        val sendButton: ImageButton = findViewById(R.id.buttonSend)
        val toolbar: MaterialToolbar = findViewById(R.id.toolbarChat)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        adapter = ChatAdapter(messages)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Add a welcome message
        messages.add(Pair("Hello! How can I help you today?", false))
        adapter.notifyItemInserted(messages.size - 1)

        sendButton.setOnClickListener {
            val message = inputMessage.text.toString()
            if (message.isNotEmpty()) {
                messages.add(Pair(message, true))
                adapter.notifyItemInserted(messages.size - 1)
                inputMessage.text.clear()

                // You will need to add your API key here
                val generativeModel = GenerativeModel("gemini-pro", "YOUR_API_KEY")
                lifecycleScope.launch {
                    val response = generativeModel.generateContent(message).text
                    messages.add(Pair(response ?: "", false))
                    adapter.notifyItemInserted(messages.size - 1)
                }
            }
        }
    }
}