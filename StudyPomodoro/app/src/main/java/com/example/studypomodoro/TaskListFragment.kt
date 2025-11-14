package com.example.studypomodoro

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar

class TaskListFragment : Fragment() {

    private lateinit var greetingTextView: TextView
    private lateinit var tasksRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_task_list, container, false)

        greetingTextView = view.findViewById(R.id.tv_greeting)
        tasksRecyclerView = view.findViewById(R.id.rv_today_tasks)

        loadAndDisplayUserName()
        setupTasksRecyclerView()

        return view
    }

    override fun onResume() {
        super.onResume()
        // Refresh the task list every time the dashboard is shown
        setupTasksRecyclerView()
    }

    private fun loadAndDisplayUserName() {
        val sharedPreferences = requireActivity().getSharedPreferences("profile", Context.MODE_PRIVATE)
        val userName = sharedPreferences.getString("USER_NAME", "David King")
        greetingTextView.text = "Hi $userName"
    }

    private fun setupTasksRecyclerView() {
        val today = Calendar.getInstance()
        val todaysTasks = TaskRepository.allTasks.filter {
            it.date.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
            it.date.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
        }

        tasksRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        tasksRecyclerView.adapter = DashboardTasksAdapter(todaysTasks)
    }
}