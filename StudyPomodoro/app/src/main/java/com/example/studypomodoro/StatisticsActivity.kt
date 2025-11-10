package com.example.studypomodoro

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class StatisticsActivity : AppCompatActivity() {

    private lateinit var totalPomodorosTextView: TextView
    private lateinit var totalStudyTimeTextView: TextView
    private lateinit var backToTaskListButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        totalPomodorosTextView = findViewById(R.id.tv_total_pomodoros)
        totalStudyTimeTextView = findViewById(R.id.tv_total_study_time)
        backToTaskListButton = findViewById(R.id.btn_back_to_task_list)

        loadStats()

        backToTaskListButton.setOnClickListener {
            finish()
        }
    }

    private fun loadStats() {
        val sharedPreferences = getSharedPreferences("stats", Context.MODE_PRIVATE)
        val totalPomodoros = sharedPreferences.getInt("total_pomodoros", 0)
        val totalStudyTime = sharedPreferences.getInt("total_study_time", 0)

        val hours = totalStudyTime / 60
        val minutes = totalStudyTime % 60

        totalPomodorosTextView.text = "Total Pomodoros today: $totalPomodoros"
        totalStudyTimeTextView.text = "Total study time: $hours hrs $minutes mins"
    }

    private fun resetStats() {
        val sharedPreferences = getSharedPreferences("stats", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}
