package com.example.plan_your_day

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class NotesViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences = application.getSharedPreferences("notes_pref", Context.MODE_PRIVATE)
    private val gson = Gson()

    private val _notes = MutableLiveData<List<Note>>()
    val notes: LiveData<List<Note>> = _notes

    init {
        loadNotes()
    }

    fun addNote(title: String, content: String) {
        val newNote = Note(title = title, content = content)
        val updatedNotes = _notes.value?.plus(newNote) ?: listOf(newNote)
        _notes.value = updatedNotes
        saveNotes(updatedNotes)
    }

    fun deleteNote(note: Note) {
        val updatedNotes = _notes.value?.filter { it.id != note.id } ?: emptyList()
        _notes.value = updatedNotes
        saveNotes(updatedNotes)
    }

    private fun saveNotes(notes: List<Note>) {
        val json = gson.toJson(notes)
        sharedPreferences.edit().putString("notes_list", json).apply()
    }

    private fun loadNotes() {
        val json = sharedPreferences.getString("notes_list", null)
        val type = object : TypeToken<List<Note>>() {}.type
        _notes.value = if (json != null) gson.fromJson(json, type) else emptyList()
    }
}
