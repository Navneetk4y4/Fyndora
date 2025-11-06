package com.example.lostandfound

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.runBlocking

class ChatbotDialogFragment : DialogFragment() {

    private val messages = mutableListOf<Pair<String, Boolean>>()
    private lateinit var adapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_chatbot, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewChat)
        val inputMessage: EditText = view.findViewById(R.id.editTextMessage)
        val sendButton: Button = view.findViewById(R.id.buttonSend)

        adapter = ChatAdapter(messages)
        recyclerView.layoutManager = LinearLayoutManager(context)
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