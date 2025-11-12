package com.example.studypomodoro

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class TaskDetailActivity : AppCompatActivity() {

    private lateinit var taskTitleTextView: TextView
    private lateinit var taskCategoryTextView: TextView
    private lateinit var focusDurationEditText: EditText
    private lateinit var breakDurationEditText: EditText
    private lateinit var cyclesEditText: EditText
    private lateinit var startPomodoroButton: Button
    private lateinit var toolbar: Toolbar

    private val clockLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        // This is the callback from TimerActivity
        if (result.resultCode == Activity.RESULT_OK) {
            // The task was marked complete. Pass the result back to the calling fragment.
            setResult(Activity.RESULT_OK, result.data)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail)

        taskTitleTextView = findViewById(R.id.tv_task_detail_title)
        taskCategoryTextView = findViewById(R.id.tv_task_detail_category)
        focusDurationEditText = findViewById(R.id.et_focus_duration)
        breakDurationEditText = findViewById(R.id.et_break_duration)
        cyclesEditText = findViewById(R.id.et_cycles)
        startPomodoroButton = findViewById(R.id.btn_start_pomodoro_session)
        toolbar = findViewById(R.id.toolbar_task_detail)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val taskName = intent.getStringExtra("TASK_NAME")
        val taskCategory = intent.getStringExtra("TASK_CATEGORY")

        taskTitleTextView.text = taskName
        taskCategoryTextView.text = taskCategory

        startPomodoroButton.setOnClickListener {
            val focusDuration = focusDurationEditText.text.toString().toIntOrNull() ?: 25
            val breakDuration = breakDurationEditText.text.toString().toIntOrNull() ?: 5
            val cycles = cyclesEditText.text.toString().toIntOrNull() ?: 1

            val intent = Intent(this, TimerActivity::class.java)
            intent.putExtra("TASK_NAME", taskName)
            intent.putExtra("FOCUS_DURATION", focusDuration)
            intent.putExtra("BREAK_DURATION", breakDuration)
            intent.putExtra("CYCLES", cycles)
            clockLauncher.launch(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
