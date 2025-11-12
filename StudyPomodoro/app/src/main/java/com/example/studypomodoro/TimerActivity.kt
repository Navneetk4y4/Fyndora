package com.example.studypomodoro

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class TimerActivity : AppCompatActivity() {

    private enum class TimerState {
        STOPPED,
        PAUSED,
        RUNNING
    }

    private var timerState = TimerState.STOPPED
    private var timer: CountDownTimer? = null
    private var focusDurationMinutes = 25L
    private var breakDurationMinutes = 5L
    private var totalCycles = 1
    private var currentCycle = 0
    private var isFocusSession = true

    private var secondsRemaining = 0L
    private var taskName: String? = null

    private lateinit var taskNameTextView: TextView
    private lateinit var timerTextView: TextView
    private lateinit var sessionStatusTextView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var playPauseButton: ImageButton
    private lateinit var nextSessionButton: ImageButton
    private lateinit var markCompleteButton: Button
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        // --- View Initialization ---
        taskNameTextView = findViewById(R.id.tv_main_task_name)
        timerTextView = findViewById(R.id.tv_main_timer)
        sessionStatusTextView = findViewById(R.id.tv_main_session_status)
        progressBar = findViewById(R.id.progress_bar_main)
        playPauseButton = findViewById(R.id.btn_play_pause)
        nextSessionButton = findViewById(R.id.btn_next_session)
        markCompleteButton = findViewById(R.id.btn_mark_complete)
        toolbar = findViewById(R.id.toolbar_main)

        // --- Toolbar Setup ---
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // --- Get Data from Intent ---
        taskName = intent.getStringExtra("TASK_NAME")
        taskNameTextView.text = taskName
        focusDurationMinutes = intent.getIntExtra("FOCUS_DURATION", 25).toLong()
        breakDurationMinutes = intent.getIntExtra("BREAK_DURATION", 5).toLong()
        totalCycles = intent.getIntExtra("CYCLES", 1)

        // --- Button Listeners ---
        playPauseButton.setOnClickListener { 
            if (timerState == TimerState.RUNNING) pauseTimer() else startTimer()
        }

        nextSessionButton.setOnClickListener { 
            timer?.cancel()
            onTimerFinished(isSwitchingManually = true)
        }

        markCompleteButton.setOnClickListener { 
            val resultIntent = Intent()
            resultIntent.putExtra("COMPLETED_TASK_NAME", taskName)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        // --- Start First Session ---
        startNewFocusSession()
    }

    private fun startNewFocusSession() {
        currentCycle++
        isFocusSession = true
        sessionStatusTextView.text = "Focus"
        secondsRemaining = focusDurationMinutes * 60
        updateTimerText()
        updateProgressBar(focusDurationMinutes * 60)
        timerState = TimerState.STOPPED
        playPauseButton.setImageResource(R.drawable.ic_play_arrow)
    }

    private fun startTimer() {
        timerState = TimerState.RUNNING
        playPauseButton.setImageResource(R.drawable.ic_pause)

        timer = object : CountDownTimer(secondsRemaining * 1000, 1000) {
            override fun onFinish() = onTimerFinished(isSwitchingManually = false)

            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining = millisUntilFinished / 1000
                updateTimerText()
                val totalDuration = if (isFocusSession) focusDurationMinutes * 60 else breakDurationMinutes * 60
                updateProgressBar(totalDuration)
            }
        }.start()
    }

    private fun pauseTimer() {
        timerState = TimerState.PAUSED
        playPauseButton.setImageResource(R.drawable.ic_play_arrow)
        timer?.cancel()
    }

    private fun onTimerFinished(isSwitchingManually: Boolean) {
        timerState = TimerState.STOPPED
        playPauseButton.setImageResource(R.drawable.ic_play_arrow)

        if (isFocusSession) {
            // Finished a focus session, start a break
            isFocusSession = false
            sessionStatusTextView.text = "Break"
            secondsRemaining = breakDurationMinutes * 60
            updateProgressBar(breakDurationMinutes * 60)
        } else {
            // Finished a break session
            if (currentCycle < totalCycles) {
                startNewFocusSession()
            } else {
                // All cycles are complete
                finish()
            }
        }
        updateTimerText()

        // If we didn't switch manually (timer ran out), start the next session automatically.
        if (!isSwitchingManually) {
             startTimer()
        }
    }

    private fun updateTimerText() {
        val minutes = secondsRemaining / 60
        val seconds = secondsRemaining % 60
        timerTextView.text = String.format("%02d:%02d", minutes, seconds)
    }

    private fun updateProgressBar(totalDurationSeconds: Long) {
        if (totalDurationSeconds > 0) {
            progressBar.progress = (secondsRemaining * 100 / totalDurationSeconds).toInt()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel() // Prevent memory leaks
    }
}
