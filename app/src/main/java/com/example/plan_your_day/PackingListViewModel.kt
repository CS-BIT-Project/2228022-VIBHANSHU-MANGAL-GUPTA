package com.example.plan_your_day

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PackingListViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPreferences = application.getSharedPreferences("PackingListPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    private val _packingItems = MutableLiveData<List<PackingItem>>()
    val packingItems: LiveData<List<PackingItem>> = _packingItems

    private val _packingStatus = MutableLiveData<String>()
    val packingStatus: LiveData<String> = _packingStatus

    init {
        loadPackingList()
        updatePackingStatus()
    }

    fun addItem(itemName: String) {
        if (itemName.isNotEmpty()) {
            val currentList = _packingItems.value.orEmpty().toMutableList()
            currentList.add(PackingItem(itemName))
            _packingItems.value = currentList
            savePackingList()
            updatePackingStatus()
        }
    }

    fun clearPacked() {
        _packingItems.value = _packingItems.value.orEmpty().filter { !it.isPacked }
        savePackingList()
        updatePackingStatus()
    }

    fun clearAll() {
        _packingItems.value = emptyList()
        savePackingList()
        updatePackingStatus()
    }

    fun toggleItemPacked(item: PackingItem) {
        val currentList = _packingItems.value.orEmpty().toMutableList()
        val index = currentList.indexOfFirst { it.name == item.name }
        if (index != -1) {
            currentList[index] = item.copy(isPacked = !item.isPacked)
            _packingItems.value = currentList
            savePackingList()
            updatePackingStatus()
        }
    }

    private fun updatePackingStatus() {
        val totalItems = _packingItems.value?.size ?: 0
        val packedItems = _packingItems.value?.count { it.isPacked } ?: 0
        _packingStatus.value = "$packedItems/$totalItems items packed"
    }

    private fun savePackingList() {
        val editor = sharedPreferences.edit()
        val json = gson.toJson(_packingItems.value)
        editor.putString("packing_list", json)
        editor.apply()
    }

    private fun loadPackingList() {
        val json = sharedPreferences.getString("packing_list", null)
        val type = object : TypeToken<List<PackingItem>>() {}.type
        _packingItems.value = gson.fromJson(json, type) ?: emptyList()
    }
}
