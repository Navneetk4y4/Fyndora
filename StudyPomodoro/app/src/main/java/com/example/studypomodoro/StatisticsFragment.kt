package com.example.studypomodoro

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class StatisticsFragment : Fragment() {

    private lateinit var totalPomodorosTextView: TextView
    private lateinit var totalStudyTimeTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_statistics, container, false)

        totalPomodorosTextView = view.findViewById(R.id.tv_total_pomodoros)
        totalStudyTimeTextView = view.findViewById(R.id.tv_total_study_time)

        loadStats()

        return view
    }

    private fun loadStats() {
        val sharedPreferences = requireActivity().getSharedPreferences("stats", Context.MODE_PRIVATE)
        val totalPomodoros = sharedPreferences.getInt("total_pomodoros", 0)
        val totalStudyTime = sharedPreferences.getInt("total_study_time", 0)

        val hours = totalStudyTime / 60
        val minutes = totalStudyTime % 60

        totalPomodorosTextView.text = "Total Pomodoros today: $totalPomodoros"
        totalStudyTimeTextView.text = "Total study time: $hours hrs $minutes mins"
    }
}