package com.example.lostandfound

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.launch

class ChatbotDialogFragment : DialogFragment() {

    private val messages = mutableListOf<Pair<String, Boolean>>()
    private lateinit var adapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return inflater.inflate(R.layout.dialog_chatbot, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewChat)
        val inputMessage: EditText = view.findViewById(R.id.editTextMessage)
        val sendButton: ImageButton = view.findViewById(R.id.buttonSend)
        val toolbar: MaterialToolbar = view.findViewById(R.id.toolbarChat)

        toolbar.setNavigationOnClickListener { dismiss() }

        adapter = ChatAdapter(messages)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        // Add a welcome message
        if (messages.isEmpty()) {
            messages.add(Pair("Hello! How can I help you today?", false))
            adapter.notifyItemInserted(messages.size - 1)
        }

        sendButton.setOnClickListener {
            val message = inputMessage.text.toString()
            if (message.isNotEmpty()) {
                messages.add(Pair(message, true))
                adapter.notifyItemInserted(messages.size - 1)
                inputMessage.text.clear()

                val generativeModel = GenerativeModel(
                    modelName = "gemini-2.0-flash",
                    apiKey = "AIzaSyCQr3_2nhQbq1ahxqHLpLgBpqgc328U5lw",
                    generationConfig = generationConfig {
                        temperature = 0.9f
                    }
                )

                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        val response = generativeModel.generateContent(message).text
                        messages.add(Pair(response ?: "", false))
                        adapter.notifyItemInserted(messages.size - 1)
                    } catch (e: Exception) {
                        messages.add(Pair("Error: ${e.message}", false))
                        adapter.notifyItemInserted(messages.size - 1)
                    }
                }
            }
        }
    }
}
