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

    private val knowledgeBase = """
    Q: What is the purpose of the Lost & Found app?
    A: The app is a community-based platform designed to help users find their lost items and report items they have found. It facilitates the process of reuniting people with their belongings.

    Q: I lost my wallet, what do I do? OR How can I report something I lost?
    A: To report a lost item, go to the main dashboard and tap on the "Report a Lost Item" card. This will open a form where you can describe the item, specify where you lost it, and upload a photo to help others identify it.

    Q: I found a phone, how can I report it? OR What if I find something?
    A: If you find an item, you should report it by tapping the "Report a Found Item" card on the dashboard. Fill in the details about the item and upload a picture. The owner can then find it and contact you.

    Q: How do I see the items that I've posted?
    A: Your profile screen shows your activity. You can access it by tapping the settings icon on the dashboard and then selecting 'Profile'. It displays counts of items you've found and items you've claimed.

    Q: I see an item that looks like mine. How do I claim it?
    A: When you are viewing the details of a found item you believe is yours, tap the "Claim" button. After you agree to the anti-fraud agreement, the app will show you the contact details of the person who found it. It is then your responsibility to arrange the pickup.

    Q: What does the fraud agreement mean when I claim an item?
    A: It's a checkbox you must tick to confirm you are the legitimate owner of the item. This is a deterrent against fraudulent claims and helps maintain trust in the community. Making a false claim is against the app's policy.

    Q: How do I search for a specific item?
    A: On the "Lost Items" or "Found Items" screens, there is a search bar at the top. You can type keywords like 'black wallet' or 'keys' to filter the list and find what you're looking for more easily.

    Q: What happens when I report someone for fraud? OR How can I report a malicious user?
    A: When you use the 'Report Fraud' button on the item details page, our moderation team is notified. We will investigate the report to determine if a user has violated our policies. This is the correct way to report a malicious user or suspicious activity and helps protect the community.

    Q: Can I change my profile picture or name?
    A: Yes, absolutely. Navigate to your profile screen and tap the "Edit Profile" button. You can update your name and select a new profile avatar there.

    Q: Is my personal information safe?
    A: We take your privacy seriously. Your contact information is only shared with another user when you explicitly agree to claim an item. It is not publicly visible on your profile or on the items you post.
    """

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

        if (messages.isEmpty()) {
            messages.add(Pair("Hello! How can I help you with the Lost & Found app?", false))
            adapter.notifyItemInserted(messages.size - 1)
        }

        sendButton.setOnClickListener {
            val userMessage = inputMessage.text.toString()
            if (userMessage.isNotEmpty()) {
                messages.add(Pair(userMessage, true))
                adapter.notifyItemInserted(messages.size - 1)
                inputMessage.text.clear()

                val prompt = """
                You are a helpful assistant for the Lost & Found app.
                Your role is to answer user questions based *only* on the information provided in the following knowledge base.
                Do not make up answers. If the question cannot be answered by the knowledge base, say "I'm sorry, I don't have information about that. You can try asking another question about how to use the app."

                --- KNOWLEDGE BASE ---
                $knowledgeBase
                --------------------

                Based on the knowledge base, please answer the following user question:
                User Question: "$userMessage"
                """

                val generativeModel = GenerativeModel(
                    modelName = "gemini-2.0-flash",
                    apiKey = "AIzaSyCQr3_2nhQbq1ahxqHLpLgBpqgc328U5lw",
                    generationConfig = generationConfig {
                        temperature = 0.7f
                    }
                )

                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        val response = generativeModel.generateContent(prompt).text
                        messages.add(Pair(response ?: "Sorry, I couldn't process that.", false))
                        adapter.notifyItemInserted(messages.size - 1)
                    } catch (e: Exception) {
                        messages.add(Pair("Error: Unable to get a response.", false))
                        adapter.notifyItemInserted(messages.size - 1)
                    }
                }
            }
        }
    }
}
