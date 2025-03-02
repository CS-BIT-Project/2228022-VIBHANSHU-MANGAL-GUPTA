package com.example.plan_your_day

import java.util.Date

data class Task(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val dueDate: Date,
    val priority: Priority,
    var isCompleted: Boolean = false
)

enum class Priority {
    HIGH, MEDIUM, LOW
}
