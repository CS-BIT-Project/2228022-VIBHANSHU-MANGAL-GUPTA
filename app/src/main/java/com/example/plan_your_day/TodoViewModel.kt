package com.example.plan_your_day

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Date

class TodoViewModel : ViewModel() {
    private val _tasks = MutableLiveData<List<Task>>()
    val tasks: LiveData<List<Task>> = _tasks

    fun addTask(title: String, dueDate: Date, priority: Priority) {
        val newTask = Task(title = title, dueDate = dueDate, priority = priority)
        val currentList = _tasks.value.orEmpty().toMutableList()
        currentList.add(newTask)
        _tasks.value = currentList.sortedBy { it.dueDate }
    }

    fun deleteTask(task: Task) {
        val currentList = _tasks.value.orEmpty().toMutableList()
        currentList.remove(task)
        _tasks.value = currentList
    }

    fun toggleTaskCompletion(task: Task) {
        val currentList = _tasks.value.orEmpty().toMutableList()
        val index = currentList.indexOfFirst { it.id == task.id }
        if (index != -1) {
            currentList[index] = task.copy(isCompleted = !task.isCompleted)
            _tasks.value = currentList
        }
    }
}