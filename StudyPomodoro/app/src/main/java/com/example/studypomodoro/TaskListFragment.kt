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

class TaskListFragment : Fragment() {

    private lateinit var projectsRecyclerView: RecyclerView
    private lateinit var greetingTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_task_list, container, false)

        projectsRecyclerView = view.findViewById(R.id.rv_projects)
        greetingTextView = view.findViewById(R.id.tv_greeting)

        // Setup Projects RecyclerView
        projectsRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val projects = listOf(
            Project(R.drawable.ic_study_tasks, "10 tasks", "Study Tasks"),
            Project(R.drawable.ic_revision_tasks, "6 tasks", "Revision Tasks"),
            Project(R.drawable.ic_coding_practice, "12 tasks", "Coding Practice"),
            Project(R.drawable.ic_reading_notes, "5 tasks", "Reading / Notes")
        )
        projectsRecyclerView.adapter = ProjectsAdapter(projects)

        loadAndDisplayUserName()

        return view
    }

    private fun loadAndDisplayUserName() {
        val sharedPreferences = requireActivity().getSharedPreferences("profile", Context.MODE_PRIVATE)
        val userName = sharedPreferences.getString("USER_NAME", "David King")
        greetingTextView.text = "Hi $userName"
    }
}