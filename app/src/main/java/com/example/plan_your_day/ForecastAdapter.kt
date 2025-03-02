package com.example.plan_your_day

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.plan_your_day.databinding.ItemForecastBinding

class ForecastAdapter : ListAdapter<ForecastItem, ForecastAdapter.ViewHolder>(ForecastDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemForecastBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemForecastBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ForecastItem) {
            binding.apply {
                dayText.text = item.day
                dateText.text = item.date
                highTemp.text = "${item.highTemp}°"
                lowTemp.text = "${item.lowTemp}°"

                // Set weather icon based on weather condition
                weatherIcon.setImageResource(
                    when (item.weather) {
                        "sunny" -> R.drawable.ic_sunny
                        "cloudy" -> R.drawable.ic_cloudy
                        "rainy" -> R.drawable.ic_rainy
                        else -> R.drawable.ic_partly_cloudy
                    }
                )
            }
        }
    }
}

class ForecastDiffCallback : DiffUtil.ItemCallback<ForecastItem>() {
    override fun areItemsTheSame(oldItem: ForecastItem, newItem: ForecastItem): Boolean {
        return oldItem.date == newItem.date
    }

    override fun areContentsTheSame(oldItem: ForecastItem, newItem: ForecastItem): Boolean {
        return oldItem == newItem
    }
}