package com.example.plan_your_day

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PackingListViewModel : ViewModel() {
    private val _packingItems = MutableLiveData<List<PackingItem>>()
    val packingItems: LiveData<List<PackingItem>> = _packingItems

    private val _packingStatus = MutableLiveData<String>()
    val packingStatus: LiveData<String> = _packingStatus

    init {
        _packingItems.value = listOf(
            PackingItem("Citizenship ID"),
            PackingItem("Phone Charger"),
            PackingItem("Headphones", true),
            PackingItem("Camera"),
            PackingItem("First Aid Kit"),
            PackingItem("ToothBrush", true)
        )
        updatePackingStatus()
    }

    fun addItem(itemName: String) {
        if (itemName.isNotEmpty()) {
            val currentList = _packingItems.value.orEmpty().toMutableList()
            currentList.add(PackingItem(itemName))
            _packingItems.value = currentList
            updatePackingStatus()
        }
    }

    fun clearPacked() {
        _packingItems.value = _packingItems.value.orEmpty().filter { !it.isPacked }
        updatePackingStatus()
    }

    fun clearAll() {
        _packingItems.value = emptyList()
        updatePackingStatus()
    }

    fun toggleItemPacked(item: PackingItem) {
        val currentList = _packingItems.value.orEmpty().toMutableList()
        val index = currentList.indexOfFirst { it.name == item.name }
        if (index != -1) {
            currentList[index] = item.copy(isPacked = !item.isPacked)
            _packingItems.value = currentList
            updatePackingStatus()
        }
    }

    private fun updatePackingStatus() {
        val totalItems = _packingItems.value?.size ?: 0
        val packedItems = _packingItems.value?.count { it.isPacked } ?: 0
        _packingStatus.value = "$packedItems/$totalItems items packed"
    }
}

data class PackingItem(val name: String, var isPacked: Boolean = false)