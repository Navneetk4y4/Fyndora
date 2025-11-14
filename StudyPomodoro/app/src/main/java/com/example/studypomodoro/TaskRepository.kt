package com.example.studypomodoro

import java.util.Calendar

object TaskRepository {
    val allTasks = mutableListOf<Task>()

    init {
        createSampleTasks()
    }

    private fun createSampleTasks() {
        val today = Calendar.getInstance()
        val tomorrow = Calendar.getInstance()
        tomorrow.add(Calendar.DAY_OF_YEAR, 1)

        allTasks.add(Task(R.drawable.ic_task_status_completed_new, "Finish UI mockups", "Work", "10:00 am", today))
        allTasks.add(Task(R.drawable.ic_task_status_in_progress_new, "Review code backend", "Work", "2:00 pm", today))
        allTasks.add(Task(R.drawable.ic_task_status_pending, "Test AbuseIPDB integration", "Work", "4:00 pm", today))
        allTasks.add(Task(R.drawable.ic_task_status_pending, "Plan weekend trip", "Personal", "6:00 pm", tomorrow))
    }
}