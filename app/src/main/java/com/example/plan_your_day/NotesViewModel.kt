package com.example.plan_your_day

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NotesViewModel : ViewModel() {
    private val _notes = MutableLiveData<List<Note>>()
    val notes: LiveData<List<Note>> = _notes

    init {
        _notes.value = emptyList() // Explicitly set empty list to trigger observer
    }

    fun addNote(title: String, content: String) {
        val newNote = Note(title = title, content = content)
        _notes.value = _notes.value?.plus(newNote) ?: listOf(newNote)
    }

    fun deleteNote(note: Note) {
        _notes.value = _notes.value?.filter { it.id != note.id }
    }
}