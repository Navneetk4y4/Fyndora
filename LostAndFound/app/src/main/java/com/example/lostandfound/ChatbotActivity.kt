package com.example.lostandfound

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.runBlocking

class ChatbotActivity : AppCompatActivity() {

    private val messages = mutableListOf<Pair<String, Boolean>>()
    private lateinit var adapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatbot)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewChat)
        val inputMessage: EditText = findViewById(R.id.editTextMessage)
        val sendButton: Button = findViewById(R.id.buttonSend)

        adapter = ChatAdapter(messages)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        sendButton.setOnClickListener {
            val message = inputMessage.text.toString()
            if (message.isNotEmpty()) {
                messages.add(Pair(message, true))
                adapter.notifyItemInserted(messages.size - 1)
                inputMessage.text.clear()

                // You will need to add your API key here
                val generativeModel = GenerativeModel("gemini-pro", "YOUR_API_KEY")
                runBlocking {
                    val response = generativeModel.generateContent(message).text
                    messages.add(Pair(response ?: "", false))
                    adapter.notifyItemInserted(messages.size - 1)
                }
            }
        }
    }
}