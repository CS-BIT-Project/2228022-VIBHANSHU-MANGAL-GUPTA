package com.example.plan_your_day

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plan_your_day.databinding.ActivityTodoBinding
import java.util.*

class TodoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTodoBinding
    private lateinit var viewModel: TodoViewModel
    private lateinit var adapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTodoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(TodoViewModel::class.java)

        setupUI()
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupUI() {
        binding.backButton.setOnClickListener { onBackPressed() }

        binding.addTaskButton.setOnClickListener {
            val title = binding.taskInput.text.toString().trim()
            if (title.isNotEmpty()) {
                showDateTimePicker { dateTime ->
                    val priority = when (binding.prioritySpinner.selectedItemPosition) {
                        0 -> Priority.HIGH
                        1 -> Priority.MEDIUM
                        else -> Priority.LOW
                    }
                    viewModel.addTask(title, dateTime, priority)
                    binding.taskInput.text.clear()
                }
            }
        }

        binding.deadlineButton.setOnClickListener {
            showDateTimePicker { _ -> }
        }

        val priorityAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.priorities,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        binding.prioritySpinner.adapter = priorityAdapter
    }

    private fun setupRecyclerView() {
        adapter = TaskAdapter(
            onDeleteClick = { task -> viewModel.deleteTask(task) },
            onTaskToggle = { task -> viewModel.toggleTaskCompletion(task) }
        )
        binding.taskRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.taskRecyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.tasks.observe(this) { tasks ->
            adapter.submitList(tasks)
            updateEmptyState(tasks.isEmpty())
        }
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            binding.emptyState.visibility = android.view.View.VISIBLE
            binding.taskRecyclerView.visibility = android.view.View.GONE
        } else {
            binding.emptyState.visibility = android.view.View.GONE
            binding.taskRecyclerView.visibility = android.view.View.VISIBLE
        }
    }

    private fun showDateTimePicker(callback: (Date) -> Unit) {
        val currentDateTime = Calendar.getInstance()
        val startYear = currentDateTime.get(Calendar.YEAR)
        val startMonth = currentDateTime.get(Calendar.MONTH)
        val startDay = currentDateTime.get(Calendar.DAY_OF_MONTH)
        val startHour = currentDateTime.get(Calendar.HOUR_OF_DAY)
        val startMinute = currentDateTime.get(Calendar.MINUTE)

        DatePickerDialog(this, { _, year, month, day ->
            val pickedDate = Calendar.getInstance()
            pickedDate.set(year, month, day, 0, 0, 0) // Reset time to 00:00:00

            callback(pickedDate.time) // Return date without time
        }, startYear, startMonth, startDay).show()
    }

}