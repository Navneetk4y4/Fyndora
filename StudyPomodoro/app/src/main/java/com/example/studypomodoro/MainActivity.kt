package com.example.studypomodoro

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private enum class TimerState {
        STOPPED,
        PAUSED,
        RUNNING
    }

    private enum class TimerMode {
        FOCUS,
        SHORT_BREAK,
        LONG_BREAK
    }

    private var timerState = TimerState.STOPPED
    private var timerMode = TimerMode.FOCUS

    private var timer: CountDownTimer? = null
    private var timerLengthSeconds = 0L
    private var secondsRemaining = 0L
    private var pomodorosCompleted = 0
    private var taskName: String? = null

    private lateinit var timerTextView: TextView
    private lateinit var modeTextView: TextView
    private lateinit var taskNameTextView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var startButton: Button
    private lateinit var pauseButton: Button
    private lateinit var resetButton: Button
    private lateinit var pomodorosCompletedTextView: TextView
    private lateinit var endSessionButton: Button
    private lateinit var statisticsButton: Button
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        taskName = intent.getStringExtra("TASK_NAME")

        timerTextView = findViewById(R.id.tv_timer)
        modeTextView = findViewById(R.id.tv_mode)
        taskNameTextView = findViewById(R.id.tv_task_name)
        progressBar = findViewById(R.id.progress_bar)
        startButton = findViewById(R.id.btn_start)
        pauseButton = findViewById(R.id.btn_pause)
        resetButton = findViewById(R.id.btn_reset)
        pomodorosCompletedTextView = findViewById(R.id.tv_pomodoros_completed)
        endSessionButton = findViewById(R.id.btn_end_session)
        statisticsButton = findViewById(R.id.btn_statistics)
        toolbar = findViewById(R.id.toolbar_main)

        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
        taskNameTextView.text = taskName

        startButton.setOnClickListener {
            startTimer()
        }

        pauseButton.setOnClickListener {
            pauseTimer()
        }

        resetButton.setOnClickListener {
            resetTimer()
        }

        endSessionButton.setOnClickListener {
            finish()
        }

        statisticsButton.setOnClickListener {
            startActivity(Intent(this, StatisticsActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        initTimer()
    }

    private fun initTimer() {
        setTimerLength()
        secondsRemaining = timerLengthSeconds
        updateTimerText()
        updateButtons()
        updateProgressBar()
    }

    private fun startTimer() {
        timerState = TimerState.RUNNING

        timer = object : CountDownTimer(secondsRemaining * 1000, 1000) {
            override fun onFinish() = onTimerFinished()

            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining = millisUntilFinished / 1000
                updateTimerText()
                updateProgressBar()
            }
        }.start()

        updateButtons()
    }

    private fun pauseTimer() {
        timerState = TimerState.PAUSED
        timer?.cancel()
        updateButtons()
    }

    private fun resetTimer() {
        timer?.cancel()
        initTimer()
        timerState = TimerState.STOPPED
        updateButtons()
    }

    private fun onTimerFinished() {
        timerState = TimerState.STOPPED

        if (timerMode == TimerMode.FOCUS) {
            pomodorosCompleted++
            saveStats(pomodorosCompleted, (timerLengthSeconds / 60).toInt())
            pomodorosCompletedTextView.text = "Pomodoros Completed: $pomodorosCompleted"

            timerMode = if (pomodorosCompleted % 4 == 0) TimerMode.LONG_BREAK else TimerMode.SHORT_BREAK
            showBreakDialog()
        } else {
            timerMode = TimerMode.FOCUS
        }

        initTimer()
    }

    private fun showBreakDialog() {
        val breakType = if (timerMode == TimerMode.LONG_BREAK) "long" else "short"
        val breakDuration = getTimerLength(timerMode) / 60

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Session Completed!")
        builder.setMessage("Take a $breakDuration-minute $breakType break?")
        builder.setPositiveButton("Yes") { dialog, which ->
            startTimer()
        }
        builder.setNegativeButton("No") { dialog, which ->
            // Do nothing, timer is already stopped
        }
        builder.show()
    }

    private fun setTimerLength() {
        timerLengthSeconds = getTimerLength(timerMode)
    }

    private fun getTimerLength(mode: TimerMode): Long {
        val sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)
        return when (mode) {
            TimerMode.FOCUS -> sharedPreferences.getInt("focus_duration", 25) * 60L
            TimerMode.SHORT_BREAK -> sharedPreferences.getInt("break_duration", 5) * 60L
            TimerMode.LONG_BREAK -> sharedPreferences.getInt("long_break_duration", 15) * 60L
        }
    }

    private fun updateTimerText() {
        val minutes = secondsRemaining / 60
        val seconds = secondsRemaining % 60
        timerTextView.text = String.format("%02d:%02d", minutes, seconds)
        modeTextView.text = when(timerMode) {
            TimerMode.FOCUS -> "Focus Mode:"
            TimerMode.SHORT_BREAK -> "Short Break"
            TimerMode.LONG_BREAK -> "Long Break"
        }
    }

    private fun updateProgressBar() {
        if (timerLengthSeconds > 0) {
            progressBar.progress = (secondsRemaining * 100 / timerLengthSeconds).toInt()
        }
    }

    private fun updateButtons() {
        when (timerState) {
            TimerState.RUNNING -> {
                startButton.visibility = Button.GONE
                pauseButton.visibility = Button.VISIBLE
                resetButton.visibility = Button.VISIBLE
            }
            TimerState.STOPPED -> {
                startButton.visibility = Button.VISIBLE
                pauseButton.visibility = Button.GONE
                resetButton.visibility = Button.GONE
            }
            TimerState.PAUSED -> {
                startButton.visibility = Button.VISIBLE
                pauseButton.visibility = Button.GONE
                resetButton.visibility = Button.VISIBLE
            }
        }
    }

    private fun saveStats(pomodoros: Int, studyTime: Int) {
        val sharedPreferences = getSharedPreferences("stats", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("total_pomodoros", pomodoros)
        val totalStudyTime = sharedPreferences.getInt("total_study_time", 0) + studyTime
        editor.putInt("total_study_time", totalStudyTime)
        editor.apply()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            R.id.action_logout -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
