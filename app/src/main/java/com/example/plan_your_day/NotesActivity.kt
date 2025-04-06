package com.example.plan_your_day

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.plan_your_day.databinding.ActivityNotesBinding
import com.example.plan_your_day.databinding.DialogAddEditNoteBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class NotesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotesBinding
    private lateinit var viewModel: NotesViewModel
    private lateinit var adapter: NotesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(NotesViewModel::class.java)


        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = NotesAdapter { note -> viewModel.deleteNote(note) }
        binding.notesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.notesRecyclerView.adapter = adapter
    }

    private fun setupListeners() {
        binding.backButton.setOnClickListener { onBackPressed() }
        binding.addNoteFab.setOnClickListener { showAddEditNoteDialog() }
    }

    private fun observeViewModel() {
        viewModel.notes.observe(this) { notes ->
            adapter.submitList(notes)
            updateEmptyState(notes.isEmpty())
        }
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            binding.emptyStateImageView.visibility = android.view.View.VISIBLE
            binding.emptyStateTextView.visibility = android.view.View.VISIBLE
            binding.notesRecyclerView.visibility = android.view.View.GONE
        } else {
            binding.emptyStateImageView.visibility = android.view.View.GONE
            binding.emptyStateTextView.visibility = android.view.View.GONE
            binding.notesRecyclerView.visibility = android.view.View.VISIBLE
        }
    }

    private fun showAddEditNoteDialog(note: Note? = null) {
        val dialogBinding = DialogAddEditNoteBinding.inflate(layoutInflater)
        val dialog = MaterialAlertDialogBuilder(this)
            .setView(dialogBinding.root)
            .create()

        dialogBinding.dialogTitleTextView.text = if (note == null) "Add New Note" else "Edit Note"
        dialogBinding.noteTitleEditText.setText(note?.title ?: "")
        dialogBinding.noteContentEditText.setText(note?.content ?: "")

        dialogBinding.saveButton.setOnClickListener {
            val title = dialogBinding.noteTitleEditText.text.toString()
            val content = dialogBinding.noteContentEditText.text.toString()
            if (title.isNotBlank() && content.isNotBlank()) {
                if (note == null) {
                    viewModel.addNote(title, content)
                } else {
                    // Implement edit functionality if needed
                }
                dialog.dismiss()
            }
        }

        dialogBinding.cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}