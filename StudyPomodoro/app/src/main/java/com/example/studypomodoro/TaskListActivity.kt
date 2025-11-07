package com.example.studypomodoro

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TaskListActivity : AppCompatActivity() {

    private lateinit var tasksListView: ListView
    private lateinit var addTaskButton: FloatingActionButton
    private lateinit var startPomodoroButton: Button
    private lateinit var toolbar: Toolbar

    private lateinit var tasks: ArrayList<String>
    private lateinit var adapter: ArrayAdapter<String>

    private var selectedTask: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        tasksListView = findViewById(R.id.lv_tasks)
        addTaskButton = findViewById(R.id.fab_add_task)
        startPomodoroButton = findViewById(R.id.btn_start_pomodoro)
        toolbar = findViewById(R.id.toolbar_task_list)

        setSupportActionBar(toolbar)
        supportActionBar?.title = ""

        tasks = loadTasks()

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, tasks)
        tasksListView.adapter = adapter

        tasksListView.setOnItemClickListener { parent, view, position, id ->
            selectedTask = tasks[position]
        }

        addTaskButton.setOnClickListener {
            showAddTaskDialog()
        }

        startPomodoroButton.setOnClickListener {
            if (selectedTask != null) {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("TASK_NAME", selectedTask)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please select a task", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showAddTaskDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add a new task")

        val input = EditText(this)
        input.hint = "Enter your task"
        builder.setView(input)

        builder.setPositiveButton("Add") { dialog, which ->
            val task = input.text.toString().trim()
            if (task.isNotEmpty()) {
                tasks.add(task)
                saveTasks(tasks)
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(this, "Please enter a task", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }

        builder.show()
    }

    private fun saveTasks(tasks: ArrayList<String>) {
        val sharedPreferences = getSharedPreferences("tasks", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putStringSet("task_list", tasks.toSet())
        editor.apply()
    }

    private fun loadTasks(): ArrayList<String> {
        val sharedPreferences = getSharedPreferences("tasks", Context.MODE_PRIVATE)
        val taskSet = sharedPreferences.getStringSet("task_list", emptySet())
        return ArrayList(taskSet ?: emptySet())
    }
}
