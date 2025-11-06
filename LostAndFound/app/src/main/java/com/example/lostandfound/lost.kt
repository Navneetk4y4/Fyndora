package com.example.lostandfound

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Locale

class Lost : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchEditText: EditText
    private lateinit var fullLostList: MutableList<LostItem>
    private lateinit var filteredLostList: MutableList<LostItem>
    private lateinit var adapter: LostAdapter

    private val dbRef = FirebaseDatabase.getInstance().getReference("FoundItems")
    private var valueEventListener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lost)

        recyclerView = findViewById(R.id.recyclerViewLost)
        searchEditText = findViewById(R.id.search_edit_text)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        fullLostList = mutableListOf()
        filteredLostList = mutableListOf()
        adapter = LostAdapter(filteredLostList) { item ->
            val intent = Intent(this, ItemDetailActivity::class.java).apply {
                putExtra("ITEM_NAME", item.name)
                putExtra("ITEM_DESCRIPTION", item.description)
                putExtra("ITEM_IMAGE_URL", item.imageUrl)
                putExtra("ITEM_LOCATION", item.location)
                putExtra("PER_NAME", item.perName)
                putExtra("PER_REG_NO", item.perRegNo)
                putExtra("PER_DETAILS", item.perDetails)
            }
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        setupSearchListener()
    }

    private fun setupSearchListener() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                filter(s.toString())
            }
        })
    }

    private fun filter(text: String) {
        val searchText = text.lowercase(Locale.getDefault())
        filteredLostList.clear()

        if (searchText.isEmpty()) {
            filteredLostList.addAll(fullLostList)
        } else {
            for (item in fullLostList) {
                if (item.name?.lowercase(Locale.getDefault())?.contains(searchText) == true ||
                    item.location?.lowercase(Locale.getDefault())?.contains(searchText) == true ||
                    item.description?.lowercase(Locale.getDefault())?.contains(searchText) == true) {
                    filteredLostList.add(item)
                }
            }
        }
        adapter.updateList(filteredLostList)
    }

    override fun onStart() {
        super.onStart()
        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                fullLostList.clear()
                for (itemSnapshot in snapshot.children) {
                    try {
                        val lostItem = itemSnapshot.getValue(LostItem::class.java)
                        if (lostItem != null) {
                            fullLostList.add(lostItem)
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@Lost, "Error parsing data: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
                // Initially, display the full list
                filter("")
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@Lost, "Failed to load data: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
        dbRef.addValueEventListener(valueEventListener!!)
    }

    override fun onStop() {
        super.onStop()
        valueEventListener?.let {
            dbRef.removeEventListener(it)
        }
    }
}
