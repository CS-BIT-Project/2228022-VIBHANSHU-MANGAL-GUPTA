package com.example.plan_your_day

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.plan_your_day.databinding.ItemTaskBinding
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapter(
    private val onDeleteClick: (Task) -> Unit,
    private val onTaskToggle: (Task) -> Unit
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TaskViewHolder(private val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task) {
            binding.taskTitleTextView.text = task.title
            binding.taskDueDateTextView.text = formatDate(task.dueDate)
            binding.taskCheckBox.isChecked = task.isCompleted

            binding.taskCheckBox.setOnCheckedChangeListener { _, isChecked ->
                onTaskToggle(task.copy(isCompleted = isChecked))
            }

            binding.deleteTaskButton.setOnClickListener {
                onDeleteClick(task)
            }

            val borderColor = when (task.priority) {
                Priority.HIGH -> ContextCompat.getColor(binding.root.context, R.color.red_500)
                Priority.MEDIUM -> ContextCompat.getColor(binding.root.context, R.color.yellow_300)
                Priority.LOW -> ContextCompat.getColor(binding.root.context, R.color.green_500)
            }
            val backgroundDrawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                setStroke(6, borderColor) // 6dp border
                cornerRadius = 16f // Rounded corners for better UI
            }

            binding.root.background = backgroundDrawable

        }

        private fun formatDate(date: Date): String {
            val sdf = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
            return sdf.format(date)
        }
    }
}

class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem == newItem
    }
}