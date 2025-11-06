package com.example.lostandfound

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth

class Home : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        auth = FirebaseAuth.getInstance()

        // Setup Toolbar
        val topAppBar: MaterialToolbar = findViewById(R.id.topAppBar)
        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_settings -> {
                    showSettingsPopup(topAppBar)
                    true
                }
                else -> false
            }
        }

        // Setup Image Slider
        viewPager = findViewById(R.id.imageSlider)
        val images = listOf(
            R.drawable.img_1,
            R.drawable.img_2
        )
        val adapter = ImageSliderAdapter(images)
        viewPager.adapter = adapter

        // Setup Card Click Listeners
        val reportLostCard: MaterialCardView = findViewById(R.id.cardReportLost)
        reportLostCard.setOnClickListener {
            startActivity(Intent(this, Lost::class.java))
        }

        val foundItemCard: MaterialCardView = findViewById(R.id.cardFoundItem)
        foundItemCard.setOnClickListener {
            startActivity(Intent(this, Found::class.java))
        }

        val fabReportFraud: FloatingActionButton = findViewById(R.id.fabReportFraud)
        fabReportFraud.setOnClickListener {
            startActivity(Intent(this, ReportFraudActivity::class.java))
        }
    }

    private fun showSettingsPopup(anchor: MaterialToolbar) {
        val popup = PopupMenu(this, anchor)
        popup.menuInflater.inflate(R.menu.settings_popup, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                R.id.menu_logout -> {
                    auth.signOut()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finishAffinity()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }
}
