package com.example.studypomodoro

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class StatisticsActivity : AppCompatActivity() {

    private lateinit var totalPomodorosTextView: TextView
    private lateinit var totalStudyTimeTextView: TextView
    private lateinit var tasksCompletedTextView: TextView
    private lateinit var backToTaskListButton: Button
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        totalPomodorosTextView = findViewById(R.id.tv_total_pomodoros)
        totalStudyTimeTextView = findViewById(R.id.tv_total_study_time)
        tasksCompletedTextView = findViewById(R.id.tv_tasks_completed)
        backToTaskListButton = findViewById(R.id.btn_back_to_task_list)
        toolbar = findViewById(R.id.toolbar_statistics)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        loadStats()

        backToTaskListButton.setOnClickListener {
            finish()
        }
    }

    private fun loadStats() {
        val sharedPreferences = getSharedPreferences("stats", Context.MODE_PRIVATE)
        val totalPomodoros = sharedPreferences.getInt("total_pomodoros", 0)
        val totalStudyTime = sharedPreferences.getInt("total_study_time", 0)
        val tasksCompleted = sharedPreferences.getInt("tasks_completed", 0)

        val hours = totalStudyTime / 3600
        val minutes = (totalStudyTime % 3600) / 60

        tasksCompletedTextView.text = "Tasks completed today: $tasksCompleted"
        totalPomodorosTextView.text = "Total Pomodoros: $totalPomodoros"
        totalStudyTimeTextView.text = "Total Focus Time: $hours hours"
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
