package com.example.studypomodoro

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)

        bottomNavigation.setOnItemSelectedListener { item ->
            var selectedFragment: Fragment? = null
            when (item.itemId) {
                R.id.navigation_dashboard -> selectedFragment = TaskListFragment()
                R.id.navigation_today_task_list -> selectedFragment = TodayTaskListFragment()
                R.id.navigation_soundscapes -> selectedFragment = SoundscapesFragment()
                R.id.navigation_music -> selectedFragment = MusicFragment()
                R.id.navigation_profile -> selectedFragment = ProfileFragment()
                R.id.navigation_statistics -> selectedFragment = StatisticsFragment()
            }
            if (selectedFragment != null) {
                supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, selectedFragment).commit()
            }
            true
        }

        // Set default fragment
        if (savedInstanceState == null) {
            bottomNavigation.selectedItemId = R.id.navigation_dashboard
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Stop the music service when the app is closed
        stopService(Intent(this, MusicService::class.java))
    }
}