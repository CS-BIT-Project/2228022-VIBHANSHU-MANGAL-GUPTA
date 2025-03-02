package com.example.plan_your_day

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.plan_your_day.databinding.ActivityPackingListBinding

class PackingListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPackingListBinding
    private lateinit var viewModel: PackingListViewModel
    private lateinit var adapter: PackingListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPackingListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(PackingListViewModel::class.java)

        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = PackingListAdapter(viewModel)
        binding.packingListRecyclerView.adapter = adapter
    }

    private fun setupListeners() {
        binding.addItemButton.setOnClickListener {
            val newItemText = binding.newItemEditText.text.toString().trim()
            if (newItemText.isNotEmpty()) {
                viewModel.addItem(newItemText)
                binding.newItemEditText.text?.clear()
            }
        }

        binding.clearPackedButton.setOnClickListener {
            viewModel.clearPacked()
        }

        binding.clearAllButton.setOnClickListener {
            viewModel.clearAll()
        }
    }

    private fun observeViewModel() {
        viewModel.packingItems.observe(this) { items ->
            adapter.submitList(items)
        }

        viewModel.packingStatus.observe(this) { status ->
            binding.packingStatusTextView.text = status
        }
    }
}