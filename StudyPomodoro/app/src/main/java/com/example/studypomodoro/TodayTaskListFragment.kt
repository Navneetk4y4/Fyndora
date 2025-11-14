package com.example.studypomodoro

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TodayTaskListFragment : Fragment() {

    private lateinit var weekDaysLayout: LinearLayout
    private lateinit var tasksRecyclerView: RecyclerView
    private lateinit var addTaskButton: FloatingActionButton
    private lateinit var monthYearTextView: TextView
    private lateinit var searchEditText: EditText
    private lateinit var searchIcon: ImageView
    private lateinit var tasksAdapter: TasksAdapter
    private val selectedDate = Calendar.getInstance()

    private val taskDetailLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val taskName = result.data?.getStringExtra("COMPLETED_TASK_NAME")
            if (taskName != null) {
                val task = TaskRepository.allTasks.find { it.name == taskName }
                task?.status = R.drawable.ic_task_status_completed_new
                filterTasks(selectedDate)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_today_task_list, container, false)

        weekDaysLayout = view.findViewById(R.id.ll_week_days)
        tasksRecyclerView = view.findViewById(R.id.rv_tasks)
        addTaskButton = view.findViewById(R.id.fab_add_task)
        monthYearTextView = view.findViewById(R.id.tv_month_year)
        searchEditText = view.findViewById(R.id.et_search)
        searchIcon = view.findViewById(R.id.iv_search)

        updateMonthYearTextView()
        setupWeekView()
        setupTasksRecyclerView()

        monthYearTextView.setOnClickListener {
            showDatePickerDialog()
        }

        searchIcon.setOnClickListener {
            searchEditText.visibility = if (searchEditText.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterTasks(selectedDate, s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        addTaskButton.setOnClickListener {
            showAddTaskDialog()
        }

        return view
    }

    private fun updateMonthYearTextView() {
        val monthYearFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        monthYearTextView.text = monthYearFormat.format(selectedDate.time)
    }

    private fun setupWeekView() {
        weekDaysLayout.removeAllViews()
        val calendar = selectedDate.clone() as Calendar
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val month = calendar.get(Calendar.MONTH)

        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
        val dateFormat = SimpleDateFormat("dd", Locale.getDefault())

        while (calendar.get(Calendar.MONTH) == month) {
            val dayView = layoutInflater.inflate(R.layout.item_date, weekDaysLayout, false)
            val dayOfWeek = dayView.findViewById<TextView>(R.id.tv_day_of_week)
            val dateNumber = dayView.findViewById<TextView>(R.id.tv_date_number)

            dayOfWeek.text = dayFormat.format(calendar.time)
            dateNumber.text = dateFormat.format(calendar.time)

            val date = calendar.clone() as Calendar
            dayView.setOnClickListener {
                selectedDate.time = date.time
                setupWeekView()
                filterTasks(selectedDate)
            }

            if (calendar.get(Calendar.DAY_OF_YEAR) == selectedDate.get(Calendar.DAY_OF_YEAR) && calendar.get(Calendar.YEAR) == selectedDate.get(Calendar.YEAR)) {
                dayView.isSelected = true
            }

            weekDaysLayout.addView(dayView)
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
    }

    private fun setupTasksRecyclerView() {
        tasksRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        tasksAdapter = TasksAdapter(mutableListOf()) { task ->
            val intent = Intent(requireContext(), TaskDetailActivity::class.java)
            intent.putExtra("TASK_NAME", task.name)
            intent.putExtra("TASK_CATEGORY", task.projectName)
            taskDetailLauncher.launch(intent)
        }
        tasksRecyclerView.adapter = tasksAdapter
        filterTasks(selectedDate)
    }

    private fun filterTasks(date: Calendar, query: String? = null) {
        var filteredTasks = TaskRepository.allTasks.filter { 
            it.date.get(Calendar.YEAR) == date.get(Calendar.YEAR) && 
            it.date.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR) 
        }
        if (!query.isNullOrEmpty()) {
            filteredTasks = filteredTasks.filter { it.name.contains(query, ignoreCase = true) }
        }
        tasksAdapter.updateTasks(filteredTasks)
    }

    private fun showDatePickerDialog() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            selectedDate.set(Calendar.YEAR, year)
            selectedDate.set(Calendar.MONTH, monthOfYear)
            selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateMonthYearTextView()
            setupWeekView()
            filterTasks(selectedDate)
        }

        DatePickerDialog(
            requireContext(),
            dateSetListener,
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showAddTaskDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.dialog_add_task, null)
        val taskNameEditText = dialogLayout.findViewById<EditText>(R.id.et_task_name)

        builder.setView(dialogLayout)
            .setPositiveButton("Add") { _, _ ->
                val taskName = taskNameEditText.text.toString()
                if (taskName.isNotEmpty()) {
                    TaskRepository.allTasks.add(Task(R.drawable.ic_task_status_pending, taskName, "General", "Now", selectedDate.clone() as Calendar))
                    filterTasks(selectedDate)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}