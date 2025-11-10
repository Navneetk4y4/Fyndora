package com.example.studypomodoro

import java.util.Calendar

data class Project(val icon: Int, val taskCount: String, val title: String)
data class Task(var status: Int, var name: String, val projectName: String, val time: String, val date: Calendar)
